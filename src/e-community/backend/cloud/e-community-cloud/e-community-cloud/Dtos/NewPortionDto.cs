using System;

namespace e_community_cloud.Dtos {
    public class NewPortionDto {
        public Guid SmartMeterId { get; set; }
        public string SmartMeterName { get; set; }
        public int EstimatedActiveEnergyPlus { get; set; }
        public int Deviation { get; set; }
        public int Flexibility { get; set; }
    }
}
