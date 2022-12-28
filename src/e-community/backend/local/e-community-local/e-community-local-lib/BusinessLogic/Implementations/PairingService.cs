using e_community_local_lib.BusinessLogic.Interfaces;
using e_community_local_lib.BusinessLogic.Interfaces.REST;
using e_community_local_lib.Database;
using e_community_local_lib.Database.General;
using e_community_local_lib.Models;
using e_community_local_lib.NonEntities;
using e_community_local_lib.Util;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;
using e_community_local_lib.Util.Extensions;
using e_community_local_lib.Endpoints;
using e_community_local_lib.BusinessLogic.Interfaces.SignalR;
using e_community_local_lib.Database.PriceRate;
using Microsoft.Extensions.Hosting;
using e_community_local_lib.Util.BusinessLogic;

namespace e_community_local_lib.BusinessLogic.Implementations
{
    public class PairingService : IPairingService
    {
        private readonly ECommunityLocalContext mDb;
        private readonly ICloudBackgroundService mCloudBackgroundService;
        private readonly ICloudRESTService mCloudRESTService;
        private static readonly HttpClient mHttpClient = new();

        public PairingService(ECommunityLocalContext _db, ICloudRESTService _cloudRESTService, ICloudBackgroundService _cloudBackgroundService)
        {
            mDb = _db;
            mCloudRESTService = _cloudRESTService;
            mCloudBackgroundService = _cloudBackgroundService;
        }

        public async Task<Status> CurrentStatus()
        {
            var member = await mDb.Member.FirstOrDefaultAsync();
            var smartMeter = await mDb.SmartMeter.FirstOrDefaultAsync();
            return new Status()
            {
                MemberId = member?.Id,
                IsConnectedToInternet = await GetConnectedToNetwork(),
                IsWiredConnected = IsWiredConnected(),
                WifiSSID = GetWifiNetwork(),
                SmartMeter = smartMeter
            };
        }

        public Task NetworkAdd(NetworkConnectModel _networkConnectModel)
        {
            string networkAdd = $"Util/Extensions/Python/networking.py add -s \"{_networkConnectModel.SSID}\" -p \"{_networkConnectModel.Password}\"";

            Process processNetworkAdd = new Process();
            processNetworkAdd.StartInfo = new ProcessStartInfo(Constants.PYTHON_EXE, networkAdd);

            processNetworkAdd.Start();
            processNetworkAdd.WaitForExit();
            processNetworkAdd.Close();

            string networkConnect = $"Util/Extensions/Python/networking.py restart_wifi_driver";

            // fetzt
            // https://www.linuxbabe.com/command-line/ubuntu-server-16-04-wifi-wpa-supplicant
            Process processNetworkConnect = new Process();

            processNetworkConnect.StartInfo = new ProcessStartInfo(Constants.PYTHON_EXE, networkConnect);

            processNetworkConnect.Start();
            processNetworkConnect.WaitForExit();
            processNetworkConnect.Close();

            return Task.CompletedTask;
        }

        public List<NetworkDiscoveryModel> NetworkDiscovery()
        {
            Process processNetworkDiscovery = new Process();

            processNetworkDiscovery.StartInfo = new ProcessStartInfo(Constants.PYTHON_EXE, Constants.NETWORK_DISCOVERY)
            {
                RedirectStandardOutput = true
            };
            processNetworkDiscovery.Start();

            string output = processNetworkDiscovery.StandardOutput.ReadToEnd();

            var availableNetworksList = JsonConvert.DeserializeObject<List<NetworkDiscoveryModel>>(output);
            availableNetworksList = availableNetworksList.DistinctBy(x => x.SSID).ToList();

            processNetworkDiscovery.WaitForExit();
            processNetworkDiscovery.Close();

            return availableNetworksList;
        }

        /// <summary>
        /// checks if the raspberry is connected via wired connection (ethernet)
        /// </summary>
        private bool IsWiredConnected()
        {
            Process processIsWiredConnection = new Process();

            processIsWiredConnection.StartInfo = new ProcessStartInfo(Constants.PYTHON_EXE, Constants.IS_WIRED_CONNECTED)
            {
                RedirectStandardOutput = true
            };
            processIsWiredConnection.Start();

            string connectionState = processIsWiredConnection.StandardOutput.ReadToEnd().Trim();

            processIsWiredConnection.WaitForExit();
            processIsWiredConnection.Close();

            return connectionState.Equals(Constants.STATUS_ETH0_UP);
        }

        /// <summary>
        /// checks if the raspberry is connected to a wifi network
        /// if yes, get the wifi ssid
        /// </summary>
        private string GetWifiNetwork()
        {
            Process processGetWifiSSID = new Process();

            processGetWifiSSID.StartInfo = new ProcessStartInfo(Constants.PYTHON_EXE, Constants.GET_WIFI_SSID)
            {
                RedirectStandardOutput = true
            };
            processGetWifiSSID.Start();

            string wifiSSID = processGetWifiSSID.StandardOutput.ReadToEnd().Trim();

            processGetWifiSSID.WaitForExit();
            processGetWifiSSID.Close();

            return wifiSSID;
        }

        public async Task CloudConnect(CloudConnectModel _cloudConnectModel)
        {
            if(_cloudConnectModel.SmartMeter == null)
            {
                throw new ServiceException(ServiceException.Type.SMART_METER_NULL);
            }
            mDb.SmartMeter.RemoveRange(mDb.SmartMeter);
            await mDb.SmartMeter.AddAsync(_cloudConnectModel.SmartMeter.CopyPropertiesTo(new SmartMeter()));

            var local = await mCloudRESTService.GetLocalDataPairing(_cloudConnectModel.SmartMeter.Id, _cloudConnectModel.RefreshToken);

            if (local.BatterySystems.Count() > 0)
            {
                mDb.BatterySystem.RemoveRange(mDb.BatterySystem);
                await mDb.BatterySystem.AddRangeAsync(local.BatterySystems
                    .Select(x => x.CopyPropertiesTo(new BatterySystem()))
                    .ToList()
                );
            }

            if (local.PVSystems.Count() > 0)
            {
                mDb.PVSystem.RemoveRange(mDb.PVSystem);
                await mDb.PVSystem.AddRangeAsync(local.PVSystems
                    .Select(x => x.CopyPropertiesTo(new PVSystem()))
                    .ToList()
                );
            }

            if (local.SupplierPriceRate != null)
            {
                mDb.SupplierPriceRate.RemoveRange(mDb.SupplierPriceRate);
                await mDb.SupplierPriceRate.AddAsync(local.SupplierPriceRate.CopyPropertiesTo(new SupplierPriceRate()));
            }

            if (local.GridPriceRate != null)
            {
                mDb.GridPriceRate.RemoveRange(mDb.GridPriceRate);
                await mDb.GridPriceRate.AddAsync(local.GridPriceRate.CopyPropertiesTo(new GridPriceRate()));
            }

            if (local.Charges != null && local.Charges.Count() > 0)
            {
                mDb.Charge.RemoveRange(mDb.Charge);
                await mDb.Charge.AddRangeAsync(local.Charges
                    .Select(x => x.CopyPropertiesTo(new Charge()))
                    .ToList()
                );
            }

            if (local.Member != null)
            {
                mDb.Member.RemoveRange(mDb.Member);
                await mDb.Member.AddAsync(local.Member.CopyPropertiesTo(new Member()));
            }

            if (local.ECommunity != null)
            {
                mDb.ECommunity.RemoveRange(mDb.ECommunity);
                await mDb.ECommunity.AddAsync(local.ECommunity.CopyPropertiesTo(new ECommunity()));
            }

            await mDb.SaveChangesAsync();
            _ = mCloudBackgroundService.ExecuteAsync();
        }

        private async Task<bool> GetConnectedToNetwork()
        {
            try
            {
                await mHttpClient.GetAsync(Constants.CONNECTIVITY_TEST_URL);
                return true;
            }
            catch
            {
                return false;
            }
        }
    }
}