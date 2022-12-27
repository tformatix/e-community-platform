using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.BusinessLogic.Interfaces.SignalR;
using e_community_cloud_lib.Database;
using e_community_cloud_lib.Database.Community;
using e_community_cloud_lib.Database.General;
using e_community_cloud_lib.Database.Local;
using e_community_cloud_lib.Models.Distribution;
using e_community_cloud_lib.Util;
using e_community_cloud_lib.Util.Enums;
using e_community_cloud_lib.Util.Extensions;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.BusinessLogic.Implementations {
    public class DistributionService : IDistributionService {

        private readonly ECommunityCloudContext mDb;
        private readonly IFCMService mFCMService;
        private readonly ILocalSignalRSenderService mLocalSignalRSenderService;
        private List<ECommunityDistribution> mCurrentDistributions;

        public DistributionService(ECommunityCloudContext _db, IFCMService _fcmService, ILocalSignalRSenderService _localSignalRSenderService) {
            mDb = _db;
            mFCMService = _fcmService;
            mLocalSignalRSenderService = _localSignalRSenderService;
        }

        public async Task StartDistribution() {
            var timestamp = DateTime.UtcNow.AddMinutes(2 * Constants.DISTRIBUTION_MONITOR_INTERVAL_MINUTES);
            timestamp.AddSeconds(-timestamp.Second);
            timestamp.AddMilliseconds(-timestamp.Millisecond);

            var eCommunityMembers = await mDb.ECommunityMembership
                .Include(x => x.ECommunity)
                .Include(x => x.Member)
                .ThenInclude(x => x.SmartMeters)
                .Where(x => x.ECommunity.DistributionMode == DistributionMode.Percentage && Constants.ACTIVE_MEMBER_PERMISSIONS.Contains(x.ECommunityPermission))
                .ToListAsync();

            mCurrentDistributions = eCommunityMembers
                .DistinctBy(x => x.ECommunityId)
                .Select(x => new ECommunityDistribution() {
                    ECommunityId = x.ECommunityId,
                    Timestamp = timestamp
                })
                .ToList();

            mDb.ECommunityDistribution.AddRange(mCurrentDistributions);
            await mDb.SaveChangesAsync();

            foreach (var distribution in mCurrentDistributions) {
                mDb.SmartMeterPortion.AddRange(eCommunityMembers
                    .Where(x => x.ECommunityId == distribution.ECommunityId)
                    .SelectMany(x => x.Member.SmartMeters)
                    .Select(x => new SmartMeterPortion() {
                        ECommunityDistributionId = distribution.Id,
                        SmartMeterId = x.Id,
                    })
                );
            }
            await mDb.SaveChangesAsync();

            mLocalSignalRSenderService.RequestHourlyForecast();
        }

        public async Task ForecastArrived(ForecastModel _forecastModel) {
            var portion = await mDb.SmartMeterPortion
                .OrderByDescending(x => x.ECommunityDistributionId)
                .FirstOrDefaultAsync(x => x.SmartMeterId == _forecastModel.SmartMeterId);

            if (portion != null) {
                portion.EstimatedActiveEnergyMinus = _forecastModel.ActiveEnergyMinus;
                portion.EstimatedActiveEnergyPlus = _forecastModel.ActiveEnergyPlus;
                portion.Flexibility = _forecastModel.Flexibility;
            }
        }

        public Task StartMonitorSession() {
            throw new NotImplementedException();
        }

        public async Task Distribute() {
            foreach (var distribution in mCurrentDistributions) {
                await Distribute(distribution);
            }
        }

        public async Task Distribute(ECommunityDistribution _eCommunityDistribution) {
            var smartMeterPortions = await mDb.SmartMeterPortion
                .Where(x => x.ECommunityDistributionId == _eCommunityDistribution.Id)
                .ToListAsync();

            var sumFeedIn = smartMeterPortions
                .Sum(x => x.EstimatedActiveEnergyMinus) ?? 0;

            // TODO error checking (missing smart meter)
            var missingSmartMeters = smartMeterPortions.Count(x => x.EstimatedActiveEnergyMinus == null);

            if (sumFeedIn > 0) {
                var sumConsumption = smartMeterPortions
                    .Sum(x => x.EstimatedActiveEnergyPlus) ?? 0;
                var energyDifference = sumFeedIn - sumConsumption;

                if (energyDifference >= 0) {
                    // more feed in than consumption (surplus)
                    DistributeSurplus(smartMeterPortions, energyDifference);
                }
                else {
                    // more consumption than feed in (shortage)
                    DistributeShortage(smartMeterPortions, energyDifference, sumConsumption);
                }
            }
            await mDb.SaveChangesAsync();
        }

        private void DistributeSurplus(List<SmartMeterPortion> _smartMeterPortions, int _energyDifference) {
            var sumFlexibilityPlus = _smartMeterPortions
                        .Where(x => x.Flexibility > 0)
                        .Sum(x => x.Flexibility);

            if (sumFlexibilityPlus > 0) {
                // members have positive flexibility
                if (_energyDifference < sumFlexibilityPlus) {
                    // difference < flexibility --> % increase
                    _smartMeterPortions.ForEach(x => {
                        if (x.Flexibility > 0) {
                            x.Deviation = _energyDifference * x.Flexibility / sumFlexibilityPlus;
                        }
                        x.Optimized = true;
                    });
                }
                else {
                    // difference >= flexibility --> maybe more feed in than consumption
                    _smartMeterPortions.ForEach(x => {
                        if (x.Flexibility > 0) {
                            x.Deviation = x.Flexibility;
                        }
                        x.Optimized = true;
                    });
                }
            }
        }

        private void DistributeShortage(List<SmartMeterPortion> _smartMeterPortions, int _energyDifference, int sumConsumption) {
            var sumFlexibilityMinus = -_smartMeterPortions
                        .Where(x => x.Flexibility < 0)
                        .Sum(x => x.Flexibility);

            if (sumFlexibilityMinus > 0) {
                // members have negative flexibility
                if (-_energyDifference <= sumFlexibilityMinus) {
                    // difference <= flexibility --> % decrease based on flexibility
                    _smartMeterPortions.ForEach(x => {
                        if (x.Flexibility < 0) {
                            x.Deviation = -_energyDifference * x.Flexibility / sumFlexibilityMinus;
                        }
                        x.Optimized = true;
                    });
                }
                else {
                    // difference > flexibility --> flexibility unsatisfactory

                    // step 1: seperate evenly
                    _smartMeterPortions.ForEach(x => {
                        x.Deviation = _energyDifference * x.EstimatedActiveEnergyPlus / sumConsumption;
                    });

                    // step 2: optimize with flexibility
                    OptimizeShortage(_smartMeterPortions);
                }
            } else {
                // no flexibility --> evenly distribute
                _smartMeterPortions.ForEach(x => {
                    if (x.Flexibility < 0) {
                        x.Deviation = -_energyDifference * x.EstimatedActiveEnergyPlus / sumConsumption;
                    }
                    x.Optimized = true;
                });
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
        private void OptimizeShortage(List<SmartMeterPortion> _smartMeterPortions) {
            var openMembers = _smartMeterPortions.Count(); // hom many members are not optimized

            void Optimize() {
                var freeEnergy = 0; // how many energy is free to use (through flexibility members)

                _smartMeterPortions.ForEach(x => {
                    if(!x.Optimized && x.Flexibility < 0 && x.Flexibility < x.Deviation) {
                        // member gets more than he actually needs
                        freeEnergy += (x.Deviation ?? 0 - x.Flexibility ?? 0);
                        x.Deviation = x.Flexibility;
                        x.Optimized = true; // no more changes
                        openMembers--;
                    }
                });

                if(freeEnergy > 0) {
                    _smartMeterPortions.ForEach(x => {
                        if (!x.Optimized) {
                            x.Deviation += freeEnergy * 1 / openMembers;
                        }
                    });
                    Optimize();
                }
            }

            Optimize();
        }
    }
}
