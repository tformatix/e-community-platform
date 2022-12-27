using e_community_local_lib.BusinessLogic.Interfaces;
using e_community_local_lib.CloudData.Local;
using e_community_local_lib.Database;
using e_community_local_lib.Database.General;
using e_community_local_lib.Util.Extensions;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_local_lib.BusinessLogic.Implementations
{
    public class LocalChangesService : ILocalChangesService
    {
        private readonly ECommunityLocalContext mDb;

        public LocalChangesService(ECommunityLocalContext _db)
        {
            mDb = _db;
        }

        public async Task SmartMeterChanged(CloudSmartMeterDto _cloudSmartMeterDto)
        {
            var smartMeter = _cloudSmartMeterDto.CopyPropertiesTo(new SmartMeter());
            mDb.SmartMeter.Update(smartMeter);
            await mDb.SaveChangesAsync();
        }
    }
}
