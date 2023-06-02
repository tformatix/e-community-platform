using e_community_local_lib.BusinessLogic.Interfaces.REST;
using e_community_local_lib.CloudData;
using e_community_local_lib.CloudData.Local;
using e_community_local_lib.Database;
using e_community_local_lib.Database.General;
using e_community_local_lib.Database.Meter;
using e_community_local_lib.Models;
using e_community_local_lib.Util.BusinessLogic;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Newtonsoft.Json;
using Serilog;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text;
using System.Threading.Tasks;

namespace e_community_local_lib.BusinessLogic.Implementations.REST {
    public class CloudRESTService : ICloudRESTService {
        private readonly ECommunityLocalContext mDb;
        private readonly IConfiguration mConfiguration;
        private readonly IConfigurationSection mSection;
        private readonly string mBasePath;
        private static readonly HttpClient mHttpClient = new();

        public CloudRESTService(IConfiguration _configuration, ECommunityLocalContext _db) {
            mDb = _db;
            mConfiguration = _configuration;
            mSection = mConfiguration.GetSection("CloudPaths");
            mBasePath = mSection.GetValue<string>("Base");
        }

        public async Task<LoginDto> Refresh(string _refreshToken) {
            Log.Information("CloudRESTService::Refresh Token");
            var refreshPath = mSection.GetValue<string>("Refresh");
            var response = await mHttpClient.PostAsync($"{mBasePath}/{refreshPath}",
                new StringContent($"\"{_refreshToken}\"", Encoding.UTF8, "application/json"));
            if (response.IsSuccessStatusCode) {
                var result = response.Content.ReadAsStringAsync().Result;
                var login = JsonConvert.DeserializeObject<LoginDto>(result);

                // update credentials
                mDb.Credential.RemoveRange(mDb.Credential);
                mDb.Credential.Add(new Credential() {
                    AccessToken = login.AccessToken,
                    RefreshToken = login.RefreshToken
                });

                _ = await mDb.SaveChangesAsync();
                return login;
            }
            return null;
        }

        public async Task<LoginDto> RefreshFromDb() {
            var credentials = await mDb.Credential.FirstOrDefaultAsync();
            if (credentials == null)
                return null;
            return await Refresh(credentials.RefreshToken);
        }

        public async Task<CloudLocalDto> GetLocalDataPairing(Guid _smartMeterId, string _refreshToken) {
            Log.Information("CloudRESTService::Get Local Data");
            var login = await Refresh(_refreshToken);
            if (login == null) {
                throw new ServiceException(ServiceException.Type.INVALID_REFRESH_TOKEN);
            }
            var pairingPath = mSection.GetValue<string>("Pairing");
            using (var requestMessage = new HttpRequestMessage(HttpMethod.Get, $"{mBasePath}/{pairingPath}{_smartMeterId}")) {
                requestMessage.Headers.Authorization = new AuthenticationHeaderValue("Bearer", login.AccessToken);
                var response = await mHttpClient.SendAsync(requestMessage);
                if (response.IsSuccessStatusCode) {
                    var result = response.Content.ReadAsStringAsync().Result;
                    return JsonConvert.DeserializeObject<CloudLocalDto>(result);
                }
                return null;
            }
        }

        public async Task SendHourlyForecast(ForecastModel _forecastModel) {
            Log.Information("CloudRESTService::Send hourly forecast");

            var login = await RefreshFromDb();
            if (login == null) {
                Log.Error("CloudRESTService::Refreshing Failed");
            }
            else {
                var forecastPath = mSection.GetValue<string>("Forecast");
                using (var requestMessage = new HttpRequestMessage(HttpMethod.Post, $"{mBasePath}/{forecastPath}")) {
                    requestMessage.Content = new StringContent(JsonConvert.SerializeObject(_forecastModel), Encoding.UTF8, "application/json");
                    requestMessage.Headers.Authorization = new AuthenticationHeaderValue("Bearer", login.AccessToken);

                    var response = await mHttpClient.SendAsync(requestMessage);
                }
            }
        }

        public async Task SendMeterDataMonitoring(MeterDataMonitoringModel _meterDataMonitoringModel) {
            Log.Information("CloudRESTService::Send meter data for monitoring");

            var login = await RefreshFromDb();
            if (login == null) {
                Log.Error("CloudRESTService::Refreshing Failed");
            }
            else {
                var monitoringPath = mSection.GetValue<string>("MeterDataMonitoring");
                using (var requestMessage = new HttpRequestMessage(HttpMethod.Post, $"{mBasePath}/{monitoringPath}")) {
                    requestMessage.Content = new StringContent(JsonConvert.SerializeObject(_meterDataMonitoringModel), Encoding.UTF8, "application/json");
                    requestMessage.Headers.Authorization = new AuthenticationHeaderValue("Bearer", login.AccessToken);

                    var response = await mHttpClient.SendAsync(requestMessage);
                }
            }
        }
    }
}
