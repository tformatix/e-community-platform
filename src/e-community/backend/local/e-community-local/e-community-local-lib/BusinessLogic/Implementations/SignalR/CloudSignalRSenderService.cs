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
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using e_community_local_lib.Util;

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

        public async Task SendBlockchainAccountBalance() {
            Process processAccountBalance = new Process();

            processAccountBalance.StartInfo = new ProcessStartInfo(Constants.PYTHON_EXE, Constants.GET_ACCOUNT_BALANCE) {
                RedirectStandardOutput = true
            };
            processAccountBalance.Start();

            var output = processAccountBalance.StandardOutput.ReadToEnd();

            processAccountBalance.WaitForExit();
            processAccountBalance.Close();

            var blockchainAccountBalance = new BlockchainAccountBalanceDto {
                Received = "0.05",
                Sent = "1.4",
                Balance = output
            };

            Log.Information($"CloudSignalRSenderService::SendBlockchainAccountBalance() ${nameof(ICloudSignalRSender.ReceiveBlockchainAccountBalance)}");

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
                $"-pt {_consentContractModel.TotalPrice}";
            
            // create consent contract on blockchain
            Process processCreateContract = new Process();
            
            Log.Information($"CloudSignalRSenderService::SendCreateConsentContract() command: {Constants.CREATE_CONSENT_CONTRACT + args}");

            processCreateContract.StartInfo = new ProcessStartInfo(Constants.PYTHON_EXE, Constants.CREATE_CONSENT_CONTRACT + args)
            {
                RedirectStandardOutput = true
            };
            processCreateContract.Start();

            var contractAddress = processCreateContract.StandardOutput.ReadToEnd();

            processCreateContract.WaitForExit();
            processCreateContract.Close();
            
            Log.Information($"CloudSignalRSenderService::SendCreateConsentContract() deployed contract address {contractAddress}");

            _consentContractModel.AddressContract = contractAddress;

            await mHubConnectionService.InvokeSignalR(
                nameof(ICloudSignalRSender.ReceiveCreateConsentContract),
                _consentContractModel
            );
        }
    }
}
