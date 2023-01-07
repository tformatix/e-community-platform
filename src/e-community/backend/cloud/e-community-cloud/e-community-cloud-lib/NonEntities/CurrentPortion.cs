using e_community_cloud_lib.Database.Local;

namespace e_community_cloud_lib.NonEntities {
    public class CurrentPortion {
        /// <summary>
        /// estimated energy A+ (consumption)
        /// </summary>
        public int EstimatedActiveEnergyPlus { get; set; }

        /// <summary>
        /// flexibility
        /// </summary>
        public int Flexibility { get; set; }

        // <summary>
        /// deviation from estimated A+ (consumption)
        /// </summary>
        public int Deviation { get; set; }

        /// <summary>
        /// unassigned A- (feed-in)
        /// </summary>
        public int UnassignedActiveEnergyMinus { get; set; }

        /// <summary>
        /// number of missing forecasts from smart meters
        /// </summary>
        public int MissingSmartMeterCount { get; set; }

        /// <summary>
        /// is relevant distribution
        /// </summary>
        public bool IsRelevant { get; set; }

        /// <summary>
        /// referencing smart meter
        /// </summary>
        public SmartMeter SmartMeter { get; set; }
    }
}
