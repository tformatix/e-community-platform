using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.Util;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Serilog;
using System;
using System.Threading;
using System.Threading.Tasks;

namespace e_community_cloud.BackgroundServices {
    /// <summary>
    /// background service which handles time driven events for energy distribution in eCommunities
    /// <list type="bullet">
    /// <item><description>every 5 minutes: monitoring (non-compliance of forecast or offline local devices)</description></item>
    /// <item><description>10 minutes before full hour: request hourly forecast from local devices</description></item>
    /// <item><description>5 minutes before full hour: distribute hourly energy and send distribution to end devices</description></item>
    /// <item><description>full hour: final distribution for the next hour</description></item>
    /// <item><description>5 minutes after full hour: remove unnecessary monitoring entries and store actual energy into distribution</description></item>
    /// </list>
    /// <seealso cref="BackgroundService"/>
    /// </summary>
    public class DistributionBackgroundService : BackgroundService {

        private readonly IServiceScopeFactory mServiceScopeFactory;

        public DistributionBackgroundService(IServiceScopeFactory _serviceScopeFactory) {
            mServiceScopeFactory = _serviceScopeFactory;
        }

        protected override async Task ExecuteAsync(CancellationToken stoppingToken) {
            Log.Information("DistributionBackgroundService::Execute");

            while (!stoppingToken.IsCancellationRequested) {
                try {
                    int minute = DateTime.UtcNow.Minute;
                    int delayMinutes = Constants.DISTRIBUTION_MONITOR_INTERVAL_MINUTES
                        - (minute % Constants.DISTRIBUTION_MONITOR_INTERVAL_MINUTES); // should be every full 5 minutes
                    await Task.Delay(delayMinutes * 60000); // e.g. 12:00, 12:05, 12:10, ...

                    using var scope = mServiceScopeFactory.CreateScope();
                    var monitoringService = scope.ServiceProvider.GetRequiredService<IMonitoringService>();

                    var currentMinute = minute + delayMinutes;
                    currentMinute = currentMinute < 60 ? currentMinute : 0;

                    var timestamp = DateTime.UtcNow;
                    // remove seconds and ticks (12:05:00.00000)
                    timestamp = timestamp
                        .AddMinutes(-timestamp.Minute)
                        .AddMinutes(currentMinute)
                        .AddSeconds(-timestamp.Second)
                        .AddTicks(-(timestamp.Ticks % 10000000));

                    // request meter data for monitoring and init database for the new monitoring entries
                    await monitoringService.StartMonitoring(timestamp);

                    switch (currentMinute) {
                        case 60 - 2 * Constants.DISTRIBUTION_MONITOR_INTERVAL_MINUTES:
                            // 10 minutes before full hour (e.g. 11:50): request forecasts and init database for distribution
                            await GetDistributionService(scope).StartDistribution(timestamp);
                            break;
                        case 60 - Constants.DISTRIBUTION_MONITOR_INTERVAL_MINUTES:
                            // 5 minutes before full hour (e.g. 11:55): distribute energy between members
                            await GetDistributionService(scope).Distribute();
                            break;
                        case 0:
                            // full hour (e.g. 12:00): inform members about their portions
                            await GetDistributionService(scope).FinalizeDistribution();
                            break;
                    }
                }
                catch (Exception _e) {
                    Log.Error($"DistributionBackgroundService::{_e.Message}");
                }
            }
        }

        /// <param name="_scope">service scope</param>
        /// <returns>distribution service in service scope</returns>
        private IDistributionService GetDistributionService(IServiceScope _scope) {
            return _scope.ServiceProvider.GetRequiredService<IDistributionService>();
        }
    }
}
