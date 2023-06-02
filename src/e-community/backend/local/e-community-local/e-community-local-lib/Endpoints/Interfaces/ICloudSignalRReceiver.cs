using e_community_local_lib.CloudData;
using e_community_local_lib.CloudData.Local;
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
        /// smart meter specific data should be updated locally
        /// </summary>
        /// <param name="_localSmartMeterDto">smart meter specific data</param>
        /// <returns></returns>
        Task UpdateSmartMeter(CloudSmartMeterDto _localSmartMeterDto);

        /// <summary>
        /// cloud requests information about the local ethereum account (balances)
        /// </summary>
        Task RequestBlockchainAccountBalance();

        /// <summary>
        /// cloud requests hourly forecast data (load profile and flexibility)
        /// </summary>
        Task RequestHourlyForecast();

        /// <summary>
        /// cloud requests meter data for monitoring
        /// </summary>
        Task RequestMeterDataMonitoring();
        
        /// <summary>
        /// creates a new consent contract on the blockchain
        /// </summary>
        /// <param name="_consentContractModel"></param>
        /// <returns></returns>
        Task CreateConsentContract(ConsentContractModel _consentContractModel);

        /// <summary>
        /// get the contracts belonging to the member
        /// </summary>
        /// <returns></returns>
        Task RequestContractsForMember();

        /// <summary>
        /// deposit the totalPrice to the smart contract wallet
        /// </summary>
        /// <param name="_consentContractModel"></param>
        /// <returns></returns>
        Task RequestDepositToContract(ConsentContractModel _consentContractModel);
        
        /// <summary>
        /// withdraws the totalPrice from the smart contract wallet
        /// to the wallet of consenter
        /// </summary>
        /// <param name="_consentContractModel"></param>
        /// <returns></returns>
        Task RequestWithdrawFromContract(ConsentContractModel _consentContractModel);

        /// <summary>
        /// request historic data from the member
        /// </summary>
        /// <param name="_requestHistoryModel"></param>
        /// <returns></returns>
        Task RequestHistoryData(RequestHistoryModel _requestHistoryModel);

        /// <summary>
        /// update the contract state
        /// </summary>
        /// <param name="_updateContractState"></param>
        /// <returns></returns>
        Task RequestUpdateContractState(UpdateContractState _updateContractState);
    }
}
