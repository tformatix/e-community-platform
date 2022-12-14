using e_community_cloud_lib.Endpoints.Dtos;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;
using e_community_cloud_lib.Models.Blockchain;

namespace e_community_cloud_lib.Endpoints.Interfaces
{
    /// <summary>
    /// provided SignalR methods of End Device
    /// </summary>
    public interface IEndDeviceSignalRSender
    {
        /// <summary>
        /// cloud sends buffered real time data from smart meters to end device
        /// </summary>
        Task ReceiveRTData(BufferedMeterDataRTDto _meterData);

        /// <summary>
        /// cloud sends ethereum account balance to end device
        /// </summary>
        /// <returns></returns>
        Task ReceiveBlockchainAccountBalance(BlockchainAccountBalanceDto _blockchainAccountBalance);
        
        /// <summary>
        /// creates a new consent contract on the blockchain
        /// </summary>
        /// <param name="_consentContractModel"></param>
        /// <returns></returns>
        Task CreateConsentContract(ConsentContractModel _consentContractModel);

        /// <summary>
        /// receives the created consent contract
        /// </summary>
        /// <param name="_consentContractModel"></param>
        /// <returns></returns>
        Task ReceiveCreateConsentContract(ConsentContractModel _consentContractModel);
    }
}
