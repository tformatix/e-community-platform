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

        /// <summary>
        /// send the contracts back to the cloud
        /// </summary>
        /// <returns></returns>
        Task SendContractsForMember();

        /// <summary>
        /// deposit the totalPrice to the smart contract wallet
        /// </summary>
        /// <param name="_consentContractModel"></param>
        /// <returns></returns>
        Task SendDepositToContract(ConsentContractModel _consentContractModel);
        
        /// <summary>
        /// withdraws the totalPrice from the smart contract wallet
        /// to the wallet of consenter
        /// </summary>
        /// <param name="_consentContractModel"></param>
        /// <returns></returns>
        Task SendWithdrawFromContract(ConsentContractModel _consentContractModel);

        /// <summary>
        /// send history date from the member
        /// </summary>
        /// <param name="_requestHistoryModel"></param>
        /// <returns></returns>
        Task SendHistoryData(RequestHistoryModel _requestHistoryModel);
        
        /// <summary>
        /// update the contract state
        /// </summary>
        /// <param name="_updateContractState"></param>
        /// <returns></returns>
        Task SendUpdateContractState(UpdateContractState _updateContractState);

        /// <summary>
        /// execute a defined python script (with args)
        /// </summary>
        /// <param name="_command">command to execute</param>
        /// <returns></returns>
        string ExecutePythonCommand(string _command, string _args);
    }
}
