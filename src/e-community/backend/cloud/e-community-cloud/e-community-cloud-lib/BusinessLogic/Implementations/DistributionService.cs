using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.BusinessLogic.Interfaces.SignalR;
using e_community_cloud_lib.Database;
using e_community_cloud_lib.Database.Community;
using e_community_cloud_lib.Models.Distribution;
using e_community_cloud_lib.NonEntities;
using e_community_cloud_lib.Util;
using e_community_cloud_lib.Util.BusinessLogic;
using e_community_cloud_lib.Util.Enums;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace e_community_cloud_lib.BusinessLogic.Implementations {
    public class DistributionService : IDistributionService {

        private readonly ECommunityCloudContext mDb;
        private readonly IFCMService mFCMService;
        private readonly ILocalSignalRSenderService mLocalSignalRSenderService;
        private readonly IReplacementValueService mReplacementValueService;

        public DistributionService(ECommunityCloudContext _db, IFCMService _fcmService, ILocalSignalRSenderService _localSignalRSenderService, IReplacementValueService _replacementValueService) {
            mDb = _db;
            mFCMService = _fcmService;
            mLocalSignalRSenderService = _localSignalRSenderService;
            mReplacementValueService = _replacementValueService;
        }

        public async Task StartDistribution() {
            var timestamp = DateTime.UtcNow.AddMinutes(2 * Constants.DISTRIBUTION_MONITOR_INTERVAL_MINUTES);
            timestamp.AddSeconds(-timestamp.Second);
            timestamp.AddMilliseconds(-timestamp.Millisecond);

            var eCommunityMemberships = await mDb.ECommunityMembership
                .Include(x => x.ECommunity)
                .Include(x => x.Member)
                .ThenInclude(x => x.SmartMeters)
                .Where(x => x.ECommunity.DistributionMode == DistributionMode.Percentage && Constants.ACTIVE_MEMBER_PERMISSIONS.Contains(x.ECommunityPermission))
                .ToListAsync();

            var distributions = eCommunityMemberships
                .DistinctBy(x => x.ECommunityId)
                .Select(x => new ECommunityDistribution() {
                    ECommunityId = x.ECommunityId,
                    Timestamp = timestamp,
                    IsCurrent = true,
                    WasDistributed = false
                })
                .ToList();

            mDb.ECommunityDistribution.AddRange(distributions);
            await mDb.SaveChangesAsync();

            foreach (var distribution in distributions) {
                mDb.SmartMeterPortion.AddRange(eCommunityMemberships
                    .Where(x => x.ECommunityId == distribution.ECommunityId)
                    .SelectMany(x => x.Member.SmartMeters)
                    .Select(x => new SmartMeterPortion() {
                        ECommunityDistributionId = distribution.Id,
                        SmartMeterId = x.Id,
                        EstimatedActiveEnergyMinus = 0,
                        EstimatedActiveEnergyPlus = 0,
                        Deviation = 0,
                        Flexibility = 0,
                        ForecastFromSmartMeter = false,
                        Acknowledged = false
                    })
                );
            }
            await mDb.SaveChangesAsync();

            mLocalSignalRSenderService.RequestHourlyForecast();
        }

        public async Task ForecastArrived(ForecastModel _forecastModel) {
            var currentDistributions = await GetCurrentDistributions();

            if (currentDistributions.Count() > 0) {
                var smartMeterPortion = currentDistributions
                    .SelectMany(x => x.SmartMeterPortions)
                    .FirstOrDefault(x => x.SmartMeterId == _forecastModel.SmartMeterId);

                if (smartMeterPortion != null) {
                    smartMeterPortion.EstimatedActiveEnergyMinus = _forecastModel.ActiveEnergyMinus;
                    smartMeterPortion.EstimatedActiveEnergyPlus = _forecastModel.ActiveEnergyPlus;
                    smartMeterPortion.Flexibility = _forecastModel.Flexibility;
                    smartMeterPortion.ForecastFromSmartMeter = true;
                    await mDb.SaveChangesAsync();
                }
            }
        }

        public Task StartMonitorSession() {
            throw new NotImplementedException();
        }

        public async Task Distribute() {
            foreach (var distribution in await GetCurrentDistributions()) {
                await Distribute(distribution);
            }
        }

        public async Task Distribute(ECommunityDistribution _eCommunityDistribution) {
            var sumFeedIn = _eCommunityDistribution.SmartMeterPortions
                .Sum(x => x.EstimatedActiveEnergyMinus);

            var missingSmartMeters = _eCommunityDistribution.SmartMeterPortions.Count(x => !x.ForecastFromSmartMeter);
            if (missingSmartMeters > 0) {
                // for missing smart meter forecasts use replacement value
                _eCommunityDistribution.SmartMeterPortions
                    .Where(x => !x.ForecastFromSmartMeter)
                    .ToList()
                    .ForEach(x => {
                        var replacement = mReplacementValueService.GetReplacementValue(x);
                        if (replacement != null) {
                            x.EstimatedActiveEnergyPlus = replacement.EstimatedActiveEnergyPlus;
                            x.EstimatedActiveEnergyMinus = replacement.EstimatedActiveEnergyMinus;
                            x.Flexibility = replacement.Flexibility;
                        }
                    });
            }

            var sumConsumption = _eCommunityDistribution.SmartMeterPortions
                .Sum(x => x.EstimatedActiveEnergyPlus);
            var energyDifference = sumFeedIn - sumConsumption;

            if (energyDifference >= 0) {
                // more feed in than consumption (surplus)
                DistributeSurplus(_eCommunityDistribution.SmartMeterPortions, energyDifference);
            }
            else {
                // more consumption than feed in (shortage)
                DistributeShortage(_eCommunityDistribution.SmartMeterPortions, energyDifference, sumConsumption);
            }

            _eCommunityDistribution.WasDistributed = true;
            await mDb.SaveChangesAsync();
            if (sumFeedIn > Constants.DISTRIBUTION_MINIMUM_ENERGY_WH) {
                SendDistributionNotifications(_eCommunityDistribution.SmartMeterPortions, mFCMService.NewDistribution);
            }
        }

        private void DistributeSurplus(IList<SmartMeterPortion> _smartMeterPortions, int _energyDifference) {
            var sumFlexibilityPlus = _smartMeterPortions
                        .Where(x => x.Flexibility > 0)
                        .Sum(x => x.Flexibility);

            if (sumFlexibilityPlus > 0) {
                // members have positive flexibility
                if (_energyDifference < sumFlexibilityPlus) {
                    // difference < flexibility --> % increase
                    foreach (var smartMeterPortion in _smartMeterPortions) {
                        if (smartMeterPortion.Flexibility > 0) {
                            smartMeterPortion.Deviation = _energyDifference * smartMeterPortion.Flexibility / sumFlexibilityPlus;
                        }
                        else {
                            smartMeterPortion.Deviation = 0;
                        }
                    }
                }
                else {
                    // difference >= flexibility --> maybe more feed in than consumption (TODO)
                    foreach (var smartMeterPortion in _smartMeterPortions) {
                        if (smartMeterPortion.Flexibility > 0) {
                            smartMeterPortion.Deviation = smartMeterPortion.Flexibility;
                        }
                        else {
                            smartMeterPortion.Deviation = 0;
                        }
                    }
                }
            }
        }

        private void DistributeShortage(IList<SmartMeterPortion> _smartMeterPortions, int _energyDifference, int sumConsumption) {
            var sumFlexibilityMinus = -_smartMeterPortions
                        .Where(x => x.Flexibility < 0)
                        .Sum(x => x.Flexibility);

            if (sumFlexibilityMinus > 0) {
                // members have negative flexibility
                if (-_energyDifference <= sumFlexibilityMinus) {
                    // difference <= flexibility --> % decrease based on flexibility
                    foreach (var smartMeterPortion in _smartMeterPortions) {
                        if (smartMeterPortion.Flexibility < 0) {
                            smartMeterPortion.Deviation = -_energyDifference * smartMeterPortion.Flexibility / sumFlexibilityMinus;
                        }
                        else {
                            smartMeterPortion.Deviation = 0;
                        }
                    }
                }
                else {
                    // difference > flexibility --> flexibility unsatisfactory

                    // step 1: seperate evenly
                    foreach (var smartMeterPortion in _smartMeterPortions) {
                        smartMeterPortion.Deviation = _energyDifference * smartMeterPortion.EstimatedActiveEnergyPlus / sumConsumption;
                    }

                    // step 2: optimize with flexibility
                    OptimizeShortage(_smartMeterPortions);
                }
            }
            else {
                // no flexibility --> evenly distribute
                foreach (var smartMeterPortion in _smartMeterPortions) {
                    smartMeterPortion.Deviation = _energyDifference * smartMeterPortion.EstimatedActiveEnergyPlus / sumConsumption;
                }
            }

        }

        /// <summary>
        /// recursive function for optimization (difference > flexibility)
        /// Example:
        /// - feed in: 5 kWh
        /// - sum consumption: 10 kWh
        ///     - Member 1: 2 kWh | 1 kWh
        ///     - Member 2: 3 kWh | 0 kWh
        ///     - Member 3: 5 kWh | -3 kWh
        /// - separation(feed in * consumption member / sum consumption)
        ///     - Member 1: 1 kWh
        ///     - Member 2: 1.5 kWh
        ///     - Member 3: 2.5 kWh --> OPTIMIZATION (2 kWh would be also enough through the flexibility)
        /// - OPTIMIZATION
        ///     - does every member needs it's assigned load?
        ///     - if not: separate the opened load (0.5 kWh in example above) to the other members
        ///     - REPEAT
        /// </summary>
        private void OptimizeShortage(IList<SmartMeterPortion> _smartMeterPortions) {
            var notOptimizedCount = _smartMeterPortions.Count();
            var optimizedSmartMeters = Enumerable.Repeat(false, notOptimizedCount).ToList();

            void Optimize() {
                var freeEnergy = 0; // how many energy is free to use (through flexibility members)

                for (int i = 0; i < _smartMeterPortions.Count(); i++) {
                    if (!optimizedSmartMeters[i] && _smartMeterPortions[i].Flexibility < 0 && _smartMeterPortions[i].Flexibility < _smartMeterPortions[i].Deviation) {
                        // member gets more than he actually needs
                        freeEnergy += (_smartMeterPortions[i].Deviation - _smartMeterPortions[i].Flexibility);
                        _smartMeterPortions[i].Deviation = _smartMeterPortions[i].Flexibility;
                        optimizedSmartMeters[i] = true; // no more changes
                        notOptimizedCount--;
                    }
                }

                if (freeEnergy > 0) {
                    optimizedSmartMeters
                        .Select((optimized, index) => (optimized, index))
                        .Where(x => !x.optimized)
                        .ToList()
                        .ForEach(x => {
                            _smartMeterPortions[x.index].Deviation += freeEnergy * 1 / notOptimizedCount;
                        });
                    Optimize();
                }
            }

            Optimize();
        }

        public async Task PortionAck(PortionAckModel _portionAckModel) {
            var currentDistributions = await GetCurrentDistributions();
            var smartMeterPortion = currentDistributions
                .SelectMany(x => x.SmartMeterPortions)
                .FirstOrDefault(x => x.SmartMeterId == _portionAckModel.SmartMeterId);

            if (smartMeterPortion != null) {
                smartMeterPortion.Acknowledged = true;
                var prevFlexibility = smartMeterPortion.Flexibility;
                smartMeterPortion.Flexibility = _portionAckModel.Flexibility;
                await mDb.SaveChangesAsync();

                if (prevFlexibility != _portionAckModel.Flexibility) {
                    // flexibility changed --> new distribution
                    await Distribute(currentDistributions.FirstOrDefault(x => x.Id == smartMeterPortion.ECommunityDistributionId));
                }
            }
        }

        public async Task FinalizeDistribution() {
            foreach (var distribution in await GetCurrentDistributions()) {
                var sumFeedIn = distribution.SmartMeterPortions
                    .Sum(x => x.EstimatedActiveEnergyMinus);

                distribution.IsCurrent = false;
                if (sumFeedIn > Constants.DISTRIBUTION_MINIMUM_ENERGY_WH) {
                    SendDistributionNotifications(distribution.SmartMeterPortions, mFCMService.FinalDistribution);
                }
            }
            await mDb.SaveChangesAsync();
        }

        private void SendDistributionNotifications(IList<SmartMeterPortion> _smartMeterPortions, FCMAndroidData _fcmAndroidData) {
            _smartMeterPortions
                .GroupBy(x => x.SmartMeter.MemberId)
                .ToDictionary(x => x.Key, x => x.ToList())
                .ToList()
                .ForEach(_pair => {
                    var sumConsumption = 0;
                    _pair.Value
                        .ForEach(_portion => {
                            sumConsumption += (_portion.EstimatedActiveEnergyPlus + _portion.Deviation);
                        });

                    _fcmAndroidData.BodyArgs = new List<string>() { sumConsumption.ToString(), "Wh" };
                    mFCMService.SendPushNotificationMember(_fcmAndroidData, _pair.Key);
                });
        }

        public async Task<SmartMeterPortion> GetCurrentPortion(Guid _smartMeterId) {
            var distribution = await mDb.ECommunityDistribution
                .OrderByDescending(x => x.Id)
                .Include(x => x.SmartMeterPortions)
                .FirstOrDefaultAsync(x => !x.IsCurrent);

            if (DateTime.UtcNow.Subtract(distribution.Timestamp).TotalMinutes > 60) {
                // time difference bigger than 1 hour
                return null;
            }

            return distribution.SmartMeterPortions
                .FirstOrDefault(x => x.SmartMeterId == _smartMeterId);
        }

        public async Task<NewDistribution> GetNewDistribution(Guid _memberId) {
            var currentDistribution = await mDb.ECommunityDistribution
                .Include(x => x.SmartMeterPortions)
                .ThenInclude(x => x.SmartMeter)
                .FirstOrDefaultAsync(x => x.IsCurrent && x.WasDistributed && x.SmartMeterPortions.Any(portion => portion.SmartMeter.MemberId == _memberId));

            if (currentDistribution == null) {
                return null;
            }

            var sumFeedIn = currentDistribution.SmartMeterPortions.Sum(x => x.EstimatedActiveEnergyMinus);
            var missingSmartMeters = currentDistribution.SmartMeterPortions.Count(x => !x.ForecastFromSmartMeter);

            var sumAssigned = 0;
            foreach (var portion in currentDistribution.SmartMeterPortions) {
                sumAssigned += (portion.EstimatedActiveEnergyPlus + portion.Deviation);
            }
            var unassigned = sumFeedIn - sumAssigned;
            return new NewDistribution() {
                MissingSmartMeterCount = missingSmartMeters,
                UnassignedActiveEnergyMinus = (Math.Abs(unassigned) > Constants.DISTRIBUTION_MINIMUM_ENERGY_WH) ? unassigned : 0,
                SmartMeterPortions = currentDistribution.SmartMeterPortions
                    .Where(x => x.SmartMeter.MemberId == _memberId)
                    .ToList(),
            };
        }

        public Task MeterDataMonitoringArrived(MeterDataMonitoringModel _meterDataMonitoringModel) {
            // TODO
            return Task.CompletedTask;
        }

        private async Task<List<ECommunityDistribution>> GetCurrentDistributions() {
            return await mDb.ECommunityDistribution
                .Where(x => x.IsCurrent)
                .Include(x => x.SmartMeterPortions)
                .ThenInclude(x => x.SmartMeter)
                .ToListAsync();
        }
    }
}