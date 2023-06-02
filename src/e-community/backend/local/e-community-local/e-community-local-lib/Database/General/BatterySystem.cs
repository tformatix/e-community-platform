using System;

namespace e_community_local_lib.Database.General
{
    /// <summary>
    /// system for storing energy (like a battery)
    /// </summary>
    public class BatterySystem
    {
        public Guid Id { get; set; }

        /// <summary>
        /// capacity of the battery system (measured in ah - Ampere Hour)
        /// </summary>
        public double CapacityAH { get; set; }
    }
}
