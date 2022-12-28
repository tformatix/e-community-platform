using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.Util;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Serilog;
using System;
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

            while (!stoppingToken.IsCancellationRequested) {
                int minute = DateTime.UtcNow.Minute;
                int delayMinutes = Constants.DISTRIBUTION_MONITOR_INTERVAL_MINUTES - (minute % Constants.DISTRIBUTION_MONITOR_INTERVAL_MINUTES);
                await Task.Delay(delayMinutes * 60000); // DISTRIBUTION_MONITOR_INTERVAL_MINUTES = 5 --> e.g. 12:00, 12:05, 12:10, ...

                using (var scope = mServiceScopeFactory.CreateScope()) {
                    var distributionService = scope.ServiceProvider.GetRequiredService<IDistributionService>();
                    // TODO: Monitoring (1)

                    switch (minute + delayMinutes) {
                        case 0:
                            // (7) inform members about their portions
                            await distributionService.FinalizeDistribution();
                            break;
                        case 60 - 2 * Constants.DISTRIBUTION_MONITOR_INTERVAL_MINUTES:
                            // (1) request forecasts and init database
                            await distributionService.StartDistribution();
                            break;
                        case 60 - Constants.DISTRIBUTION_MONITOR_INTERVAL_MINUTES:
                            // (3) distribute energy
                            await distributionService.Distribute();
                            break;
                    }
                }
            }
        }
    }
}
