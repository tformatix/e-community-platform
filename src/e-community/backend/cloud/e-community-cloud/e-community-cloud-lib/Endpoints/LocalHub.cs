using e_community_cloud_lib.BusinessLogic.Interfaces.SignalR;
using e_community_cloud_lib.Database;
using e_community_cloud_lib.Endpoints.Dtos;
using e_community_cloud_lib.Endpoints.Interfaces;
using e_community_cloud_lib.Util.Enums;
using e_community_cloud_lib.Util.Extensions;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.SignalR;
using Microsoft.EntityFrameworkCore;
using Serilog;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using e_community_cloud_lib.Models.Blockchain;

namespace e_community_cloud_lib.Endpoints {
    /// <summary>
    /// handles SignalR communication with Smart Meter
    /// Smart Meter invokes methods of Hub ("listener")
    /// <seealso cref="Hub"/>
    /// <seealso cref="ILocalSignalRReceiver"/>
    /// </summary>
    [Authorize]
    public class LocalHub : Hub<ILocalSignalRSender>, ILocalSignalRReceiver {
        private readonly ILocalSignalRReceiverService mLocalSignalRReceiverService;
        private readonly ILocalSignalRSenderService mLocalSignalRSenderService;
        private readonly IRTListenerSingleton mRTListenerSingleton;
        private readonly ECommunityCloudContext mDb;

        public LocalHub(
            ILocalSignalRReceiverService _localSignalRReceiverService,
            ILocalSignalRSenderService _localSignalRSenderService,
            IRTListenerSingleton _RTListenerSingleton,
            ECommunityCloudContext _db
        ) {
            mLocalSignalRReceiverService = _localSignalRReceiverService;
            mLocalSignalRSenderService = _localSignalRSenderService;
            mRTListenerSingleton = _RTListenerSingleton;
            mDb = _db;
        }

        public override async Task OnConnectedAsync() {
            var memberId = Context.User.GetMemberId();
            if (memberId != null) {
                var groupName = memberId?.GetGroupName(GroupType.Member);
                await Groups.AddToGroupAsync(Context.ConnectionId, groupName);
                var eCommunityId = mDb.GetECommunityId(memberId);
                if (eCommunityId != null) {
                    await Groups.AddToGroupAsync(Context.ConnectionId, eCommunityId?.GetGroupName(GroupType.ECommunity));
                }
                await base.OnConnectedAsync();
                Log.Information($"Endpoint/Local::Connected #{memberId}");

                if (mRTListenerSingleton.GetRTListenerData((Guid)memberId) != null) {
                    // should already send real time data
                    mLocalSignalRSenderService.RequestRTDataFaulty(groupName);
                }
            }
        }

        public override Task OnDisconnectedAsync(Exception exception) {
            var memberId = Context.User.GetMemberId();
            Log.Information($"Endpoint/Local::Disconnected #{memberId}");
            return base.OnDisconnectedAsync(exception);
        }

        public async Task AssignSmartMeterId(Guid _smartMeterId) {
            Log.Information($"Endpoint/Local::AssignSmartMeterId #{_smartMeterId}");
            await Groups.AddToGroupAsync(Context.ConnectionId, _smartMeterId.GetGroupName(GroupType.SmartMeter));
        }

        public Task ReceiveRTData(LocalMeterDataRTDto _meterData) {
            var memberId = Context.User.GetMemberId();
            if (memberId != null) {
                mLocalSignalRReceiverService.ReceivedRTData(_meterData, (Guid)memberId);
            }
            return Task.CompletedTask;
        }

        public Task TimerElapsed() {
            var memberId = Context.User.GetMemberId();
            Log.Information($"Endpoint/Local::TimerElapsed #{memberId}");
            if (memberId != null) {
                mLocalSignalRReceiverService.TimerElapsed((Guid) memberId);
            }
            return Task.CompletedTask;
        }

        public Task ReceiveBlockchainAccountBalance(BlockchainAccountBalanceDto _blockchainAccountBalance)
        {
            Log.Information($"Endpoint/Local::ReceivedBlockchainAccountBalance #{_blockchainAccountBalance}");
            var memberId = Context.User.GetMemberId();

            if (memberId != null)
            {
                mLocalSignalRReceiverService.ReceivedBlockchainAccountBalance((Guid) memberId, _blockchainAccountBalance);
            }
            
            return Task.CompletedTask;
        }

        public Task ReceiveCreateConsentContract(ConsentContractModel _consentContractModel)
        {
            Log.Information($"Endpoint/Local::ReceiveCreateConsentContract ");
            var memberId = Context.User.GetMemberId();

            if (memberId != null)
            {
                mLocalSignalRReceiverService.ReceiveCreateConsentContract((Guid) memberId, _consentContractModel);
            }
            
            return Task.CompletedTask;
        }

        public Task ReceiveContractsForMember(IList<ConsentContractModel> _contractsForMember)
        {
            Log.Information($"Endpoint/Local::ReceiveContractsForMember ");
            var memberId = Context.User.GetMemberId();

            if (memberId != null)
            {
                mLocalSignalRReceiverService.ReceiveContractsForMember((Guid) memberId, _contractsForMember);
            }
            
            return Task.CompletedTask;
        }

        public Task ReceiveDepositToContract(string _contractBalance)
        {
            Log.Information($"Endpoint/Local::ReceiveDepositToContract ");
            var memberId = Context.User.GetMemberId();

            if (memberId != null)
            {
                mLocalSignalRReceiverService.ReceiveDepositToContract((Guid) memberId, _contractBalance);
            }
            
            return Task.CompletedTask;
        }

        public Task ReceiveWithdrawFromContract(string _accountBalance)
        {
            Log.Information($"Endpoint/Local::ReceiveWithdrawFromContract ");
            var memberId = Context.User.GetMemberId();

            if (memberId != null)
            {
                mLocalSignalRReceiverService.ReceiveWithdrawFromContract((Guid) memberId, _accountBalance);
            }
            
            return Task.CompletedTask;
        }

        public Task ReceiveHistoryData(MeterDataHistDto _meterDataHistDto)
        {
            Log.Information($"Endpoint/Local::ReceiveHistoryData");
            var memberId = Context.User.GetMemberId();

            if (memberId != null)
            {
                mLocalSignalRReceiverService.ReceiveHistoryData((Guid) memberId, _meterDataHistDto);
            }
            
            return Task.CompletedTask;
        }
    }
}
