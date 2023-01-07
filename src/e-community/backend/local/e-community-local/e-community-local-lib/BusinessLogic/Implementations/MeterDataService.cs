using e_community_local_lib.BusinessLogic.Interfaces;
using e_community_local_lib.CloudData;
using e_community_local_lib.Database;
using e_community_local_lib.Database.Meter;
using e_community_local_lib.Util;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using System;
using System.Collections.Generic;
using System.Diagnostics.Metrics;
using System.Linq;
using System.Net.Http;
using System.Reflection.Metadata;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;

namespace e_community_local_lib.BusinessLogic.Implementations {
    public class MeterDataService : IMeterDataService {
        private readonly ECommunityLocalContext mDb;

        public MeterDataService(ECommunityLocalContext _db) {
            mDb = _db;
        }

        public async Task<MeterDataMonitoringModel> GetMeterDataMonitoring() {
            var smartMeter = mDb.SmartMeter
                    .OrderByDescending(x => x.Id)
                    .FirstOrDefaultAsync();

            async Task<MeterDataRealTime> GetMeterDataRealTime(int count = 0) {
                // try max. 10 times (MeterDataRealTime could be empty)
                var meterData = await mDb.MeterDataRealTime
                    .OrderByDescending(x => x.Id)
                    .FirstOrDefaultAsync();
                if (meterData == null && count < Constants.MAX_MONITORING_RT_ATTEMPTS_SECONDS) {
                    // wait max. 10 seconds
                    await Task.Delay(1000);
                    meterData = await GetMeterDataRealTime(++count);
                }
                return meterData;
            }

            var meterData = await GetMeterDataRealTime();
            if (meterData != null) {
                return new MeterDataMonitoringModel() {
                    SmartMeterId = (await smartMeter).Id,
                    ActiveEnergyMinus = meterData.ActiveEnergyMinus,
                    ActiveEnergyPlus = meterData.ActiveEnergyPlus,
                };
            }
            return null;
        }
    }
}
