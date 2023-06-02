using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.Endpoints.Dtos
{
    /// <summary>
    /// real time meter data
    /// </summary>
    public class MeterDataRTDto
    {
        /// <summary>
        /// id of smart meter
        /// </summary>
        public Guid SmartMeterId { get; set; }

        /// <summary>
        /// id of member
        /// </summary>
        public Guid MemberId { get; set; }

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
    }
}
