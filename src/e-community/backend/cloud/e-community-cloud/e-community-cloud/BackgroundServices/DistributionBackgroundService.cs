using e_community_cloud_lib.BusinessLogic.Implementations;
using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.Database.General;
using e_community_cloud_lib.Util;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Serilog;
using System;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;

namespace e_community_cloud.BackgroundServices {
    public class DistributionBackgroundService : BackgroundService {
        private readonly IServiceScopeFactory mServiceScopeFactory;

        public DistributionBackgroundService(IServiceScopeFactory _serviceScopeFactory) {
            mServiceScopeFactory = _serviceScopeFactory;
        }

        protected override async Task ExecuteAsync(CancellationToken stoppingToken) {
            Log.Information("DistributionBackgroundService::Execute");

            while(!stoppingToken.IsCancellationRequested) {
                int minute = DateTime.UtcNow.Minute;
                int delayMinutes = 5 - (minute % 5);
                await Task.Delay(delayMinutes * 60000); // e.g. 12:00, 12:05, 12:10, ...

                using (var scope = mServiceScopeFactory.CreateScope()) {
                    // TODO: Monitoring

                    switch(minute + delayMinutes) {
                        case 0:
                            // TODO: (7) assigned portions
                            break;
                        case 50:
                            // TODO: (1) request forecast
                            break;
                        case 55:
                            // TODO: (3) portions for changing/accepting
                            break;
                    }
                }
            }
        }
    }
}
