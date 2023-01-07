using e_community_cloud_lib.Database.Local;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.NonEntities
{
    public class NewDistribution {
        /// <summary>
        /// portions of new distribution
        /// </summary>
        public List<SmartMeterPortion> SmartMeterPortions { get; set; }

        /// <summary>
        /// unassigned A- (feed-in)
        /// </summary>
        public int UnassignedActiveEnergyMinus { get; set; }

        /// <summary>
        /// number of missing forecasts from smart meters
        /// </summary>
        public int MissingSmartMeterCount { get; set; }
    }
}
