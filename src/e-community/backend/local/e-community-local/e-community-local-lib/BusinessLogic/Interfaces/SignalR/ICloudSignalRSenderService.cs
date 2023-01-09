using e_community_local_lib.BusinessLogic.Interfaces.REST;
using e_community_local_lib.Database;
using Microsoft.AspNetCore.SignalR.Client;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;
using e_community_local_lib.CloudData;

namespace e_community_local_lib.BusinessLogic.Interfaces.SignalR
{
    /// <summary>
    /// SignalR sender to the cloud (send something to a cloud)
    /// </summary>
    public interface ICloudSignalRSenderService
    {
        /// <summary>
        /// add to groups (member and eCommunity)
        /// </summary>
        Task AssignSmartMeterId();

        /// <summary>
        /// send realtime data
        /// </summary>
        Task SendRTData();

        /// <summary>
        /// real time timer elapsed (end device must extend real time sessions after 5 minutes)
        /// </summary>
        Task TimerElapsed();

        /// <summary>
        /// send ethereum account balance
        /// </summary>
        /// <returns></returns>
        Task SendBlockchainAccountBalance();

        /// <summary>
        /// creates a new consent contract on the blockchain
        /// </summary>
        /// <param name="_consentContractModel"></param>
        /// <returns></returns>
        Task SendCreateConsentContract(ConsentContractModel _consentContractModel);
    }
}
