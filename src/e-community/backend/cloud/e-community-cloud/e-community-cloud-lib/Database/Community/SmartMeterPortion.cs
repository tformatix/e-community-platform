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
        /// portion (consumption)
        /// </summary>
        public int? Portion { get; set; }

        /// <summary>
        /// flexibility
        /// </summary>
        public int? Flexibility { get; set; }

        /// <summary>
        /// did user acknowledged estimated consumption
        /// </summary>
        public bool Acknowledged { get; set; }
    }
}
