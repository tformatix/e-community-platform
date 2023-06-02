using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using e_community_cloud_lib.Database.General;

namespace e_community_cloud_lib.Database.Local
{
    /// <summary>
    /// meter data for a specific monitoring entry
    /// </summary>
    public class MeterDataMonitoring {
        public int MonitoringId { get; set; }
        public Monitoring Monitoring { get; set; }
        public Guid SmartMeterId { get; set; }
        public SmartMeter SmartMeter { get; set; }

        // <summary>
        /// actual energy A+ (consumption)
        /// </summary>
        public int? ActiveEnergyPlus { get; set; }

        // <summary>
        /// actual energy A- (feed in)
        /// </summary>
        public int? ActiveEnergyMinus { get; set; }

        /// <summary>
        /// energy projected to 1 hour (e.g. 12:10 --> *6)
        /// </summary>
        public int? ProjectedActiveEnergyPlus { get; set; }

        /// <summary>
        /// energy projected to 1 hour (e.g. 12:10 --> *6)
        /// </summary>
        public int? ProjectedActiveEnergyMinus { get; set; }

        /// <summary>
        /// non compliance of forecast
        /// </summary>
        public bool NonCompliance { get; set; }
    }
}
