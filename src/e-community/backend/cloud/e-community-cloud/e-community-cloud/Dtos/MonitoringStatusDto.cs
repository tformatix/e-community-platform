using System;

namespace e_community_cloud.Dtos {
    public class MonitoringStatusDto {
        public Guid SmartMeterId { get; set; }
        public string SmartMeterName { get; set; }
        public int? EstimatedActiveEnergyPlus { get; set; }
        public int? ProjectedActiveEnergyPlus { get; set; }
    }
}
