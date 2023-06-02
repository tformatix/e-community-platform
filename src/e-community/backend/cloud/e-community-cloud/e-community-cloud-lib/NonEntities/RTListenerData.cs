using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.NonEntities
{
    /// <summary>
    /// listener specific data (needed for the SignalR real time service)
    /// </summary>
    public class RTListenerData
    {
        /// <summary>
        /// id of an eCommunity
        /// </summary>
        public Guid? ECommunityId { get; set; }

        /// <summary>
        /// SignalR group name
        /// </summary>
        public string SignalRGroupName { get; set; }

        /// <summary>
        /// number of Smart Meters overall
        /// </summary>
        public int SmartMeterCount { get; set; }

        /// <summary>
        /// number of Smart Meters of an member
        /// </summary>
        public int SmartMeterCountMember { get; set; }

        /// <summary>
        /// number of active devices of a member
        /// </summary>
        public int ActiveDeviceCount { get; set; }

        /// <summary>
        /// timestamp where last real time were sent
        /// </summary>
        public DateTime PreviousSentTimestamp { get; set; }

        /// <summary>
        /// all members of the eCommunity
        /// </summary>
        public List<Guid> ECommunityMembers { get; set; }
    }
}
