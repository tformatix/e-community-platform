using System;
using System.Collections.Generic;

namespace e_community_cloud.Dtos {
    public class NewDistributionDto {
        /// <summary>
        /// list of new portions (smart meter dependant)
        /// </summary>
        public List<NewPortionDto> NewPortions { get; set; }

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
