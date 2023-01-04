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

                using var scope = mServiceScopeFactory.CreateScope();
                var monitoringService = scope.ServiceProvider.GetRequiredService<IMonitoringService>();

                var currentMinute = minute + delayMinutes;
                currentMinute = currentMinute < 60 ? currentMinute : 0;

                var timestamp = DateTime.UtcNow;
                timestamp = timestamp
                    .AddMinutes(-timestamp.Minute)
                    .AddMinutes(currentMinute)
                    .AddSeconds(-timestamp.Second)
                    .AddTicks(-(timestamp.Ticks % 10000000));

                // request meter data for monitoring and init database
                await monitoringService.StartMonitoring(timestamp);

                switch (currentMinute) {
                    case 60 - 2 * Constants.DISTRIBUTION_MONITOR_INTERVAL_MINUTES:
                        // 10 minutes before full hour (e.g. 11:50): request forecasts and init database for distribution
                        await GetDistributionService(scope).StartDistribution(timestamp);
                        break;
                    case 60 - Constants.DISTRIBUTION_MONITOR_INTERVAL_MINUTES:
                        // 5 minutes before full hour (e.g. 11:55): distribute energy
                        await GetDistributionService(scope).Distribute();
                        break;
                    case 0:
                        // full hour (e.g. 12:00): inform members about their portions
                        await GetDistributionService(scope).FinalizeDistribution();
                        break;
                }
            }
        }

        private IDistributionService GetDistributionService(IServiceScope _scope) {
            return _scope.ServiceProvider.GetRequiredService<IDistributionService>();
        }
    }
}
