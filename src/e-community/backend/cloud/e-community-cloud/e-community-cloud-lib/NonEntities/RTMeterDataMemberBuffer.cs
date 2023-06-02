using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.NonEntities {
    public class RTMeterDataMemberBuffer {
        /// <summary>
        /// number of measurements
        /// </summary>
        public int SmartMeterCount { get; set; }

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
