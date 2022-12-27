using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.BusinessLogic.Interfaces.SignalR;
using e_community_cloud_lib.Database;
using e_community_cloud_lib.Database.Community;
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
                        Acknowledged = false
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

            if(portion != null) {
                portion.EstimatedActiveEnergyMinus = _forecastModel.ActiveEnergyMinus;
                portion.EstimatedActiveEnergyPlus = _forecastModel.ActiveEnergyPlus;
                portion.Flexibility = _forecastModel.Flexibility;
            }
        }

        public Task StartMonitorSession() {
            throw new NotImplementedException();
        }

        public async Task Distribute() {
            foreach(var distribution in mCurrentDistributions) {
                await Distribute(distribution);
            }
        }

        public async Task Distribute(ECommunityDistribution _eCommunityDistribution) {
            var smartMeters = await mDb.SmartMeterPortion
                .Where(x => x.ECommunityDistributionId == _eCommunityDistribution.Id)
                .ToListAsync();

            var sumFeedIn = smartMeters
                .Sum(x => x.EstimatedActiveEnergyMinus);

            // TODO error checking
            if(sumFeedIn > 0) {
                var sumConsumption = smartMeters
                    .Sum(x => x.EstimatedActiveEnergyPlus);
                var energyDifference = sumFeedIn - sumConsumption;

                if(energyDifference >= 0) {
                    // more feed in than consumption
                    var sumFlexibilityPlus = smartMeters
                        .Where(x => x.Flexibility > 0)
                        .Sum(x => x.Flexibility);

                    if(sumFlexibilityPlus > 0) {
                        // members have positive flexibility
                        if(energyDifference < sumFlexibilityPlus) {
                            // difference < flexibility --> % increase

                        } else {
                            // difference >= flexibility --> maybe more feed in than consumption

                        }
                    }
                } else {
                    // more consumption than feed in
                    var sumFlexibilityMinus = smartMeters
                        .Where(x => x.Flexibility < 0)
                        .Sum(x => x.Flexibility);
                }
            }

        }

    }
}
