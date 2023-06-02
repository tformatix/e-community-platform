using e_community_local_lib.BusinessLogic.Interfaces.REST;
using e_community_local_lib.BusinessLogic.Interfaces.SignalR;
using e_community_local_lib.CloudData;
using e_community_local_lib.Database;
using e_community_local_lib.Endpoints.Interfaces;
using e_community_local_lib.Util.Extensions;
using Microsoft.AspNetCore.SignalR.Client;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Serilog;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;
using e_community_local_lib.Database.Meter;
using e_community_local_lib.Models;
using e_community_local_lib.Util;
using Newtonsoft.Json;

namespace e_community_local_lib.BusinessLogic.Implementations.SignalR {
    public class CloudSignalRSenderService : ICloudSignalRSenderService {
        private readonly ECommunityLocalContext mDb;
        private readonly IHubConnectionService mHubConnectionService;

        public CloudSignalRSenderService(ECommunityLocalContext _db, IHubConnectionService _hubConnectionService) {
            mDb = _db;
            mHubConnectionService = _hubConnectionService;
        }

        public async Task AssignSmartMeterId() {
            var smartMeterId = (await mDb.SmartMeter.FirstOrDefaultAsync())?.Id;
            await mHubConnectionService.InvokeSignalR(
                nameof(ICloudSignalRSender.AssignSmartMeterId),
                smartMeterId
            );
        }

        public async Task SendRTData() {
            var meterData = await mDb.MeterDataRealTime
                    .OrderBy(x => x.Id)
                    .LastOrDefaultAsync();
            var smartMeterId = (await mDb.SmartMeter.FirstOrDefaultAsync())?.Id;
            if (meterData != null && smartMeterId != null) {
                meterData.Timestamp = meterData.Timestamp.ToUniversalTime(); // convert timestamp to UTC
                Log.Information($"CloudSignalRSenderService::SendRTData() from {meterData.Timestamp}");
                await mHubConnectionService.InvokeSignalR(
                    nameof(ICloudSignalRSender.ReceiveRTData),
                    meterData.CopyPropertiesTo(new MeterDataRTDto() {
                        SmartMeterId = (Guid)smartMeterId,
                    })
                );
            }
        }
        public async Task TimerElapsed() {
            await mHubConnectionService.InvokeSignalR(nameof(ICloudSignalRSender.TimerElapsed));
        }

        public async Task SendBlockchainAccountBalance() 
        {
            var blockchainBalance = ExecutePythonCommand(Constants.GET_ACCOUNT_BALANCE, "");

            var blockchainAccountBalance = new BlockchainAccountBalanceDto 
            {
                Received = "0.05",
                Sent = "1.4",
                Balance = blockchainBalance
            };

            Log.Information($"CloudSignalRSenderService::SendBlockchainAccountBalance() balance: {blockchainBalance}");

            await mHubConnectionService.InvokeSignalR(
                nameof(ICloudSignalRSender.ReceiveBlockchainAccountBalance),
                blockchainAccountBalance.CopyPropertiesTo(new BlockchainAccountBalanceDto())
            );
        }

        public async Task SendCreateConsentContract(ConsentContractModel _consentContractModel)
        {
            var args =
                $"-ac {_consentContractModel.AddressConsenter} " +
                $"-cid {_consentContractModel.ContractId} " +
                $"-sed {_consentContractModel.StartEnergyData} " +
                $"-eed {_consentContractModel.EndEnergyData} " +
                $"-vc {_consentContractModel.ValidityOfContract} " +
                $"-ph {_consentContractModel.PricePerHour} " +
                $"-pt {_consentContractModel.TotalPrice}" +
                $"-du {_consentContractModel.DataUsage}" +
                $"-tr {_consentContractModel.TimeResolution}";

            var contractAddress = ExecutePythonCommand(Constants.CREATE_CONSENT_CONTRACT, args);
            
            Log.Information($"CloudSignalRSenderService::SendCreateConsentContract() deployed contract address {contractAddress}");

            if (contractAddress.StartsWith("0x"))
            {
                _consentContractModel.AddressContract = contractAddress;
                
                await mHubConnectionService.InvokeSignalR(
                    nameof(ICloudSignalRSender.ReceiveCreateConsentContract),
                    _consentContractModel
                );
            }
        }

        public async Task SendContractsForMember()
        {
            try
            {
                var contracts = ExecutePythonCommand(Constants.CONTRACTS_FOR_MEMBER, "");
                //contracts = contracts.Replace("\"", "\\\"");
            
                Log.Information($"CloudSignalRSenderService::SendContractsForMember() ${contracts}");
            
                //var contractsForMember = JsonSerializer.Deserialize<List<ConsentContractModel>>(contracts);
            
                var contractsForMember = JsonConvert.DeserializeObject<List<ConsentContractModel>>(contracts);

                if (contractsForMember != null)
                {
                    Log.Information($"CloudSignalRSenderService::SendContractsForMember() ${contractsForMember.Count}");

                    await mHubConnectionService.InvokeSignalR(
                        nameof(ICloudSignalRSender.ReceiveContractsForMember),
                        contractsForMember
                    );
                }
                else
                {
                    Log.Error($"CloudSignalRSenderService::SendContractsForMember() failed to receive contracts");
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e);
                throw;
            }
        }

        public async Task SendDepositToContract(ConsentContractModel _consentContractModel)
        {
            // deposit to the contract
            var args =
                $"-cid {_consentContractModel.ContractId.Trim()} " +
                $"-v {_consentContractModel.TotalPrice} ";

            var balanceContract = ExecutePythonCommand(Constants.DEPOSIT_TO_CONTRACT, args);

            var isContractSatisfied = double.TryParse(balanceContract, out var balanceCheck);
                
            Log.Information($"CloudSignalRSenderService::SendDepositToContract() contract balance: {balanceCheck}");

            if (isContractSatisfied && balanceCheck > 0.0d)
            {
                Log.Information($"CloudSignalRSenderService::SendDepositToContract() contract balance: {balanceCheck}");
                    
                await mHubConnectionService.InvokeSignalR(
                    nameof(ICloudSignalRSender.ReceiveDepositToContract),
                    balanceCheck.ToString(CultureInfo.InvariantCulture)
                );
            }
        }

        public async Task SendWithdrawFromContract(ConsentContractModel _consentContractModel)
        {
            var args =
                $"-cid {_consentContractModel.ContractId.Trim()}";

            var balanceConsenter = ExecutePythonCommand(Constants.WITHDRAW_FROM_CONTRACT, args);
            
            Log.Information($"CloudSignalRSenderService::SendWithdrawFromContract() consenter balance: {balanceConsenter}");
                    
            await mHubConnectionService.InvokeSignalR(
                nameof(ICloudSignalRSender.ReceiveWithdrawFromContract),
                balanceConsenter.ToString(CultureInfo.InvariantCulture)
            );
        }

        public async Task SendHistoryData(RequestHistoryModel _requestHistoryModel)
        {
            int timeStep = _requestHistoryModel.TimeResolution / Constants.TIME_DIFF_HIST;

            // filter history table with given timestamps
            var dataHistory = mDb.MeterDataHistory
                .ToList()
                .Where(x => x.Timestamp.CompareTo(_requestHistoryModel.FromTimestamp) > 0 &&
                            x.Timestamp.CompareTo(_requestHistoryModel.ToTimestamp) < 0)
                .Where((x, i) => (i % timeStep == 0))
                .ToList();

            if (!dataHistory.Any())
            {
                Log.Error(
                    $"CloudSignalRSenderService::SendHistoryData() no data found between {_requestHistoryModel.FromTimestamp} and {_requestHistoryModel.ToTimestamp}");
            }
            else
            {
                var historyData = CalcEnergyDiffHist(dataHistory);
                var meterDataHistDto = new MeterDataHistDto()
                {
                    MeterDataValues = historyData
                        .Select(x => x.ParseMeterData())
                        .ToList(),
                    Max = new MeterDataBase(),
                    Min = new MeterDataBase(),
                    Avg = new MeterDataBase(),
                    Unit = new MeterDataBase()
                };
                
                Log.Information($"CloudSignalRSenderService::SendHistoryData() {meterDataHistDto.MeterDataValues}");

                await mHubConnectionService.InvokeSignalR(
                    nameof(ICloudSignalRSender.ReceiveHistoryData),
                    meterDataHistDto
                );
            }
        }

        public Task SendUpdateContractState(UpdateContractState _updateContractState)
        {
            var args =
                $"-cid {_updateContractState.ContractId.Trim()}";

            switch (_updateContractState.UpdateState)
            {
                case (int)UpdateContractStateOpt.ContractAccept:
                    ExecutePythonCommand(Constants.ACCEPT_CONTRACT, args);
                    break;
                case (int)UpdateContractStateOpt.ContractReject:
                    ExecutePythonCommand(Constants.REJECT_CONTRACT, args);
                    break;
                case (int)UpdateContractStateOpt.ContractRevoke:
                    ExecutePythonCommand(Constants.REVOKE_CONTRACT, args);
                    break;
            }
            
            return Task.CompletedTask;
        }

        /// <summary>
        /// energy (Zählerstand) should be substracted from the prev energy (Zählerstand) 
        /// </summary>
        /// <param name="dataHistory">filtered history list</param>
        List<MeterDataHistory> CalcEnergyDiffHist(List<MeterDataHistory> dataHistory)
        {
            // clone list
            var clonedHistory = dataHistory
                .Select(x => x.CopyPropertiesTo(new MeterDataHistory()))
                .ToList();
            
            // calc differences
            MeterDataHistory cloned, data;
            
            for (int i = 1; i < dataHistory.Count(); i++)
            {
                cloned = clonedHistory[i];
                data = dataHistory[i - 1];
                cloned.Id = i; // ID ascending
                cloned.ActiveEnergyPlus -= data.ActiveEnergyPlus;
                cloned.ActiveEnergyMinus -= data.ActiveEnergyMinus;
                cloned.ReactiveEnergyPlus -= data.ReactiveEnergyPlus;
                cloned.ReactiveEnergyMinus -= data.ReactiveEnergyMinus;
            }
            clonedHistory.RemoveAt(0); // delete first entry --> needed for diferences
            return clonedHistory;
        }

        public string ExecutePythonCommand(string _command, string _args)
        {
            Process process = new Process();

            var cmd = _args.Length == 0 ? _command : _command + _args;
            
            Log.Information($"CloudSignalRSenderService::ExecutePythonCommand() command {cmd}");

            process.StartInfo = new ProcessStartInfo(Constants.PYTHON_EXE, cmd) {
                RedirectStandardOutput = true
            };
            process.Start();

            var output = process.StandardOutput.ReadToEnd();

            process.WaitForExit();
            process.Close();

            return output.Trim();
        }
    }
}
