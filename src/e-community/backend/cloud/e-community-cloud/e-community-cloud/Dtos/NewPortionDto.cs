using System;

namespace e_community_cloud.Dtos {
    public class NewPortionDto {
        public Guid SmartMeterId { get; set; }

        /// <summary>
        /// name of smart meter
        /// </summary>
        public string SmartMeterName { get; set; }

        /// <summary>
        /// estimated energy A+ (consumption)
        /// </summary>
        public int EstimatedActiveEnergyPlus { get; set; }

        /// <summary>
        /// deviation from estimated A+ (consumption)
        /// </summary>
        public int Deviation { get; set; }

        /// <summary>
        /// flexibility
        /// </summary>
        public int Flexibility { get; set; }
    }
}
