using e_community_cloud_lib.Endpoints.Dtos;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;
using e_community_cloud_lib.Models.Blockchain;

namespace e_community_cloud_lib.Endpoints.Interfaces
{
    /// <summary>
    /// provided SignalR methods of cloud
    /// </summary>
    public interface ILocalSignalRReceiver
    {
        /// <summary>
        /// add to eCommunity group
        /// </summary>
        /// <param name="_smartMeterId">id of SmartMeter</param>
        Task AssignSmartMeterId(Guid _smartMeterId);

        /// <summary>
        /// cloud receives realtime data from Smart Meter
        /// </summary>
        /// <param name="_meterData">near real time meter data</param>
        Task ReceiveRTData(LocalMeterDataRTDto _meterData);

        /// <summary>
        /// real time timer elapsed (end device must extend real time sessions after 5 minutes)
        /// </summary>
        Task TimerElapsed();

        /// <summary>
        /// receive balance of the local ethereum account
        /// </summary>
        /// <param name="_blockchainAccountBalance"></param>
        /// <returns></returns>
        Task ReceiveBlockchainAccountBalance(BlockchainAccountBalanceDto _blockchainAccountBalance);

        /// <summary>
        /// receive the created consent contract
        /// </summary>
        /// <param name="_consentContractModel"></param>
        /// <returns></returns>
        Task ReceiveCreateConsentContract(ConsentContractModel _consentContractModel);

        /// <summary>
        /// receives the contracts for the member in a list
        /// </summary>
        /// <param name="_contractsForMember"></param>
        /// <returns></returns>
        Task ReceiveContractsForMember(IList<ConsentContractModel> _contractsForMember);
        
        /// <summary>
        /// receives the balance of the smart contract wallet
        /// </summary>
        /// <param name="_contractBalance"></param>
        /// <returns></returns>
        Task ReceiveDepositToContract(string _contractBalance);
        
        /// <summary>
        /// receives the balance of consenter's wallet
        /// </summary>
        /// <param name="_accountBalance"></param>
        /// <returns></returns>
        Task ReceiveWithdrawFromContract(string _accountBalance);

        /// <summary>
        /// receives the history data from a member
        /// </summary>
        /// <param name="_meterDataHistDto"></param>
        /// <returns></returns>
        Task ReceiveHistoryData(MeterDataHistDto _meterDataHistDto);
    }
}
