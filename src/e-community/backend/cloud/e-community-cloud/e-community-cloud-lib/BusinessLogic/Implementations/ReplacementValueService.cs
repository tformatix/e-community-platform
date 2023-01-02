using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.BusinessLogic.Interfaces.SignalR;
using e_community_cloud_lib.Database;
using e_community_cloud_lib.Database.Community;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.BusinessLogic.Implementations {
    public class ReplacementValueService : IReplacementValueService {
        private readonly ECommunityCloudContext mDb;

        public ReplacementValueService(ECommunityCloudContext _db) {
            mDb = _db;
        }

        public SmartMeterPortion GetReplacementValue(SmartMeterPortion _smartMeterPortion) {
            return mDb.SmartMeterPortion
                .OrderByDescending(x => x.ECommunityDistributionId)
                .FirstOrDefault(x =>
                    x.ECommunityDistributionId == _smartMeterPortion.ECommunityDistributionId &&
                    x.SmartMeterId == _smartMeterPortion.SmartMeterId &&
                    x.ForecastFromSmartMeter
                );
        }
    }
}
