using e_community_cloud_lib.Endpoints.Dtos;
using e_community_cloud_lib.NonEntities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using e_community_cloud_lib.Models.Blockchain;

namespace e_community_cloud_lib.BusinessLogic.Interfaces.SignalR {
    public interface IRTListenerSignalRSenderService {
        /// <summary>
        /// send realtime data to member devices incl. buffering
        /// </summary>
        /// <param name="_memberId">memeber id</param>
        /// <param name="_listenerData">listener specific data</param>
        /// <param name="_buffer">buffer for real time data</param>
        void SendToMembersBuffered(Guid _memberId, RTListenerData _listenerData, RTMeterDataBuffer _buffer);

        /// <summary>
        /// send realtime data to member devices
        /// </summary>
        /// <param name="_meterData">meter data</param>
        /// <param name="_memberId">member id</param>
        void SendToMember(Guid _memberId, BufferedMeterDataRTDto _meterData);

        /// <summary>
        /// send ethereum account balance to member device
        /// </summary>
        /// <param name="_memberId"></param>
        void SendToMemberBlockchainAccountBalance(Guid _memberId, BlockchainAccountBalanceDto _blockchainAccountBalance);

        /// <summary>
        /// send the created consent contract to member
        /// </summary>
        /// <param name="_memberId"></param>
        /// <param name="_consentContractModel"></param>
        void SendToMemberCreateConsentContract(Guid _memberId, ConsentContractModel _consentContractModel);

        /// <summary>
        /// send the contracts for the member
        /// </summary>
        /// <param name="_memberId"></param>
        /// <param name="_contractsForMember"></param>
        void SendToMemberContractsForMember(Guid _memberId, IList<ConsentContractModel> _contractsForMember);
        
        /// <summary>
        /// send the balance of the smart contract wallet for the member
        /// </summary>
        /// <param name="_memberId"></param>
        /// <param name="_contractBalance"></param>
        void SendToMemberDepositToContract(Guid _memberId, string _contractBalance);
        
        /// <summary>
        /// send the balance of the consenter's wallet
        /// </summary>
        /// <param name="_memberId"></param>
        /// <param name="_contractBalance"></param>
        void SendToMemberWithdrawFromContract(Guid _memberId, string _accountBalance);

        /// <summary>
        /// send to member the history data
        /// </summary>
        /// <param name="_memberId"></param>
        /// <param name="_meterDataHistDto"></param>
        void SendToMemberHistoryData(Guid _memberId, MeterDataHistDto _meterDataHistDto);
    }
}
