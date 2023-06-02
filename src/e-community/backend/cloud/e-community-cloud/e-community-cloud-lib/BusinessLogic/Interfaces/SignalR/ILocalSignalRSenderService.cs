using e_community_cloud_lib.Models.SmartMeter;
using e_community_cloud_lib.NonEntities;
using e_community_cloud_lib.Util;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;
using e_community_cloud_lib.Models.Blockchain;
using e_community_cloud_lib.Models.History;

namespace e_community_cloud_lib.BusinessLogic.Interfaces.SignalR
{
    /// <summary>
    /// SignalR sender to the Smart Meter (send something to a Smart Meter)
    /// </summary>
    public interface ILocalSignalRSenderService
    {
        /// <summary>
        /// request realtime data from smart meters (for 5 minutes)
        /// </summary>
        void RequestRTData(Guid? _memberId);

        /// <summary>
        /// request realtime data for member with missing smart meters
        /// </summary>
        void RequestRTDataFaulty(string _signalRGroupName);

        /// <summary>
        /// real time data connection should be extended (every 5 minutes)
        /// </summary>
        void ExtendRTData(Guid? _memberId);

        /// <summary>
        /// stop realtime data stream from smart meter
        /// </summary>
        void StopRTData(Guid? _memberId);

        /// <summary>
        /// updates smart meter in database
        /// </summary>
        /// <param name="_updateSmartMeterModel">changes to smart meter</param>
        void UpdateSmartMeter(UpdateSmartMeterModel _updateSmartMeterModel);

        /// <summary>
        /// cloud requests hourly forecast data (load profile and flexibility)
        /// </summary>
        void RequestHourlyForecast();

        /// <summary>
        /// cloud requests meter data for monitoring
        /// </summary>
        void RequestMeterDataMonitoring();

        /// <summary>
        /// cloud requests information about the local ethereum account (balances)
        /// </summary>
        void RequestBlockchainAccountBalance(Guid? _memberId);

        /// <summary>
        /// creates a new consent contract on the blockchain
        /// </summary>
        /// <param name="_memberId"></param>
        /// <param name="_consentContractModel"></param>
        void CreateConsentContract(Guid? _memberId, ConsentContractModel _consentContractModel);

        /// <summary>
        /// cloud requests the contracts were the member is involved
        /// either proposer or consenter
        /// </summary>
        /// <param name="_memberId"></param>
        void RequestContractsForMember(Guid? _memberId);

        /// <summary>
        /// cloud request to deposit the totalPrice to the smart contract wallet
        /// </summary>
        /// <param name="_memberId"></param>
        /// <param name="consentContract"></param>
        void RequestDepositToContract(Guid? _memberId, ConsentContractModel consentContract);
        
        /// <summary>
        /// cloud request to withdraw the totalPrice from the smart contract wallet
        /// </summary>
        /// <param name="_memberId"></param>
        /// <param name="consentContract"></param>
        void RequestWithdrawFromContract(Guid? _memberId, ConsentContractModel consentContract);

        /// <summary>
        /// cloud requests the history data from a specific member
        /// </summary>
        /// <param name="_requestHistoryModel">Given Timespan and requested MemberId</param>
        void RequestHistoryData(RequestHistoryModel _requestHistoryModel);

        /// <summary>
        /// updates the state of the contract (accepted, rejected, revoked)
        /// </summary>
        /// <param name="memberId"></param>
        /// <param name="updateContractState"></param>
        void UpdateContractState(Guid? _memberId, UpdateContractState _updateContractState);
    }
}
