using e_community_cloud_lib.BusinessLogic.Interfaces.SignalR;
using e_community_cloud_lib.Endpoints.Dtos;
using e_community_cloud_lib.NonEntities;
using e_community_cloud_lib.Util;
using e_community_cloud_lib.Util.Extensions;
using Serilog;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Timers;
using e_community_cloud_lib.Models.Blockchain;

namespace e_community_cloud_lib.BusinessLogic.Implementations.SignalR {
    /// <summary>
    /// <seealso cref="ILocalSignalRReceiverService"/>
    /// </summary>
    public class LocalSignalRReceiverService : ILocalSignalRReceiverService {
        private readonly ILocalSignalRSenderService mLocalSignalRSenderService;
        private readonly IRTListenerSignalRSenderService mRTListenerSignalRSenderService;
        private readonly IRTListenerSingleton mRTListenerSingleton;

        public LocalSignalRReceiverService(
            ILocalSignalRSenderService _localSignalRSenderService,
            IRTListenerSignalRSenderService _RTListenerSignalRSenderService,
            IRTListenerSingleton _RTListenerSingleton
        ) {
            mLocalSignalRSenderService = _localSignalRSenderService;
            mRTListenerSignalRSenderService = _RTListenerSignalRSenderService;
            mRTListenerSingleton = _RTListenerSingleton;
        }

        public void ReceivedRTData(LocalMeterDataRTDto _meterData, Guid _memberId) {
            if ((_meterData.Timestamp - DateTime.UtcNow).TotalMilliseconds < Constants.AUTO_SEND_RT_DATA_MILLISECONDS) {
                // only handle data which has not passed more than AUTO_SEND_RT_DATA_SECONDS seconds
                var listenerData = mRTListenerSingleton.GetRTListenerData(_memberId);

                if (listenerData != null) {
                    if (listenerData.SmartMeterCount > 1) {
                        // more than one smart meter (wait for others - BUFFERING)

                        var meterData = _meterData.CopyPropertiesTo(new MeterDataRTDto());
                        meterData.MemberId = _memberId;
                        var id = listenerData.ECommunityId ?? _memberId;

                        var bufferEntries = mRTListenerSingleton.RTBuffer.GetValueOrDefault(id);
                        RTMeterDataBuffer buffer = null;

                        if (bufferEntries == null) {
                            mRTListenerSingleton.RTBuffer.Add(id, bufferEntries = new List<RTMeterDataBuffer>());
                        }
                        else {
                            buffer = bufferEntries
                                .FirstOrDefault(x => x?.Timestamp == _meterData.Timestamp);
                        }

                        if (buffer == null) {
                            // no entry for given timestamp
                            buffer = new RTMeterDataBuffer() {
                                Timestamp = _meterData.Timestamp,
                                MeterData = new List<MeterDataRTDto> { meterData }
                            };

                            // add new timer for automatic sending after AUTO_SEND_RT_DATA_SECONDS (even if data is missing)
                            buffer.Timer = new Timer(Constants.AUTO_SEND_RT_DATA_MILLISECONDS);
                            buffer.Timer.Elapsed += (_, _) => {
                                // timer elapsed --> send faulty data (some data is missing)
                                mRTListenerSignalRSenderService.SendToMembersBuffered(_memberId, listenerData, buffer);
                                bufferEntries.Remove(buffer);
                            };
                            buffer.Timer.AutoReset = false;
                            buffer.Timer.Enabled = true;

                            bufferEntries.Add(buffer);
                        }
                        else {
                            // entries for given timestamp are already available
                            buffer.MeterData.Add(meterData);
                            if (buffer.MeterData.Count() == listenerData.SmartMeterCount) {
                                // buffer is full (send to end device(s))
                                buffer.Timer.Enabled = false;
                                mRTListenerSignalRSenderService.SendToMembersBuffered(_memberId, listenerData, buffer);
                                bufferEntries.Remove(buffer);
                            }
                        }
                    }
                    else {
                        // only one smart meter (buffering not necessary - send directly to end device)
                        var endDeviceMeterData = _meterData.CopyPropertiesTo(new BufferedMeterDataRTDto());
                        mRTListenerSignalRSenderService.SendToMember(_memberId, endDeviceMeterData);
                        listenerData.PreviousSentTimestamp = endDeviceMeterData.Timestamp;
                    }
                }
            }
        }

        public void TimerElapsed(Guid memberId) {
            var listenerDataMember = mRTListenerSingleton.GetRTListenerData((Guid)memberId);
            if (listenerDataMember != null) {
                if (listenerDataMember.ECommunityId == null) {
                    // member is not part in an eCommunity
                    mLocalSignalRSenderService.StopRTData(memberId);
                }
                else {
                    // member is part in an eCommunity
                    var listenersECommunity = mRTListenerSingleton.GetListeners((Guid)listenerDataMember.ECommunityId);
                    foreach (var listener in listenersECommunity) {
                        mLocalSignalRSenderService.StopRTData(listener.Key);
                    }
                }
            }
        }

        public void ReceivedBlockchainAccountBalance(Guid _memberId, BlockchainAccountBalanceDto _blockchainAccountBalance)
        {
            var blockchainAccountBalance = _blockchainAccountBalance.CopyPropertiesTo(new BlockchainAccountBalanceDto());
            mRTListenerSignalRSenderService.SendToMemberBlockchainAccountBalance(_memberId, blockchainAccountBalance);
        }

        public void ReceiveCreateConsentContract(Guid _memberId, ConsentContractModel _consentContractModel)
        {
            mRTListenerSignalRSenderService.SendToMemberCreateConsentContract(_memberId, _consentContractModel);
        }

        public void ReceiveContractsForMember(Guid _memberId, IList<ConsentContractModel> _contractsForMember)
        {
            mRTListenerSignalRSenderService.SendToMemberContractsForMember(_memberId, _contractsForMember);
        }

        public void ReceiveDepositToContract(Guid _memberId, string _contractBalance)
        {
            mRTListenerSignalRSenderService.SendToMemberDepositToContract(_memberId, _contractBalance);
        }

        public void ReceiveWithdrawFromContract(Guid _memberId, string _accountBalance)
        {
            mRTListenerSignalRSenderService.SendToMemberWithdrawFromContract(_memberId, _accountBalance);
        }

        public void ReceiveHistoryData(Guid _memberId, MeterDataHistDto _meterDataHistDto)
        {
            mRTListenerSignalRSenderService.SendToMemberHistoryData(_memberId, _meterDataHistDto);
        }
    }
}
