using e_community_cloud_lib.Endpoints.Dtos;
using System;
using System.Collections.Generic;
using System.Text;
using e_community_cloud_lib.Models.Blockchain;

namespace e_community_cloud_lib.BusinessLogic.Interfaces.SignalR
{
    /// <summary>
    /// called by hub which listens to the Smart Meter (handle received content)
    /// </summary>
    public interface ILocalSignalRReceiverService
    {
        /// <summary>
        /// handle received realtime data
        /// </summary>
        /// <param name="_meterData">near real time meter data</param>
        /// <param name="_memberId">member id</param>
        void ReceivedRTData(LocalMeterDataRTDto _meterData, Guid _memberId);

        /// <summary>
        /// real time timer elapsed (end device must extend real time sessions after 5 minutes) --> stop RT services
        /// </summary>
        /// <param name="memberId">member id</param>
        void TimerElapsed(Guid memberId);

        /// <summary>
        /// handle received ethereum account balance
        /// </summary>
        /// <param name="memberId">member id</param>
        void ReceivedBlockchainAccountBalance(Guid memberId, BlockchainAccountBalanceDto _blockchainAccountBalance);
        
        /// <summary>
        /// receive the created consent contract
        /// </summary>
        /// <param name="_consentContractModel"></param>
        /// <returns></returns>
        void ReceiveCreateConsentContract(Guid memberId, ConsentContractModel _consentContractModel);

        /// <summary>
        /// receives the contracts for the member in a list
        /// </summary>
        /// <param name="_memberId"></param>
        /// <param name="_contractsForMembers"></param>
        void ReceiveContractsForMember(Guid _memberId, IList<ConsentContractModel> _contractsForMember);
        
        /// <summary>
        /// receives the balance of the smart contract wallet
        /// </summary>
        /// <param name="_memberId"></param>
        /// <param name="_contractsForMembers"></param>
        void ReceiveDepositToContract(Guid _memberId, string _contractBalance);

        /// <summary>
        /// receives the new balance of the consenter's wallet
        /// </summary>
        /// <param name="_memberId"></param>
        /// <param name="_accountBalance"></param>
        void ReceiveWithdrawFromContract(Guid _memberId, string _accountBalance);

        /// <summary>
        /// receives the history energy data
        /// </summary>
        /// <param name="_memberId"></param>
        /// <param name="_meterDataHistDto"></param>
        void ReceiveHistoryData(Guid _memberId, MeterDataHistDto _meterDataHistDto);
    }
}
