using e_community_local_lib.CloudData;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace e_community_local_lib.Endpoints.Interfaces
{
    /// <summary>
    /// provided SignalR methods of cloud
    /// </summary>
    public interface ICloudSignalRSender
    {
        /// <summary>
        /// add to eCommunity group
        /// </summary>
        /// <param name="_smartMeterId">id of smart meter</param>
        Task AssignSmartMeterId(Guid _smartMeterId);

        /// <summary>
        /// cloud receives realtime data from Smart Meter
        /// </summary>
        /// <param name="_meterData">near real time meter data</param>
        Task ReceiveRTData(MeterDataRTDto _meterData);

        /// <summary>
        /// real time timer elapsed (end device must extend real time sessions after 5 minutes)
        /// </summary>
        Task TimerElapsed();

        /// <summary>
        /// cloud receives ethereum account balances
        /// </summary>
        /// <param name="_blockchainAccountBalance"></param>
        /// <returns></returns>
        Task ReceiveBlockchainAccountBalance(BlockchainAccountBalanceDto _blockchainAccountBalance);

        /// <summary>
        /// receive the created contract on an address
        /// </summary>
        /// <param name="_consentContractDto"></param>
        /// <returns></returns>
        Task ReceiveCreateConsentContract(ConsentContractDto _consentContractDto);

        /// <summary>
        /// receive the contracts for the member
        /// </summary>
        /// <param name="_contractsForMember"></param>
        /// <returns></returns>
        Task ReceiveContractsForMember(IList<ConsentContractModel> _contractsForMember);

        /// <summary>
        /// receive the balance of the smart contract wallet
        /// </summary>
        /// <param name="_contractBalance"></param>
        /// <returns></returns>
        Task ReceiveDepositToContract(string _contractBalance);
        
        /// <summary>
        /// receive the balance of consenter's wallet
        /// </summary>
        /// <param name="_accountBalance"></param>
        /// <returns></returns>
        Task ReceiveWithdrawFromContract(string _accountBalance);

        /// <summary>
        /// receive the history energy data
        /// </summary>
        /// <param name="_meterDataHistDto"></param>
        /// <returns></returns>
        Task ReceiveHistoryData(MeterDataHistDto _meterDataHistDto);
    }
}
