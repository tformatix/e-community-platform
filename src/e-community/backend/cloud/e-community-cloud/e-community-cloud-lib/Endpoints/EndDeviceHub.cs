using e_community_cloud_lib.BusinessLogic.Interfaces.SignalR;
using e_community_cloud_lib.Database;
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

namespace e_community_cloud_lib.Endpoints
{
    /// <summary>
    /// handles SignalR communication with End Device (real time)
    /// <seealso cref="Hub"/>
    /// <seealso cref="IEndDeviceSignalRSender"/>
    /// </summary>
    [Authorize]
    public class EndDeviceHub : Hub<IEndDeviceSignalRSender>
    {
        private readonly ECommunityCloudContext mDb;

        public EndDeviceHub(
            ECommunityCloudContext _db
        ) {
            mDb = _db;
        }

        public override async Task OnConnectedAsync()
        {
            var memberId = Context.User.GetMemberId();
            if (memberId != null)
            {
                await Groups.AddToGroupAsync(Context.ConnectionId, memberId?.GetGroupName(GroupType.Member));
                var eCommunityId = mDb.GetECommunityId(memberId);
                if (eCommunityId != null)
                {
                    await Groups.AddToGroupAsync(Context.ConnectionId, eCommunityId?.GetGroupName(GroupType.ECommunity));
                }

                Log.Information($"Endpoint/EndDevice::Connected #{memberId}");
                await base.OnConnectedAsync();
            }
        }

        public override Task OnDisconnectedAsync(Exception exception) {
            var memberId = Context.User.GetMemberId();
            Log.Information($"Endpoint/EndDevice::Disconnected #{memberId}");
            return base.OnDisconnectedAsync(exception);
        }
    }
}
