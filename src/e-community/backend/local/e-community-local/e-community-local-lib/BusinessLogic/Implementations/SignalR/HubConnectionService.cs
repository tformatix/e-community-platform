using e_community_local_lib.BusinessLogic.Interfaces.REST;
using e_community_local_lib.BusinessLogic.Interfaces.SignalR;
using e_community_local_lib.CloudData;
using e_community_local_lib.CloudData.Local;
using e_community_local_lib.Endpoints;
using e_community_local_lib.Endpoints.Interfaces;
using Microsoft.AspNetCore.SignalR.Client;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Serilog;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_local_lib.BusinessLogic.Implementations.SignalR {
    public class HubConnectionService : IHubConnectionService {

        private HubConnection mHubConnection;
        private readonly IConfiguration mConfiguration;
        private readonly IServiceScopeFactory mServiceScopeFactory;

        public HubConnectionService(
            IConfiguration _configuration,
            IServiceScopeFactory _serviceScopeFactory
        ) {
            mConfiguration = _configuration;
            mServiceScopeFactory = _serviceScopeFactory;
        }

        public async Task<HubConnection> GetHubConnection() {
            if (mHubConnection == null) {
                try {
                    // refresh token
                    LoginDto login;
                    using (var scope = mServiceScopeFactory.CreateScope()) {
                        var cloudRESTService = scope.ServiceProvider.GetRequiredService<ICloudRESTService>();
                        var cloudBackgroundService = scope.ServiceProvider.GetRequiredService<ICloudBackgroundService>();
                        var cloudSignalRSenderService = scope.ServiceProvider.GetRequiredService<ICloudSignalRSenderService>();

                        login = await cloudRESTService.RefreshFromDb();
                        if (login == null) {
                            Log.Error("HubConnectionService::Refreshing Failed");
                            // refreshing failed
                            // TODO: inform user
                            return null;
                        }

                        Log.Information("HubConnectionService::Build HubConnection");

                        // get paths
                        var section = mConfiguration.GetSection("CloudPaths");
                        var basePath = section.GetValue<string>("Base");
                        var signalRPath = section.GetValue<string>("SignalR");

                        // create hub connection
                        mHubConnection = new HubConnectionBuilder()
                            .WithUrl($"{basePath}/{signalRPath}", _options => {
                                _options.AccessTokenProvider = () => {
                                    return Task.FromResult(login.AccessToken);
                                };
                            })
                            .Build();

                        mHubConnection?.On(nameof(ICloudSignalRReceiver.RequestRTData), () => cloudBackgroundService.RequestRTData());
                        mHubConnection?.On(nameof(ICloudSignalRReceiver.ExtendRTData), () => cloudBackgroundService.ExtendRTData());
                        mHubConnection?.On(nameof(ICloudSignalRReceiver.StopRTData), () => cloudBackgroundService.StopRTData());
                        mHubConnection?.On(nameof(ICloudSignalRReceiver.UpdateSmartMeter),
                            (CloudSmartMeterDto _cloudSmartMeterDto) => cloudBackgroundService.UpdateSmartMeter(_cloudSmartMeterDto)
                        );
                        mHubConnection?.On(nameof(ICloudSignalRReceiver.RequestHourlyForecast), () => cloudBackgroundService.RequestHourlyForecast());
                        mHubConnection?.On(nameof(ICloudSignalRReceiver.RequestMeterDataMonitoring), () => cloudBackgroundService.RequestMeterDataMonitoring());

                        // blockchain connections
                        mHubConnection?.On(nameof(ICloudSignalRReceiver.RequestBlockchainAccountBalance),
                            () => cloudBackgroundService.RequestBlockchainAccountBalance());
                        mHubConnection?.On(nameof(ICloudSignalRReceiver.CreateConsentContract),
                            (ConsentContractModel _consentContractModel) => cloudBackgroundService.CreateConsentContract(_consentContractModel));
                        mHubConnection?.On(nameof(ICloudSignalRReceiver.RequestContractsForMember),
                            () => cloudBackgroundService.RequestContractsForMember());
                        mHubConnection?.On(nameof(ICloudSignalRReceiver.RequestDepositToContract),
                            (ConsentContractModel _consentContractModel) => cloudBackgroundService.RequestDepositToContract(_consentContractModel));
                        mHubConnection?.On(nameof(ICloudSignalRReceiver.RequestWithdrawFromContract),
                            (ConsentContractModel _consentContractModel) => cloudBackgroundService.RequestWithdrawFromContract(_consentContractModel));
                        mHubConnection?.On(nameof(ICloudSignalRReceiver.RequestHistoryData),
                            (RequestHistoryModel _requestHistoryModel) =>
                                cloudBackgroundService.RequestHistoryData(_requestHistoryModel));
                        mHubConnection?.On(nameof(ICloudSignalRReceiver.RequestUpdateContractState),
                            (UpdateContractState _updateContractState) =>
                                cloudBackgroundService.RequestUpdateContractState(_updateContractState));

                        
                        await mHubConnection?.StartAsync();
                        if (mHubConnection?.State == HubConnectionState.Connected) {
                            await cloudSignalRSenderService.AssignSmartMeterId();
                        }
                    }

                    Log.Information("HubConnectionService::SignalR initiated");
                }
                catch (Exception _exc) {
                    Log.Error($"HubConnectionService::Error while building hub connection::{_exc.Message}");
                }
            }

            return mHubConnection;
        }

        public async Task<HubConnection> Reconnect() {
            Log.Information("HubConnectionService::Reconnect");
            if(mHubConnection != null) await mHubConnection?.StopAsync();
            mHubConnection = null;
            return await GetHubConnection();
        }

        public async Task InvokeSignalR(String _method) {
            try {
                var hubConnection = await GetHubConnection();
                await hubConnection?.InvokeAsync(_method);
            }
            catch (Exception _exc) {
                await SignalRErrorHandling(_exc);
            }
        }

        public async Task InvokeSignalR(String _method, object _object) {
            try {
                mHubConnection?.InvokeAsync(_method, _object);
            }
            catch (Exception _exc) {
                await SignalRErrorHandling(_exc);
            }
        }

        private async Task SignalRErrorHandling(Exception _exc) {
            Log.Error($"HubConnectionService::InvokeSignalR {_exc.Message}");
            await Reconnect();
        }
    }
}
