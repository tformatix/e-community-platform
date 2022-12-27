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
                    _buffer.MeterData.ForEach(meterData => {
                        bufferedMeterData.ActivePowerPlus += meterData.ActivePowerPlus;
                        bufferedMeterData.ActivePowerMinus += meterData.ActivePowerMinus;
                        bufferedMeterData.ReactivePowerPlus += meterData.ReactivePowerPlus;
                        bufferedMeterData.ReactivePowerMinus += meterData.ReactivePowerMinus;
                    });

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
                    var memberMeterDataMap = new Dictionary<Guid, RTMeterDataMemberBuffer>(); // listening members (MemberId -> Meter Data)

                    mRTListenerSingleton.GetListeners((Guid)_listenerData.ECommunityId).ForEach(listener => {
                        // new map entry for every listening member
                        listener.Value.PreviousSentTimestamp = _buffer.Timestamp;
                        memberMeterDataMap.Add(listener.Key, new RTMeterDataMemberBuffer());
                    });

                    _buffer.MeterData.ForEach(meterData => {
                        // add up meter data
                        bufferedMeterData.ECommunityActivePowerPlus += meterData.ActivePowerPlus;
                        bufferedMeterData.ECommunityActivePowerMinus += meterData.ActivePowerMinus;
                        bufferedMeterData.ECommunityReactivePowerPlus += meterData.ReactivePowerPlus;
                        bufferedMeterData.ECommunityReactivePowerMinus += meterData.ReactivePowerMinus;

                        var memberMeterData = memberMeterDataMap.GetValueOrDefault(meterData.MemberId);
                        if (memberMeterData != null) {
                            // member is listening
                            memberMeterData.ActivePowerPlus += meterData.ActivePowerPlus;
                            memberMeterData.ActivePowerMinus += meterData.ActivePowerMinus;
                            memberMeterData.ReactivePowerPlus += meterData.ReactivePowerPlus;
                            memberMeterData.ReactivePowerMinus += meterData.ReactivePowerMinus;
                            memberMeterData.SmartMeterCount++;
                        }
                    });

                    foreach (var memberMeterData in memberMeterDataMap) {
                        // send to every member
                        bufferedMeterData.ActivePowerPlus = memberMeterData.Value.ActivePowerPlus;
                        bufferedMeterData.ActivePowerMinus = memberMeterData.Value.ActivePowerMinus;
                        bufferedMeterData.ReactivePowerPlus = memberMeterData.Value.ReactivePowerPlus;
                        bufferedMeterData.ReactivePowerMinus = memberMeterData.Value.ReactivePowerMinus;

                        bufferedMeterData.MissingSmartMeterCountMember =
                            _listenerData.SmartMeterCountMember - memberMeterData.Value.SmartMeterCount;
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
            mLocalHubContext.Clients.Groups(_memberId.GetGroupName(GroupType.Member))
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
    }
}
