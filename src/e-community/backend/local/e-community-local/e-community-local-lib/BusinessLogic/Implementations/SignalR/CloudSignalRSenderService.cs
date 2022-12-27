using e_community_local_lib.BusinessLogic.Interfaces.REST;
using e_community_local_lib.BusinessLogic.Interfaces.SignalR;
using e_community_local_lib.CloudDtos;
using e_community_local_lib.Database;
using e_community_local_lib.Endpoints.Interfaces;
using e_community_local_lib.Util.Extensions;
using Microsoft.AspNetCore.SignalR.Client;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Serilog;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

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
            if (meterData != null) {
                meterData.Timestamp = meterData.Timestamp.ToUniversalTime(); // convert timestamp to UTC
                Log.Information($"CloudSignalRSenderService::SendRTData() from {meterData.Timestamp}");
                await mHubConnectionService.InvokeSignalR(
                    nameof(ICloudSignalRSender.ReceiveRTData),
                    meterData.CopyPropertiesTo(new MeterDataRTDto())
                );
            }
        }
        public async Task TimerElapsed() {
            await mHubConnectionService.InvokeSignalR(nameof(ICloudSignalRSender.TimerElapsed));
        }

        public async Task SendBlockchainAccountBalance()
        {
            // TODO call python script and get account balances
            Log.Information($"CloudSignalRSenderService::SendBlockchainAccountBalance()");

            var blockchainAccountBalance = new BlockchainAccountBalanceDto
            {
                Received = "5 ETH",
                Sent = "1 ETH",
                Balance = "55 ETH"
            };

            await mHubConnectionService.InvokeSignalR(
                nameof(ICloudSignalRSender.ReceiveBlockchainAccountDetails),
                blockchainAccountBalance.CopyPropertiesTo(new BlockchainAccountBalanceDto())
            );
        }
    }
}
