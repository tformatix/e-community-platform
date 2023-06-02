using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.Models.Distribution
{
    public class ForecastModel
    {
        /// <summary>
        /// id of smart meter
        /// </summary>
        public Guid SmartMeterId { get; set; }

        /// <summary>
        /// estimated energy A+ (consumption)
        /// </summary>
        public int ActiveEnergyPlus { get; set; }

        /// <summary>
        /// estimated energy A- (feed in)
        /// </summary>
        public int ActiveEnergyMinus { get; set; }

        /// <summary>
        /// estimated flexibility
        /// </summary>
        public int Flexibility { get; set; }
    }
}
