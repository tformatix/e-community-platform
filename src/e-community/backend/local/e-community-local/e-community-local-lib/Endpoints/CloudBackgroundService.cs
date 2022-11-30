using e_community_local_lib.BusinessLogic.Interfaces;
using e_community_local_lib.BusinessLogic.Interfaces.SignalR;
using e_community_local_lib.CloudDtos.Local;
using e_community_local_lib.Endpoints.Interfaces;
using Microsoft.AspNetCore.SignalR.Client;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Serilog;
using System;
using System.Threading.Tasks;
using System.Threading;
using e_community_local_lib.Util;
using e_community_local_lib.CloudDtos;

namespace e_community_local_lib.Endpoints {
    /// <summary>
    /// background loop and listener to cloud
    /// <seealso cref="BackgroundService"/>
    /// <seealso cref="ICloudBackgroundService"/>
    /// <seealso cref="ICloudSignalRReceiver"/>
    /// </summary>
    public class CloudBackgroundService : BackgroundService, ICloudBackgroundService {
        private readonly IServiceScopeFactory mServiceScopeFactory;
        private System.Timers.Timer mTimer;
        private IHubConnectionService mHubConnectionService;
        private ICloudSignalRSenderService mCloudSignalRSenderService;
        private ILocalChangesService mLocalChangesService;
        private static bool mRTDataRequested = false;

        public CloudBackgroundService(IServiceScopeFactory _serviceScopeFactory) {
            mServiceScopeFactory = _serviceScopeFactory;
        }

        async Task ICloudBackgroundService.ExecuteAsync(CancellationToken stoppingToken) => await ExecuteAsync(stoppingToken);


        protected override async Task ExecuteAsync(CancellationToken stoppingToken) {
            Log.Information("CloudBackgroundService::Execute");
            using (var scope = mServiceScopeFactory.CreateScope()) {
                mHubConnectionService = scope.ServiceProvider.GetRequiredService<IHubConnectionService>();
                mCloudSignalRSenderService = scope.ServiceProvider.GetRequiredService<ICloudSignalRSenderService>();
                mLocalChangesService = scope.ServiceProvider.GetRequiredService<ILocalChangesService>();

                // TODO: @Michi slave starten

                while (!stoppingToken.IsCancellationRequested) {
                    try {
                        var hubConnection = await mHubConnectionService.GetHubConnection();
                        await Task.Delay(1000);
                        if (hubConnection == null || hubConnection.State == HubConnectionState.Disconnected) {
                            // hub connection disconnected (start again)
                            hubConnection = await mHubConnectionService.Reconnect();
                        }
                        else {
                            if (mRTDataRequested) {
                                _ = mCloudSignalRSenderService.SendRTData();
                            }
                        }
                    } catch (Exception _exc) {
                        Log.Error($"CloudBackgroundService::loop {_exc.Message}");
                    }
                }

            }
        }


        public Task RequestRTData() {
            Log.Information("CloudBackgroundService::RT requested");

            // timer, which must be extended every 5 minutes
            if (mTimer != null) {
                mTimer.Interval = Constants.RT_EXTEND_MILLISECONDS;
            }
            else {
                mTimer = new System.Timers.Timer(Constants.RT_EXTEND_MILLISECONDS);
                mTimer.Elapsed += (_, _) => {
                    // timer elapsed
                    Log.Information("CloudBackgroundService::RT timer elapsed");
                    mRTDataRequested = false;
                    mCloudSignalRSenderService.TimerElapsed();
                };
                mTimer.AutoReset = false;
                mTimer.Enabled = true;
            }

            mRTDataRequested = true;
            return Task.CompletedTask;
        }

        public Task ExtendRTData() {
            Log.Information("CloudBackgroundService::RT extended");

            // extend timer
            mTimer.Interval = Constants.RT_EXTEND_MILLISECONDS;

            mRTDataRequested = true;
            return Task.CompletedTask;
        }

        public Task StopRTData() {
            Log.Information("CloudBackgroundService::RT stopped");

            mTimer.Enabled = false;

            mRTDataRequested = false;
            return Task.CompletedTask;
        }

        public Task ReceiveRTData(BufferedMeterDataRTDto _meterData) {
            Log.Information("CloudBackgroundService::Received RT Data");
            // TODO: do something with it
            return Task.CompletedTask;
        }

        public async Task UpdateSmartMeter(CloudSmartMeterDto _cloudSmartMeterDto) {
            Log.Information("CloudBackgroundService::UpdateSmartMeter");
            await mLocalChangesService.SmartMeterChanged(_cloudSmartMeterDto);
        }
    }
}
