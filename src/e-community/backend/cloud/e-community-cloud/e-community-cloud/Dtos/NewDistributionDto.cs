using System;
using System.Collections.Generic;

namespace e_community_cloud.Dtos {
    public class NewDistributionDto {
        public List<NewPortionDto> NewPortions { get; set; }
        public int UnassignedActiveEnergyMinus { get; set; }
        public int MissingSmartMeterCount { get; set; }
    }
}
