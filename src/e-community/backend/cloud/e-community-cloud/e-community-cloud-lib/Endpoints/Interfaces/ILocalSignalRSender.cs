using e_community_cloud_lib.Endpoints.Dtos;
using e_community_cloud_lib.LocalDtos;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.Endpoints.Interfaces
{
    /// <summary>
    /// provided SignalR methods of Local
    /// </summary>
    public interface ILocalSignalRSender
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
        /// smart meter specific data should be updated locally
        /// </summary>
        /// <param name="_localSmartMeterDto">smart meter specific data</param>
        /// <returns></returns>
        Task UpdateSmartMeter(LocalSmartMeterDto _localSmartMeterDto);

        /// <summary>
        /// cloud requests hourly forecast data (load profile and flexibility)
        /// </summary>
        Task RequestHourlyForecast();

        /// <summary>
        /// cloud requests meter data for monitoring
        /// </summary>
        Task RequestMeterDataMonitoring();

        /// <summary>
        /// cloud requests information about the local ethereum account (balances)
        /// </summary>
        Task RequestBlockchainAccountBalance();

        /// <summary>
        /// cloud receives information about the local ethereum account (balances)
        /// </summary>
        /// <returns></returns>
        Task ReceiveBlockchainAccountBalance(BlockchainAccountBalanceDto _blockchainAccountBalance);
    }
}
