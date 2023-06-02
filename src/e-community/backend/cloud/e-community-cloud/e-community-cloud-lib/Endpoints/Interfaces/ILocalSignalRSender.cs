using e_community_cloud_lib.Endpoints.Dtos;
using e_community_cloud_lib.LocalDtos;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;
using e_community_cloud_lib.Models.Blockchain;
using e_community_cloud_lib.Models.History;

namespace e_community_cloud_lib.Endpoints.Interfaces
{
    /// <summary>
    /// provided SignalR methods of Local
    /// </summary>
    public interface ILocalSignalRSender
    {
        /// <summary>
        /// cloud requests real time data from local
        /// </summary>
        Task RequestRTData();

        /// <summary>
        /// cloud extends real time data connection from local (otherwise local backend stops real time traffic)
        /// </summary>
        Task ExtendRTData();

        /// <summary>
        /// cloud stops real time data stream from local
        /// </summary>
        Task StopRTData();

        /// <summary>
        /// smart meter specific data should be updated locally
        /// </summary>
        /// <param name="_localSmartMeterDto">smart meter specific data</param>
        /// <returns></returns>
        Task UpdateSmartMeter(LocalSmartMeterDto _localSmartMeterDto);

        /// <summary>
        /// cloud requests hourly forecast data (load profile and flexibility)
        /// </summary>
        Task RequestHourlyForecast();

        /// <summary>
        /// cloud requests meter data for monitoring
        /// </summary>
        Task RequestMeterDataMonitoring();

        /// <summary>
        /// cloud requests information about the local ethereum account (balances)
        /// </summary>
        Task RequestBlockchainAccountBalance();

        /// <summary>
        /// cloud receives information about the local ethereum account (balances)
        /// </summary>
        /// <returns></returns>
        Task ReceiveBlockchainAccountBalance(BlockchainAccountBalanceDto _blockchainAccountBalance);
        
        /// <summary>
        /// receive the created consent contract
        /// </summary>
        /// <param name="_consentContractModel"></param>
        /// <returns></returns>
        Task ReceiveCreateConsentContract(ConsentContractModel _consentContractModel);

        /// <summary>
        /// creates a new consent contract on the blockchain
        /// </summary>
        /// <param name="_consentContractModel"></param>
        /// <returns></returns>
        Task CreateConsentContract(ConsentContractModel _consentContractModel);

        /// <summary>
        /// cloud requests the contracts were the member is involved
        /// either proposer or consenter
        /// </summary>
        /// <param name="_memberId"></param>
        Task RequestContractsForMember();

        /// <summary>
        /// cloud recieves the contracts were the member is involved
        /// either proposer or consenter
        /// </summary>
        /// <param name="_contractsForMember"></param>
        /// <param name="_memberId"></param>
        Task ReceiveContractsForMember(ContractsForMember _contractsForMember);

        /// <summary>
        /// cloud requests to deposit totalPrice to the smart contract wallet
        /// </summary>
        /// <param name="_consentContract"></param>
        /// <returns></returns>
        Task RequestDepositToContract(ConsentContractModel _consentContract);
        
        /// <summary>
        /// cloud receive the balance of the smart contract wallet
        /// </summary>
        /// <param name="_contractBalance"></param>
        /// <returns></returns>
        Task ReceiveDepositToContract(string _contractBalance);
        
        /// <summary>
        /// cloud requests to withdraw from the smart contract wallet
        /// </summary>
        /// <param name="_consentContract"></param>
        /// <returns></returns>
        Task RequestWithdrawFromContract(ConsentContractModel _consentContract);
        
        /// <summary>
        /// cloud receive the balance of consenter's wallet
        /// </summary>
        /// <param name="_accountBalance"></param>
        /// <returns></returns>
        Task ReceiveWithdrawFromContract(string _accountBalance);

        /// <summary>
        /// cloud requests historic energy data from any member
        /// </summary>
        /// <param name="_requestHistoryModel"></param>
        /// <returns></returns>
        Task RequestHistoryData(RequestHistoryModel _requestHistoryModel);

        /// <summary>
        /// cloud receive the history energy data
        /// </summary>
        /// <param name="_meterDataHistDto"></param>
        /// <returns></returns>
        Task ReceiveHistoryData(MeterDataHistDto _meterDataHistDto);

        /// <summary>
        /// updates the state of the contract (accepted, rejected, revoked)
        /// </summary>
        /// <param name="_updateContractState"></param>
        Task RequestUpdateContractState(UpdateContractState _updateContractState);
    }
}
