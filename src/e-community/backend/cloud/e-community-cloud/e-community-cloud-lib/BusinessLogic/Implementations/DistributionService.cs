using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.Database;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.BusinessLogic.Implementations {
    public class DistributionService : IDistributionService {

        private readonly ECommunityCloudContext mDb;
        private readonly IFCMService mFCMService;

        public DistributionService(ECommunityCloudContext _db, IFCMService _fcmService) {
            mDb = _db;
            mFCMService = _fcmService;
        }

        public Task Distribute(Guid _eCommunityId) {
            return Task.CompletedTask;
        }
    }
}
