using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.BusinessLogic.Interfaces.SignalR;
using e_community_cloud_lib.Database;
using e_community_cloud_lib.Database.Community;
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

            var distributions = eCommunityMembers
                .DistinctBy(x => x.ECommunityId)
                .Select(x => new ECommunityDistribution() {
                    ECommunityId = x.ECommunityId,
                    Timestamp = timestamp
                })
                .ToList();

            mDb.ECommunityDistribution.AddRange(distributions);
            await mDb.SaveChangesAsync();

            foreach (var distribution in distributions) {
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

        public Task StartMonitorSession() {
            throw new NotImplementedException();
        }

        public Task Distribute(Guid _eCommunityId) {
            return Task.CompletedTask;
        }
    }
}
