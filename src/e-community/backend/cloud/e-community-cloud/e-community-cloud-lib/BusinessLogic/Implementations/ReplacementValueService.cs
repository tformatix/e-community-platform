using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.Database;
using e_community_cloud_lib.Database.Local;
using System.Linq;

namespace e_community_cloud_lib.BusinessLogic.Implementations {
    public class ReplacementValueService : IReplacementValueService {
        private readonly ECommunityCloudContext mDb;

        public ReplacementValueService(ECommunityCloudContext _db) {
            mDb = _db;
        }

        public SmartMeterPortion GetReplacementValue(SmartMeterPortion _smartMeterPortion) {
            // simply return last portion
            return mDb.SmartMeterPortion
                .OrderByDescending(x => x.ECommunityDistributionId)
                .FirstOrDefault(x =>
                    x.SmartMeterId == _smartMeterPortion.SmartMeterId &&
                    x.ForecastFromSmartMeter
                );
        }
    }
}
