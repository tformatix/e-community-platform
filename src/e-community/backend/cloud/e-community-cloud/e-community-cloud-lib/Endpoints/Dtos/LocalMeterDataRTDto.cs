using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.Endpoints.Dtos
{
    public class LocalMeterDataRTDto
    {
        public Guid SmartMeterId { get; set; }

        /// <summary>
        /// date and time of measuring point
        /// </summary>
        public DateTime Timestamp { get; set; }

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
