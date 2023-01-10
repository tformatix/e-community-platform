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
    }
}
