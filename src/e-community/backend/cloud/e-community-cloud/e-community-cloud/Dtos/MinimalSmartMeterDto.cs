using System;

namespace e_community_cloud.Dtos {
    public class MinimalSmartMeterDto {
        public Guid Id { get; set; }

        /// <summary>
        /// name of the device
        /// </summary>
        public string Name { get; set; }

        /// <summary>
        /// short description of the device
        /// </summary>
        public string Description { get; set; }

        /// <summary>
        /// marks the main raspberry (only one weather calculation per household)
        /// </summary>
        public bool IsMain { get; set; }
    }
}
