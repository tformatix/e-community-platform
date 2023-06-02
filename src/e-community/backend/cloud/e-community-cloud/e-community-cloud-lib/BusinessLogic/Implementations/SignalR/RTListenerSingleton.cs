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

namespace e_community_cloud_lib.BusinessLogic.Implementations.SignalR {
    public class RTListenerSingleton : IRTListenerSingleton {
        public Dictionary<Guid, RTListenerData> RTListeners { get; }
        public Dictionary<Guid, List<RTMeterDataBuffer>> RTBuffer { get; }

        public RTListenerSingleton() {
            RTListeners = new Dictionary<Guid, RTListenerData>();
            RTBuffer = new Dictionary<Guid, List<RTMeterDataBuffer>>();
        }

        public RTListenerData GetRTListenerData(Guid _memberId) {
            var listenerData = RTListeners.GetValueOrDefault(_memberId);
            if (listenerData == null) {
                listenerData = RTListeners
                    .Select(x => x.Value)
                    .FirstOrDefault(x => x?.ECommunityMembers?.Contains(_memberId) == true);
            }
            return listenerData;
        }

        public List<KeyValuePair<Guid, RTListenerData>> GetListeners(Guid _eCommunityId) {
            return RTListeners
                .Where(x => x.Value.ECommunityId == _eCommunityId)
                .ToList();
        }
    }
}
