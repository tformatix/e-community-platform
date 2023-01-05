using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_local_lib.CloudData {
    public class ProcessedMeterDataRTDto {
        /// <summary>
        /// id of member
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
