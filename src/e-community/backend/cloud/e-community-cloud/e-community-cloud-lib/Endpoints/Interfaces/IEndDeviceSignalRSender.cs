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
        /// receives the created consent contract
        /// </summary>
        /// <param name="_consentContractModel"></param>
        /// <returns></returns>
        Task ReceiveCreateConsentContract(ConsentContractModel _consentContractModel);

        /// <summary>
        /// receives the contracts for the member in a list
        /// </summary>
        /// <param name="_contractsForMember"></param>
        /// <returns></returns>
        Task ReceiveContractsForMember(ContractsForMember _contractsForMember);
        
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
        /// cloud receive the history energy data
        /// </summary>
        /// <param name="_meterDataHistDto"></param>
        /// <returns></returns>
        Task ReceiveHistoryData(MeterDataHistDto _meterDataHistDto);
    }
}
