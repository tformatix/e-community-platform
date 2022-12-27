using e_community_cloud_lib.Database.Local;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.Database.Community {
    public class SmartMeterPortion {
        public int ECommunityDistributionId { get; set; }
        public ECommunityDistribution ECommunityDistribution { get; set; }

        public Guid SmartMeterId { get; set; }
        public SmartMeter SmartMeter { get; set; }

        // <summary>
        /// estimated energy A- (feed in)
        /// </summary>
        public int? EstimatedActiveEnergyMinus { get; set; }

        // <summary>
        /// estimated energy A+ (consumption)
        /// </summary>
        public int? EstimatedActiveEnergyPlus { get; set; }

        // <summary>
        /// actual energy A+ (consumption)
        /// </summary>
        public int? ActualActiveEnergyPlus { get; set; }

        // <summary>
        /// deviation from estimated A+ (consumption)
        /// </summary>
        public int? Deviation { get; set; }

        /// <summary>
        /// flexibility
        /// </summary>
        public int? Flexibility { get; set; }

        /// <summary>
        /// is portion optimized
        /// </summary>
        public bool Optimized { get; set; }

        /// <summary>
        /// did user acknowledged estimated consumption
        /// </summary>
        public bool Acknowledged { get; set; }
    }
}
