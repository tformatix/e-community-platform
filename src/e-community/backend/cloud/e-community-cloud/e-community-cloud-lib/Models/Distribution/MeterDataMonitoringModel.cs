using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.Models.Distribution
{
    public class MeterDataMonitoringModel
    {
        /// <summary>
        /// current energy A+
        /// </summary>
        public int ActiveEnergyPlus { get; set; }

        /// <summary>
        /// current energy A-
        /// </summary>
        public int ActiveEnergyMinus { get; set; }
    }
}
