using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.BusinessLogic.Interfaces.SignalR;
using e_community_cloud_lib.Database;
using e_community_cloud_lib.Database.Community;
using e_community_cloud_lib.Database.Local;
using e_community_cloud_lib.Models.Distribution;
using e_community_cloud_lib.NonEntities;
using e_community_cloud_lib.Util;
using e_community_cloud_lib.Util.BusinessLogic;
using e_community_cloud_lib.Util.Enums;
using e_community_cloud_lib.Util.Extensions;
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

        public async Task StartDistribution(DateTime _timestamp) {
            var timestamp = _timestamp.AddMinutes(2 * Constants.DISTRIBUTION_MONITOR_INTERVAL_MINUTES);

            // active memberships in eCommunities with dynamic distribution
            var eCommunityMemberships = await mDb.ECommunityMembership
                .Include(x => x.ECommunity)
                .Include(x => x.Member)
                .ThenInclude(x => x.SmartMeters)
                .Where(x => x.ECommunity.DistributionMode == DistributionMode.Percentage && Constants.ACTIVE_MEMBER_PERMISSIONS.Contains(x.ECommunityPermission))
                .ToListAsync();

            // create distribution objects for the active memberships
            var distributions = eCommunityMemberships
                .DistinctBy(x => x.ECommunityId)
                .Select(x => new ECommunityDistribution() {
                    ECommunityId = x.ECommunityId,
                    Timestamp = timestamp,
                    IsCalculating = true, // distribution is currently calculating
                    WasDistributed = false,
                    IsRelevant = false,
                })
                .ToList();

            mDb.ECommunityDistribution.AddRange(distributions);
            await mDb.SaveChangesAsync();

            foreach (var distribution in distributions) {
                // iterate over created distribtuions and create smart meter portions
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

            // request hourly forecast from every smart meter
            mLocalSignalRSenderService.RequestHourlyForecast();
        }

        public async Task ForecastArrived(ForecastModel _forecastModel) {
            var calculatingDistributions = await GetCalculatingDistributions(); // currently calculated distributions

            if (calculatingDistributions.Count() > 0) {
                // portion of specific smart meter
                var smartMeterPortion = calculatingDistributions
                    .SelectMany(x => x.SmartMeterPortions)
                    .FirstOrDefault(x => x.SmartMeterId == _forecastModel.SmartMeterId);

                if (smartMeterPortion != null) {
                    // portion available
                    smartMeterPortion.EstimatedActiveEnergyMinus = _forecastModel.ActiveEnergyMinus;
                    smartMeterPortion.EstimatedActiveEnergyPlus = _forecastModel.ActiveEnergyPlus;
                    smartMeterPortion.Flexibility = _forecastModel.Flexibility;
                    smartMeterPortion.ForecastFromSmartMeter = true;
                    await mDb.SaveChangesAsync();
                }
            }
        }

        public async Task Distribute() {
            foreach (var distribution in await GetCalculatingDistributions()) {
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

            foreach (var smartMeterPortion in _eCommunityDistribution.SmartMeterPortions) {
                // reset deviation
                smartMeterPortion.Deviation = 0;
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
            _eCommunityDistribution.IsRelevant = sumFeedIn > Constants.DISTRIBUTION_MINIMUM_ENERGY_WH; // only relevant if feed in is over threshold
            await mDb.SaveChangesAsync();
            if (_eCommunityDistribution.IsRelevant) {
                // only send notifications if distribution is relevant
                SendDistributionNotifications(_eCommunityDistribution.SmartMeterPortions, mFCMService.NewDistribution);
            }
        }

        /// <summary>
        /// distributes energy surplus (consumption < feed-in)
        /// </summary>
        /// <param name="_smartMeterPortions">portions of smart meters</param>
        /// <param name="_energyDifference">consumption - feed-in</param>
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
                    }
                }
                else {
                    // difference >= flexibility --> everybody gets their positive flexibility (maybe more feed in than consumption)
                    foreach (var smartMeterPortion in _smartMeterPortions) {
                        if (smartMeterPortion.Flexibility > 0) {
                            smartMeterPortion.Deviation = smartMeterPortion.Flexibility;
                        }
                    }
                }
            }
        }

        /// <summary>
        /// distributes energy shortage (consumption > feed-in)
        /// </summary>
        /// <param name="_smartMeterPortions">portions of smart meters</param>
        /// <param name="_energyDifference">consumption - feed-in</param>
        /// <param name="sumConsumption">sum of the consumption of the smart meters</param>
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
            var calculatingDistributions = await GetCalculatingDistributions(); // currently calculated distributions
            var smartMeterPortion = calculatingDistributions
                .SelectMany(x => x.SmartMeterPortions)
                .FirstOrDefault(x => x.SmartMeterId == _portionAckModel.SmartMeterId);

            if (smartMeterPortion != null) {
                smartMeterPortion.Acknowledged = true;
                if (smartMeterPortion.Flexibility != _portionAckModel.Flexibility) {
                    // flexibility changed --> new distribution
                    if (_portionAckModel.Flexibility < -smartMeterPortion.EstimatedActiveEnergyPlus) {
                        // flexibility smaller than estimated consumption (not possible)
                        smartMeterPortion.Flexibility = -smartMeterPortion.EstimatedActiveEnergyPlus;
                    }
                    else {
                        smartMeterPortion.Flexibility = _portionAckModel.Flexibility;
                    }
                    await mDb.SaveChangesAsync();
                    await Distribute(calculatingDistributions.FirstOrDefault(x => x.Id == smartMeterPortion.ECommunityDistributionId));
                } else {
                    await mDb.SaveChangesAsync();
                }
            } else {
                // distribution expired
                throw new ServiceException(ServiceException.Type.PORTION_ACK_EXPIRED);
            }
        }

        public async Task FinalizeDistribution() {
            foreach (var distribution in await GetCalculatingDistributions()) {
                distribution.IsCalculating = false;
                await mDb.SaveChangesAsync();
                if (distribution.IsRelevant) {
                    // only send notifications if distribution is relevant
                    SendDistributionNotifications(distribution.SmartMeterPortions, mFCMService.FinalDistribution);
                }
            }
        }

        /// <summary>
        /// sends notifications about current distribution via FCM (grouped by member)
        /// </summary>
        /// <param name="_smartMeterPortions">portions of smart meters</param>
        /// <param name="_fcmAndroidData">FCM notification data for Android</param>
        private void SendDistributionNotifications(IList<SmartMeterPortion> _smartMeterPortions, FCMAndroidData _fcmAndroidData) {
            _smartMeterPortions
                .GroupBy(x => x.SmartMeter.MemberId)
                .ToDictionary(x => x.Key, x => x.ToList())
                .ToList()
                .ForEach(_pair => {
                    // MemberId -> List<SmartMeterPortion>
                    // aggregate per member and send notification
                    var sumConsumption = 0;
                    _pair.Value
                        .ForEach(_portion => {
                            sumConsumption += (_portion.EstimatedActiveEnergyPlus + _portion.Deviation);
                        });

                    _fcmAndroidData.BodyArgs = new List<string>() { Formatter.formatMeterData(sumConsumption, true) };
                    mFCMService.SendPushNotificationMember(_fcmAndroidData, _pair.Key);
                });
        }

        public async Task<CurrentPortion> GetCurrentPortion(Guid _smartMeterId, bool _includeSmartMeter) {
            // get first distribution which is not calculating
            var distribution = (_includeSmartMeter)
                ? await mDb.ECommunityDistribution
                    .OrderByDescending(x => x.Id)
                    .Include(x => x.SmartMeterPortions)
                    .ThenInclude(x => x.SmartMeter)
                    .FirstOrDefaultAsync(x => !x.IsCalculating && x.SmartMeterPortions.Any(portion => portion.SmartMeterId == _smartMeterId))
                : await mDb.ECommunityDistribution
                    .OrderByDescending(x => x.Id)
                    .Include(x => x.SmartMeterPortions)
                    .FirstOrDefaultAsync(x => !x.IsCalculating && x.SmartMeterPortions.Any(portion => portion.SmartMeterId == _smartMeterId));


            if (distribution == null || DateTime.UtcNow.Subtract(distribution.Timestamp).TotalMinutes > 60) {
                // empty or time difference bigger than 1 hour
                return null;
            }

            var smartMeterPortion = distribution.SmartMeterPortions
                .FirstOrDefault(x => x.SmartMeterId == _smartMeterId);

            if (smartMeterPortion != null) {
                return smartMeterPortion.CopyPropertiesTo(new CurrentPortion() {
                    MissingSmartMeterCount = GetMissingSmartMeterCount(smartMeterPortion.ECommunityDistribution),
                    UnassignedActiveEnergyMinus = GetUnassignedActiveEnergyMinus(smartMeterPortion.ECommunityDistribution),
                    IsRelevant = smartMeterPortion.ECommunityDistribution.IsRelevant,
                    SmartMeter = smartMeterPortion.SmartMeter
                });
            }
            return null;
        }

        public async Task<NewDistribution> GetNewDistribution(Guid _memberId) {
            var calculatingDistribution = await mDb.ECommunityDistribution
                .Include(x => x.SmartMeterPortions)
                .ThenInclude(x => x.SmartMeter)
                .FirstOrDefaultAsync(x => x.IsRelevant && x.IsCalculating && x.WasDistributed && x.SmartMeterPortions.Any(portion => portion.SmartMeter.MemberId == _memberId));

            if (calculatingDistribution == null) {
                return null;
            }

            return new NewDistribution() {
                MissingSmartMeterCount = GetMissingSmartMeterCount(calculatingDistribution),
                UnassignedActiveEnergyMinus = GetUnassignedActiveEnergyMinus(calculatingDistribution),
                SmartMeterPortions = calculatingDistribution.SmartMeterPortions
                    .Where(x => x.SmartMeter.MemberId == _memberId)
                    .ToList(),
            };
        }

        /// <param name="_distribution">distribution of eCommunity</param>
        /// <returns>sum of feed in</returns>
        private int GetSumFeedIn(ECommunityDistribution _distribution) {
            return _distribution.SmartMeterPortions.Sum(x => x.EstimatedActiveEnergyMinus);
        }

        /// <param name="_distribution">distribution of eCommunity</param>
        /// <returns>unassigned A- (feed-in)</returns>
        private int GetUnassignedActiveEnergyMinus(ECommunityDistribution _distribution) {
            var sumFeedIn = GetSumFeedIn(_distribution);

            var sumAssigned = 0;
            foreach (var portion in _distribution.SmartMeterPortions) {
                sumAssigned += (portion.EstimatedActiveEnergyPlus + portion.Deviation);
            }
            var unassigned = sumFeedIn - sumAssigned;
            return (Math.Abs(unassigned) > Constants.DISTRIBUTION_MINIMUM_ENERGY_WH) ? unassigned : 0;
        }

        /// <param name="_distribution">distribution of eCommunity</param>
        /// <returns>number of missing forecasts from smart meters</returns>
        private int GetMissingSmartMeterCount(ECommunityDistribution _distribution) {
            return _distribution.SmartMeterPortions.Count(x => !x.ForecastFromSmartMeter);
        }

        /// <returns>distributions currently calculated</returns>
        private async Task<List<ECommunityDistribution>> GetCalculatingDistributions() {
            return await mDb.ECommunityDistribution
                .Where(x => x.IsCalculating)
                .Include(x => x.SmartMeterPortions)
                .ThenInclude(x => x.SmartMeter)
                .ToListAsync();
        }
    }
}