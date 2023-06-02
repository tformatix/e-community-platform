using e_community_local_lib.BusinessLogic.Interfaces;
using e_community_local_lib.BusinessLogic.Interfaces.SignalR;
using e_community_local_lib.CloudData.Local;
using e_community_local_lib.Endpoints.Interfaces;
using Microsoft.AspNetCore.SignalR.Client;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Serilog;
using System;
using System.Threading.Tasks;
using System.Threading;
using e_community_local_lib.Util;
using e_community_local_lib.CloudData;
using e_community_local_lib.BusinessLogic.Implementations;
using e_community_local_lib.BusinessLogic.Interfaces.REST;

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
                            await RequestHourlyForecast(); // send forecast
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

        public async Task UpdateSmartMeter(CloudSmartMeterDto _cloudSmartMeterDto) {
            Log.Information("CloudBackgroundService::UpdateSmartMeter");
            await mLocalChangesService.SmartMeterChanged(_cloudSmartMeterDto);
        }

        public async Task RequestBlockchainAccountBalance()
        {
            Log.Information("CloudBackgroundService::BlockchainAccountBalance requested");

            using (var scope = mServiceScopeFactory.CreateScope())
            {
                var cloudSignalR = scope.ServiceProvider.GetRequiredService<ICloudSignalRSenderService>();
                await cloudSignalR.SendBlockchainAccountBalance();
            }
        }

        public async Task RequestHourlyForecast() {
            Log.Information("CloudBackgroundService::Hourly forecast requested");

            using (var scope = mServiceScopeFactory.CreateScope()) {
                var cloudRESTService = scope.ServiceProvider.GetRequiredService<ICloudRESTService>();
                var forecastService = scope.ServiceProvider.GetRequiredService<IForecastService>();
                await cloudRESTService.SendHourlyForecast(await forecastService.GetHourlyForecast());
            }
        }

        public async Task RequestMeterDataMonitoring() {
            Log.Information("CloudBackgroundService::Meter Data requested for monitoring");

            using (var scope = mServiceScopeFactory.CreateScope()) {
                var cloudRESTService = scope.ServiceProvider.GetRequiredService<ICloudRESTService>();
                var meterDataService = scope.ServiceProvider.GetRequiredService<IMeterDataService>();
                var meterData = await meterDataService.GetMeterDataMonitoring();
                if (meterData != null) {
                    await cloudRESTService.SendMeterDataMonitoring(meterData);
                }
            }
        }

        public async Task CreateConsentContract(ConsentContractModel _consentContractModel)
        {
            Log.Information("CloudBackgroundService::CreateConsentContract requested");

            using (var scope = mServiceScopeFactory.CreateScope())
            {
                var cloudSignalR = scope.ServiceProvider.GetRequiredService<ICloudSignalRSenderService>();
                await cloudSignalR.SendCreateConsentContract(_consentContractModel);
            }
        }

        public async Task RequestContractsForMember()
        {
            Log.Information("CloudBackgroundService::RequestContractsForMember requested");

            using (var scope = mServiceScopeFactory.CreateScope())
            {
                var cloudSignalR = scope.ServiceProvider.GetRequiredService<ICloudSignalRSenderService>();
                await cloudSignalR.SendContractsForMember();
            }
        }

        public async Task RequestDepositToContract(ConsentContractModel _consentContractModel)
        {
            Log.Information("CloudBackgroundService::RequestDepositToContract requested");

            using (var scope = mServiceScopeFactory.CreateScope())
            {
                var cloudSignalR = scope.ServiceProvider.GetRequiredService<ICloudSignalRSenderService>();
                await cloudSignalR.SendDepositToContract(_consentContractModel);
            }
        }

        public async Task RequestWithdrawFromContract(ConsentContractModel _consentContractModel)
        {
            Log.Information("CloudBackgroundService::RequestWithdrawFromContract requested");

            using (var scope = mServiceScopeFactory.CreateScope())
            {
                var cloudSignalR = scope.ServiceProvider.GetRequiredService<ICloudSignalRSenderService>();
                await cloudSignalR.SendWithdrawFromContract(_consentContractModel);
            }
        }

        public async Task RequestHistoryData(RequestHistoryModel _requestHistoryModel)
        {
            Log.Information("CloudBackgroundService::RequestHistoryData requested");

            using (var scope = mServiceScopeFactory.CreateScope())
            {
                var cloudSignalR = scope.ServiceProvider.GetRequiredService<ICloudSignalRSenderService>();
                await cloudSignalR.SendHistoryData(_requestHistoryModel);
            }
        }

        public async Task RequestUpdateContractState(UpdateContractState _updateContractState)
        {
            Log.Information("CloudBackgroundService::RequestUpdateContractState requested");

            using (var scope = mServiceScopeFactory.CreateScope())
            {
                var cloudSignalR = scope.ServiceProvider.GetRequiredService<ICloudSignalRSenderService>();
                await cloudSignalR.SendUpdateContractState(_updateContractState);
            }
        }
    }
}
