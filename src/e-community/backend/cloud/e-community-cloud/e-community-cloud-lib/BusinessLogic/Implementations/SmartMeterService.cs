using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.BusinessLogic.Interfaces.SignalR;
using e_community_cloud_lib.Database;
using e_community_cloud_lib.Database.Local;
using e_community_cloud_lib.Models.SmartMeter;
using e_community_cloud_lib.Util.Extensions;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.BusinessLogic.Implementations
{
    public class SmartMeterService : ISmartMeterService
    {
        private readonly ECommunityCloudContext mDb;
        private readonly ILocalSignalRSenderService mLocalSignalRSenderService;

        public SmartMeterService(ECommunityCloudContext _db, ILocalSignalRSenderService _localSignalRSenderService)
        {
            mDb = _db;
            mLocalSignalRSenderService = _localSignalRSenderService;
        }

        public async Task<List<SmartMeter>> GetSmartMeters(Guid _memberId) {
            return await mDb.SmartMeter
                .Where(x => x.MemberId == _memberId)
                .OrderBy(x => x.Name)
                .ToListAsync();
        }

        public async Task Update(UpdateSmartMeterModel _updateSmartMeterModel)
        {
            var smartMeter = await mDb.SmartMeter.FirstOrDefaultAsync(x => x.Id == _updateSmartMeterModel.Id);
            smartMeter = _updateSmartMeterModel.CopyPropertiesTo(smartMeter);
            smartMeter.LocalStorageId = Guid.NewGuid();
            await mDb.SaveChangesAsync();
            mLocalSignalRSenderService.UpdateSmartMeter(_updateSmartMeterModel);
        }
    }
}
