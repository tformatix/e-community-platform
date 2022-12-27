using e_community_local_lib.CloudDtos;
using e_community_local_lib.CloudDtos.Local;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace e_community_local_lib.Endpoints.Interfaces
{
    /// <summary>
    /// provided SignalR methods of Local
    /// </summary>
    public interface ICloudSignalRReceiver
    {
        /// <summary>
        /// cloud requests real time data from local
        /// </summary>
        Task RequestRTData();

        /// <summary>
        /// cloud extends real time data connection from local (otherwise local backend stops real time traffic)
        /// </summary>
        Task ExtendRTData();

        /// <summary>
        /// cloud stops real time data stream from local
        /// </summary>
        Task StopRTData();

        /// <summary>
        /// cloud sends buffered real time data to smart meter
        /// </summary>
        Task ReceiveRTData(BufferedMeterDataRTDto _meterData);

        /// <summary>
        /// smart meter specific data should be updated locally
        /// </summary>
        /// <param name="_localSmartMeterDto">smart meter specific data</param>
        /// <returns></returns>
        Task UpdateSmartMeter(CloudSmartMeterDto _localSmartMeterDto);

        /// <summary>
        /// cloud requests information about the local ethereum account (balances)
        /// </summary>
        Task RequestBlockchainAccountBalance();
    }
}
