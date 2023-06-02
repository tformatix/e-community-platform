using System;

namespace e_community_cloud_lib.LocalDtos
{
    /// <summary>
    /// system for storing energy (like a battery)
    /// </summary>
    public class LocalBatterySystemDto
    {
        public Guid Id { get; set; }

        /// <summary>
        /// capacity of the battery system (measured in ah - Ampere Hour)
        /// </summary>
        public double CapacityAH { get; set; }
    }
}
