using e_community_cloud_lib.BusinessLogic.Interfaces.SignalR;
using e_community_cloud_lib.Database;
using e_community_cloud_lib.Endpoints;
using e_community_cloud_lib.Endpoints.Interfaces;
using e_community_cloud_lib.LocalDtos;
using e_community_cloud_lib.Models.SmartMeter;
using e_community_cloud_lib.NonEntities;
using e_community_cloud_lib.Util;
using e_community_cloud_lib.Util.BusinessLogic;
using e_community_cloud_lib.Util.Enums;
using e_community_cloud_lib.Util.Extensions;
using Microsoft.AspNetCore.SignalR;
using Microsoft.EntityFrameworkCore;
using Serilog;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using e_community_cloud_lib.Models.Blockchain;
using e_community_cloud_lib.Models.History;

namespace e_community_cloud_lib.BusinessLogic.Implementations.SignalR
{
    /// <summary>
    /// <seealso cref="ILocalSignalRSenderService"/>
    /// </summary>
    public class LocalSignalRSenderService : ILocalSignalRSenderService {
        // Errors
        private readonly ECommunityCloudContext mDb;
        private readonly IHubContext<LocalHub, ILocalSignalRSender> mSmartMeterHubContext;
        private readonly IRTListenerSingleton mRTListenerSingleton;

        public LocalSignalRSenderService(
            IHubContext<LocalHub, ILocalSignalRSender> _smartMeterHubContext,
            IRTListenerSingleton _RTListenerSingleton,
            ECommunityCloudContext _db
        ) {
            mSmartMeterHubContext = _smartMeterHubContext;
            mRTListenerSingleton = _RTListenerSingleton;
            mDb = _db;
        }

        public async void RequestRTData(Guid? _memberId) {
            if (_memberId == null) {
                throw new ServiceException(ServiceException.Type.MEMBER_ID_NOT_RESOLVABLE);
            }
            var memberId = (Guid)_memberId;

            var listenerDataMember = mRTListenerSingleton.RTListeners.GetValueOrDefault(memberId);
            if (listenerDataMember == null) {
                // first devive of a member tries to start the real time service
                var eCommunityId = mDb.GetECommunityId(_memberId);
                listenerDataMember = new RTListenerData() {
                    ECommunityId = eCommunityId,
                    ActiveDeviceCount = 0
                };

                if (eCommunityId == null) {
                    // member is not part in an eCommunity
                    listenerDataMember.SignalRGroupName = memberId.GetGroupName(GroupType.Member);
                    listenerDataMember.SmartMeterCount = mDb.Member
                        .Include(x => x.SmartMeters)
                        .Where(x => x.Id == memberId)
                        .Select(x => x.SmartMeters)
                        .Count();
                    listenerDataMember.SmartMeterCountMember = listenerDataMember.SmartMeterCount;

                    await mSmartMeterHubContext.Clients.Groups(listenerDataMember.SignalRGroupName).RequestRTData(); // request real time data
                }
                else {
                    // member is part in an eCommunity
                    var listenerDataECommunity = mRTListenerSingleton.RTListeners
                        .Select(x => x.Value)
                        .FirstOrDefault(x => x.ECommunityId == eCommunityId);
                    if (listenerDataECommunity == null) {
                        // first devive of the eCommunity tries to the start real time service
                        listenerDataMember.SignalRGroupName = eCommunityId?.GetGroupName(GroupType.ECommunity);

                        var members = mDb.ECommunityMembership
                            .Include(x => x.Member)
                            .ThenInclude(x => x.SmartMeters)
                            .Where(x => x.ECommunityId == eCommunityId && Constants.ACTIVE_MEMBER_PERMISSIONS.Contains(x.ECommunityPermission))
                            .Select(x => x.Member)
                            .ToList();

                        var smartMeters = members
                            .SelectMany(x => x.SmartMeters)
                            .ToList();

                        listenerDataMember.SmartMeterCount = smartMeters.Count();
                        listenerDataMember.SmartMeterCountMember = smartMeters
                            .Where(x => x.MemberId == memberId)
                            .ToList()
                            .Count();

                        listenerDataMember.ECommunityMembers = members
                            .Select(x => x.Id)
                            .ToList();

                        await mSmartMeterHubContext.Clients.Groups(listenerDataMember.SignalRGroupName).RequestRTData(); // request real time data
                    }
                    else {
                        // not first device of an eCommunity (copy values from other listener)
                        listenerDataMember.SignalRGroupName = listenerDataECommunity.SignalRGroupName;

                        listenerDataMember.SmartMeterCount = listenerDataECommunity.SmartMeterCount;
                        listenerDataMember.SmartMeterCountMember = mDb.Member
                            .Include(x => x.SmartMeters)
                            .Where(x => x.Id == memberId)
                            .Select(x => x.SmartMeters)
                            .Count();

                        listenerDataMember.ECommunityMembers = listenerDataECommunity.ECommunityMembers;
                    }
                }

                mRTListenerSingleton.RTListeners.Add(memberId, listenerDataMember); // add to listeners dictionary
            }
            listenerDataMember.ActiveDeviceCount++;
        }

        public void RequestRTDataFaulty(string _signalRGroupName) {
            Log.Information($"LocalSignalRSenderService::RequestRTDataFaulty {_signalRGroupName}");
            mSmartMeterHubContext.Clients.Groups(_signalRGroupName).RequestRTData();
        }

        public void ExtendRTData(Guid? _memberId) {
            if (_memberId == null) {
                throw new ServiceException(ServiceException.Type.MEMBER_ID_NOT_RESOLVABLE);
            }
            var memberId = (Guid)_memberId;

            var listenerDataMember = mRTListenerSingleton.GetRTListenerData(memberId);
            if (listenerDataMember != null) {
                mSmartMeterHubContext.Clients.Groups(listenerDataMember.SignalRGroupName).ExtendRTData();
            } else {
                RequestRTData(memberId);
            }
        }

        public void StopRTData(Guid? _memberId) {
            if (_memberId == null) {
                throw new ServiceException(ServiceException.Type.MEMBER_ID_NOT_RESOLVABLE);
            }
            var memberId = (Guid)_memberId;

            var listenerDataMember = mRTListenerSingleton.RTListeners.GetValueOrDefault(memberId);
            if (listenerDataMember != null) {
                listenerDataMember.ActiveDeviceCount--;
                if (listenerDataMember.ActiveDeviceCount == 0) {
                    // no devive of a member listens to the real time service anymore
                    if (listenerDataMember.ECommunityId == null) {
                        // member is not part in an eCommunity (stop real time service)
                        mSmartMeterHubContext.Clients.Groups(listenerDataMember.SignalRGroupName).StopRTData();
                    }
                    else {
                        // member is part in an eCommunity
                        if (mRTListenerSingleton.GetListeners((Guid)listenerDataMember.ECommunityId).Count() == 1) {
                            // only this device from the eCommunity is listening to the real time service (stop it)
                            mSmartMeterHubContext.Clients.Groups(listenerDataMember.SignalRGroupName).StopRTData();
                        }
                    }
                    mRTListenerSingleton.RTListeners.Remove(memberId); // remove from listeners dictionary
                }
            }
        }

        public void UpdateSmartMeter(UpdateSmartMeterModel _updateSmartMeterModel) {
            mSmartMeterHubContext.Clients.Groups(_updateSmartMeterModel.Id.GetGroupName(GroupType.SmartMeter))
                .UpdateSmartMeter(_updateSmartMeterModel.CopyPropertiesTo(new LocalSmartMeterDto()));
        }

        public void RequestHourlyForecast() {
            mSmartMeterHubContext.Clients.All.RequestHourlyForecast();
        }

        public void RequestMeterDataMonitoring() {
            mSmartMeterHubContext.Clients.All.RequestMeterDataMonitoring();
        }

        public void RequestBlockchainAccountBalance(Guid? _memberId)
        {
            if (_memberId == null) {
                throw new ServiceException(ServiceException.Type.MEMBER_ID_NOT_RESOLVABLE);
            }
            var memberId = (Guid)_memberId;
            
            var listenerDataMember = mRTListenerSingleton.RTListeners.GetValueOrDefault(memberId);

            if (listenerDataMember == null)
            {
                listenerDataMember = new RTListenerData() {};
                
                listenerDataMember.SignalRGroupName = memberId.GetGroupName(GroupType.Member);
                listenerDataMember.SmartMeterCount = mDb.Member
                    .Include(x => x.SmartMeters)
                    .Where(x => x.Id == memberId)
                    .Select(x => x.SmartMeters)
                    .Count();
                listenerDataMember.SmartMeterCountMember = listenerDataMember.SmartMeterCount;
                mRTListenerSingleton.RTListeners.Add(memberId, listenerDataMember);
            }
            
            Log.Information("RequestBlockchainAccountBalance()");
            mSmartMeterHubContext.Clients.Groups(listenerDataMember.SignalRGroupName).RequestBlockchainAccountBalance();
        }

        public void CreateConsentContract(Guid? _memberId, ConsentContractModel _consentContractModel)
        {
            if (_memberId == null) {
                throw new ServiceException(ServiceException.Type.MEMBER_ID_NOT_RESOLVABLE);
            }
            var memberId = (Guid)_memberId;
            
            var listenerDataMember = mRTListenerSingleton.RTListeners.GetValueOrDefault(memberId);

            if (listenerDataMember == null)
            {
                listenerDataMember = new RTListenerData() {};
                
                listenerDataMember.SignalRGroupName = memberId.GetGroupName(GroupType.Member);
                listenerDataMember.SmartMeterCount = mDb.Member
                    .Include(x => x.SmartMeters)
                    .Where(x => x.Id == memberId)
                    .Select(x => x.SmartMeters)
                    .Count();
                listenerDataMember.SmartMeterCountMember = listenerDataMember.SmartMeterCount;
                mRTListenerSingleton.RTListeners.Add(memberId, listenerDataMember);
            }
            
            mSmartMeterHubContext.Clients.Groups(listenerDataMember.SignalRGroupName).CreateConsentContract(_consentContractModel);
        }

        public void RequestContractsForMember(Guid? _memberId)
        {
            if (_memberId == null) {
                throw new ServiceException(ServiceException.Type.MEMBER_ID_NOT_RESOLVABLE);
            }
            var memberId = (Guid)_memberId;
            
            var listenerDataMember = mRTListenerSingleton.RTListeners.GetValueOrDefault(memberId);

            if (listenerDataMember == null)
            {
                listenerDataMember = new RTListenerData() {};
                
                listenerDataMember.SignalRGroupName = memberId.GetGroupName(GroupType.Member);
                listenerDataMember.SmartMeterCount = mDb.Member
                    .Include(x => x.SmartMeters)
                    .Where(x => x.Id == memberId)
                    .Select(x => x.SmartMeters)
                    .Count();
                listenerDataMember.SmartMeterCountMember = listenerDataMember.SmartMeterCount;
                mRTListenerSingleton.RTListeners.Add(memberId, listenerDataMember);
            }
            
            mSmartMeterHubContext.Clients.Groups(listenerDataMember.SignalRGroupName).RequestContractsForMember();
        }

        public void RequestDepositToContract(Guid? _memberId, ConsentContractModel _consentContract)
        {
            if (_memberId == null) {
                throw new ServiceException(ServiceException.Type.MEMBER_ID_NOT_RESOLVABLE);
            }
            var memberId = (Guid)_memberId;
            
            var listenerDataMember = mRTListenerSingleton.RTListeners.GetValueOrDefault(memberId);

            if (listenerDataMember == null)
            {
                listenerDataMember = new RTListenerData() {};
                
                listenerDataMember.SignalRGroupName = memberId.GetGroupName(GroupType.Member);
                listenerDataMember.SmartMeterCount = mDb.Member
                    .Include(x => x.SmartMeters)
                    .Where(x => x.Id == memberId)
                    .Select(x => x.SmartMeters)
                    .Count();
                listenerDataMember.SmartMeterCountMember = listenerDataMember.SmartMeterCount;
                mRTListenerSingleton.RTListeners.Add(memberId, listenerDataMember);
            }
            
            mSmartMeterHubContext.Clients.Groups(listenerDataMember.SignalRGroupName).RequestDepositToContract(_consentContract);
        }

        public void RequestWithdrawFromContract(Guid? _memberId, ConsentContractModel _consentContract)
        {
            if (_memberId == null) {
                throw new ServiceException(ServiceException.Type.MEMBER_ID_NOT_RESOLVABLE);
            }
            var memberId = (Guid)_memberId;
            
            var listenerDataMember = mRTListenerSingleton.RTListeners.GetValueOrDefault(memberId);

            if (listenerDataMember == null)
            {
                listenerDataMember = new RTListenerData() {};
                
                listenerDataMember.SignalRGroupName = memberId.GetGroupName(GroupType.Member);
                listenerDataMember.SmartMeterCount = mDb.Member
                    .Include(x => x.SmartMeters)
                    .Where(x => x.Id == memberId)
                    .Select(x => x.SmartMeters)
                    .Count();
                listenerDataMember.SmartMeterCountMember = listenerDataMember.SmartMeterCount;
                mRTListenerSingleton.RTListeners.Add(memberId, listenerDataMember);
            }
            
            mSmartMeterHubContext.Clients.Groups(listenerDataMember.SignalRGroupName).RequestWithdrawFromContract(_consentContract);
        }

        public void RequestHistoryData(RequestHistoryModel _requestHistoryModel)
        {
            var listenerDataMember = mRTListenerSingleton.RTListeners.GetValueOrDefault(_requestHistoryModel.RequestedMemberId);

            if (listenerDataMember == null)
            {
                listenerDataMember = new RTListenerData() {};
                
                listenerDataMember.SignalRGroupName = _requestHistoryModel.RequestedMemberId.GetGroupName(GroupType.Member);
                listenerDataMember.SmartMeterCount = mDb.Member
                    .Include(x => x.SmartMeters)
                    .Where(x => x.Id == _requestHistoryModel.RequestedMemberId)
                    .Select(x => x.SmartMeters)
                    .Count();
                listenerDataMember.SmartMeterCountMember = listenerDataMember.SmartMeterCount;
                mRTListenerSingleton.RTListeners.Add(_requestHistoryModel.RequestedMemberId, listenerDataMember);
            }
            
            mSmartMeterHubContext.Clients.Groups(listenerDataMember.SignalRGroupName).RequestHistoryData(_requestHistoryModel);
        }

        public void UpdateContractState(Guid? _memberId, UpdateContractState _updateContractState)
        {
            if (_memberId == null) {
                throw new ServiceException(ServiceException.Type.MEMBER_ID_NOT_RESOLVABLE);
            }
            var memberId = (Guid)_memberId;
            
            var listenerDataMember = mRTListenerSingleton.RTListeners.GetValueOrDefault(memberId);

            if (listenerDataMember == null)
            {
                listenerDataMember = new RTListenerData() {};
                
                listenerDataMember.SignalRGroupName = memberId.GetGroupName(GroupType.Member);
                listenerDataMember.SmartMeterCount = mDb.Member
                    .Include(x => x.SmartMeters)
                    .Where(x => x.Id == memberId)
                    .Select(x => x.SmartMeters)
                    .Count();
                listenerDataMember.SmartMeterCountMember = listenerDataMember.SmartMeterCount;
                mRTListenerSingleton.RTListeners.Add(memberId, listenerDataMember);
            }
            
            mSmartMeterHubContext.Clients.Groups(listenerDataMember.SignalRGroupName).RequestUpdateContractState(_updateContractState);
        }
    }
}
