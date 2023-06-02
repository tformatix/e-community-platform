using e_community_local_lib.BusinessLogic.Interfaces;
using e_community_local_lib.CloudData;
using e_community_local_lib.Database;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_local_lib.BusinessLogic.Implementations {
    public class ForecastService : IForecastService {
        private readonly ECommunityLocalContext mDb;

        public ForecastService(ECommunityLocalContext _db) {
            mDb = _db;
        }

        public async Task<ForecastModel> GetHourlyForecast() {
            var smartMeter = mDb.SmartMeter.FirstOrDefaultAsync();

            var histCount = await mDb.MeterDataHistory.CountAsync();
            if (histCount >= 5) {
                // we need 5 values for 1 hour (0, 15, 30, 45, 60)
                var descOrdered = mDb.MeterDataHistory
                    .OrderByDescending(x => x.Id);

                var currentHour = await descOrdered.FirstOrDefaultAsync();
                var lastHour = await descOrdered.Skip(4).FirstOrDefaultAsync();

                return new() {
                    ActiveEnergyMinus = currentHour.ActiveEnergyMinus - lastHour.ActiveEnergyMinus,
                    ActiveEnergyPlus = currentHour.ActiveEnergyPlus - lastHour.ActiveEnergyPlus,
                    Flexibility = 0,
                    SmartMeterId = (await smartMeter).Id
                };
            }

            return new() {
                ActiveEnergyMinus = 0,
                ActiveEnergyPlus = 0,
                Flexibility = 0,
                SmartMeterId = (await smartMeter).Id
            };
        }
    }
}
