using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_local_lib.CloudDtos
{
    public class BufferedMeterDataRTDto
    {
        /// <summary>
        /// date and time of measuring point
        /// </summary>
        public DateTime Timestamp { get; set; }

        /// <summary>
        /// how many smart meters are missing overall
        /// </summary>
        public int MissingSmartMeterCount { get; set; }

        /// <summary>
        /// how many smart meters are missing from a member
        /// </summary>
        public int MissingSmartMeterCountMember { get; set; }

        /// <summary>
        /// current power P+
        /// </summary>
        public int ActivePowerPlus { get; set; }

        /// <summary>
        /// current power P-
        /// </summary>
        public int ActivePowerMinus { get; set; }

        /// <summary>
        /// current power R+
        /// </summary>
        public int ReactivePowerPlus { get; set; }

        /// <summary>
        /// current power R-
        /// </summary>
        public int ReactivePowerMinus { get; set; }

        /// <summary>
        /// current power P+ of whole eCommunity
        /// </summary>
        public int ECommunityActivePowerPlus { get; set; }

        /// <summary>
        /// current power P- of whole eCommunity
        /// </summary>
        public int ECommunityActivePowerMinus { get; set; }

        /// <summary>
        /// current power R+ of whole eCommunity
        /// </summary>
        public int ECommunityReactivePowerPlus { get; set; }

        /// <summary>
        /// current power R- of whole eCommunity
        /// </summary>
        public int ECommunityReactivePowerMinus { get; set; }
    }
}
