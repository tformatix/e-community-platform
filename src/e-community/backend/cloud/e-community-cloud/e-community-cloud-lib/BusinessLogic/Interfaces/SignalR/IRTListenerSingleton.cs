using e_community_cloud_lib.Endpoints.Dtos;
using e_community_cloud_lib.NonEntities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.BusinessLogic.Interfaces.SignalR {
    public interface IRTListenerSingleton {
        /// <summary>
        /// currently active end device listeners <br/>
        /// Key: MemberId <br/>
        /// Value: RealTimeListenerData (eCommunityId, ActiveDeviceCount, SmartMeterCount, SignalRGroupName)
        /// </summary>
        Dictionary<Guid, RTListenerData> RTListeners { get; }

        /// <summary>
        /// buffers real time meter data </br>
        /// Key: eCommunityId OR memberId (if member is not part of an eCommunity) <br/>
        /// Value: list of meter mata of all smart meters at a specific timestamp
        /// </summary>
        Dictionary<Guid, List<RTMeterDataBuffer>> RTBuffer { get; }

        /// <returns>listener specific data based on the member id</returns>
        RTListenerData GetRTListenerData(Guid _memberId);

        /// <returns>list of listening members based on eCommunity id</returns>
        List<KeyValuePair<Guid, RTListenerData>> GetListeners(Guid _eCommunityId);
    }
}
