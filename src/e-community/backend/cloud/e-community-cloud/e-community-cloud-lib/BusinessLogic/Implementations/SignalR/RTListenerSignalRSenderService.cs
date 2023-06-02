using e_community_cloud_lib.BusinessLogic.Interfaces.SignalR;
using e_community_cloud_lib.Endpoints;
using e_community_cloud_lib.Endpoints.Dtos;
using e_community_cloud_lib.Endpoints.Interfaces;
using e_community_cloud_lib.NonEntities;
using e_community_cloud_lib.Util.Enums;
using e_community_cloud_lib.Util.Extensions;
using Microsoft.AspNetCore.SignalR;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Serilog;
using System.Text.Json;
using e_community_cloud_lib.Models.Blockchain;
using Newtonsoft.Json;

namespace e_community_cloud_lib.BusinessLogic.Implementations.SignalR {
    public class RTListenerSignalRSenderService : IRTListenerSignalRSenderService {
        private IRTListenerSingleton mRTListenerSingleton;
        private ILocalSignalRSenderService mLocalSignalRSenderService;
        private IHubContext<LocalHub, ILocalSignalRSender> mLocalHubContext;
        private IHubContext<EndDeviceHub, IEndDeviceSignalRSender> mEndDeviceHubContext;

        public RTListenerSignalRSenderService(
            IRTListenerSingleton _RTListenerSingleton,
            IHubContext<EndDeviceHub, IEndDeviceSignalRSender> _endDeviceHubContext,
            IHubContext<LocalHub, ILocalSignalRSender> _localHubContext,
            ILocalSignalRSenderService _localSignalRSenderService
        ) {
            mRTListenerSingleton = _RTListenerSingleton;
            mEndDeviceHubContext = _endDeviceHubContext;
            mLocalHubContext = _localHubContext;
            mLocalSignalRSenderService = _localSignalRSenderService;
        }

        public void SendToMembersBuffered(Guid _memberId, RTListenerData _listenerData, RTMeterDataBuffer _buffer) {
            if (_listenerData.PreviousSentTimestamp < _buffer.Timestamp) {
                var bufferedMeterData = new BufferedMeterDataRTDto() {
                    Timestamp = _buffer.Timestamp,
                    MissingSmartMeterCount = _listenerData.SmartMeterCount - _buffer.MeterData.Count
                };

                if (_listenerData.ECommunityId == null) {
                    // member is not part in an eCommunity
                    bufferedMeterData.MeterDataMember = _buffer.MeterData;

                    _listenerData.PreviousSentTimestamp = _buffer.Timestamp;
                    bufferedMeterData.MissingSmartMeterCountMember = bufferedMeterData.MissingSmartMeterCount;
                    if (bufferedMeterData.MissingSmartMeterCountMember > 0) {
                        // missing smart meters
                        mLocalSignalRSenderService.RequestRTDataFaulty(_listenerData.SignalRGroupName);
                    }

                    SendToMember(_memberId, bufferedMeterData);
                }
                else {
                    // member is part in an eCommunity
                    var memberMeterDataMap = new Dictionary<Guid, List<MeterDataRTDto>>(); // listening members (MemberId -> Meter Data)

                    mRTListenerSingleton.GetListeners((Guid)_listenerData.ECommunityId).ForEach(listener => {
                        // new map entry for every listening member
                        listener.Value.PreviousSentTimestamp = _buffer.Timestamp;
                        memberMeterDataMap.Add(listener.Key, new List<MeterDataRTDto>());
                    });

                    _buffer.MeterData.ForEach(meterData => {
                        // add up meter data
                        bufferedMeterData.ECommunityActivePowerPlus += meterData.ActivePowerPlus;
                        bufferedMeterData.ECommunityActivePowerMinus += meterData.ActivePowerMinus;
                        bufferedMeterData.ECommunityReactivePowerPlus += meterData.ReactivePowerPlus;
                        bufferedMeterData.ECommunityReactivePowerMinus += meterData.ReactivePowerMinus;

                        if (memberMeterDataMap.GetValueOrDefault(meterData.MemberId) != null) {
                            // member is listening
                            memberMeterDataMap[meterData.MemberId].Add(meterData); 
                        }
                    });

                    foreach (var memberMeterData in memberMeterDataMap) {
                        // send to every member
                        bufferedMeterData.MeterDataMember = memberMeterData.Value;

                        bufferedMeterData.MissingSmartMeterCountMember =
                            _listenerData.SmartMeterCountMember - memberMeterData.Value.Count;
                        if (bufferedMeterData.MissingSmartMeterCount > 0) {
                            // missing smart meters
                            mLocalSignalRSenderService.RequestRTDataFaulty(_listenerData.SignalRGroupName);
                        }

                        SendToMember(memberMeterData.Key, bufferedMeterData);
                    }
                }
            }
        }

        public void SendToMember(Guid _memberId, BufferedMeterDataRTDto _meterData) {
            mEndDeviceHubContext.Clients.Groups(_memberId.GetGroupName(GroupType.Member))
               .ReceiveRTData(_meterData);
        }

        public void SendToMemberBlockchainAccountBalance(Guid _memberId, BlockchainAccountBalanceDto _blockchainAccountBalance)
        {
            Log.Information($"Blockchain/SendToMemberBlockchainAccountBalance::{_blockchainAccountBalance}");
            
            mEndDeviceHubContext.Clients.Groups(_memberId.GetGroupName(GroupType.Member))
                .ReceiveBlockchainAccountBalance(_blockchainAccountBalance);
            mLocalHubContext.Clients.Groups(_memberId.GetGroupName(GroupType.Member))
                .ReceiveBlockchainAccountBalance(_blockchainAccountBalance);
        }

        public void SendToMemberCreateConsentContract(Guid _memberId, ConsentContractModel _consentContractModel)
        {
            mEndDeviceHubContext.Clients.Groups(_memberId.GetGroupName(GroupType.Member))
                .ReceiveCreateConsentContract(_consentContractModel);
            mLocalHubContext.Clients.Groups(_memberId.GetGroupName(GroupType.Member))
                .ReceiveCreateConsentContract(_consentContractModel);
        }

        public void SendToMemberContractsForMember(Guid _memberId, IList<ConsentContractModel> _contractsForMember)
        {
            Log.Information($"Blockchain/SendToMemberContractsForMember::{_contractsForMember.Count}");
            
            var contractsObj = new ContractsForMember
            {
                Contracts = JsonConvert.SerializeObject(_contractsForMember)
            };

            mEndDeviceHubContext.Clients.Groups(_memberId.GetGroupName(GroupType.Member))
                .ReceiveContractsForMember(contractsObj);
            mLocalHubContext.Clients.Groups(_memberId.GetGroupName(GroupType.Member))
                .ReceiveContractsForMember(contractsObj);
        }

        public void SendToMemberDepositToContract(Guid _memberId, string _contractBalance)
        {
            Log.Information($"Blockchain/SendToMemberDepositToContract::{_contractBalance}");
            
            mEndDeviceHubContext.Clients.Groups(_memberId.GetGroupName(GroupType.Member))
                .ReceiveDepositToContract(_contractBalance);
            mLocalHubContext.Clients.Groups(_memberId.GetGroupName(GroupType.Member))
                .ReceiveDepositToContract(_contractBalance);
        }

        public void SendToMemberWithdrawFromContract(Guid _memberId, string _accountBalance)
        {
            Log.Information($"Blockchain/SendToMemberWithdrawFromContract::{_accountBalance}");
            
            mEndDeviceHubContext.Clients.Groups(_memberId.GetGroupName(GroupType.Member))
                .ReceiveWithdrawFromContract(_accountBalance);
            mLocalHubContext.Clients.Groups(_memberId.GetGroupName(GroupType.Member))
                .ReceiveWithdrawFromContract(_accountBalance);
        }

        public void SendToMemberHistoryData(Guid _memberId, MeterDataHistDto _meterDataHistDto)
        {
            Log.Information($"Blockchain/SendToMemberHistoryData::{_meterDataHistDto}");
            
            mEndDeviceHubContext.Clients.Groups(_memberId.GetGroupName(GroupType.Member))
                .ReceiveHistoryData(_meterDataHistDto);
            mLocalHubContext.Clients.Groups(_memberId.GetGroupName(GroupType.Member))
                .ReceiveHistoryData(_meterDataHistDto);
        }
    }
}
