using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.Database;
using e_community_cloud_lib.Database.Local;
using e_community_cloud_lib.Models.SmartMeter;
using e_community_cloud_lib.Util.BusinessLogic;
using e_community_cloud_lib.Util.Extensions;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.BusinessLogic.Implementations
{
    public class PairingService : IPairingService
    {
        private readonly ECommunityCloudContext mDb;

        public PairingService(ECommunityCloudContext _db)
        {
            mDb = _db;
        }

        public async Task<SmartMeter> CreateSmartMeter(CreateSmartMeterModel _createSmartMeterModel, Guid _memberId)
        {
            SmartMeter smartMeter = _createSmartMeterModel.CopyPropertiesTo(new SmartMeter());
            smartMeter.MemberId = _memberId;
            smartMeter.LocalStorageId = Guid.NewGuid();
            await mDb.SmartMeter.AddAsync(smartMeter);
            await mDb.SaveChangesAsync();
            return smartMeter;
        }

        public async Task<SmartMeter> GetLocalData(Guid _smartMeterId)
        {
            var smartMeter = await mDb.SmartMeter
                .Include(x => x.BatterySystems)
                .Include(x => x.PVSystems)
                .Include(x => x.SupplierPriceRate)
                .Include(x => x.GridPriceRate)
                .ThenInclude(x => x.Charges)
                .Include(x => x.Member)
                .ThenInclude(x => x.ECommunityMemberships)
                .FirstOrDefaultAsync(x => x.Id == _smartMeterId);
            if (smartMeter == null)
            {
                throw new ServiceException(ServiceException.Type.NO_SMART_METER_FOUND);
            }
            return smartMeter;
        }
    }
}
