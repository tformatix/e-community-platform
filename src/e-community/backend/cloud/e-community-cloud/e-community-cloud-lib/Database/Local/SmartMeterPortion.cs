using e_community_cloud_lib.Database.Community;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.Database.Local
{
    /// <summary>
    /// assigned portion of a smart meter (for a specific distribution entry)
    /// </summary>
    public class SmartMeterPortion
    {
        public int ECommunityDistributionId { get; set; }
        public ECommunityDistribution ECommunityDistribution { get; set; }

        public Guid SmartMeterId { get; set; }
        public SmartMeter SmartMeter { get; set; }

        // <summary>
        /// estimated energy A- (feed in)
        /// </summary>
        public int EstimatedActiveEnergyMinus { get; set; }

        // <summary>
        /// estimated energy A+ (consumption)
        /// </summary>
        public int EstimatedActiveEnergyPlus { get; set; }

        // <summary>
        /// deviation from estimated A+ (consumption)
        /// </summary>
        public int Deviation { get; set; }

        /// <summary>
        /// flexibility
        /// </summary>
        public int Flexibility { get; set; }

        /// <summary>
        /// forecast from smart meter
        /// </summary>
        public bool ForecastFromSmartMeter { get; set; }

        // <summary>
        /// actual energy A- (feed in)
        /// </summary>
        public int? ActualActiveEnergyMinus { get; set; }

        // <summary>
        /// actual energy A+ (consumption)
        /// </summary>
        public int? ActualActiveEnergyPlus { get; set; }

        /// <summary>
        /// did user acknowledged flexibility
        /// </summary>
        public bool Acknowledged { get; set; }
    }
}
