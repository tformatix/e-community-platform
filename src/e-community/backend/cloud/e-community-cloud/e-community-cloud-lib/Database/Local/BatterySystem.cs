using e_community_cloud_lib.Util.BaseClasses;
using System;

namespace e_community_cloud_lib.Database.Local
{
    /// <summary>
    /// system for storing energy (like a battery)
    /// <see cref="EntityBase"/>
    /// </summary>
    public class BatterySystem: EntityBase
    {
        /// <summary>
        /// capacity of the battery system (measured in ah - Ampere Hour)
        /// </summary>
        public double CapacityAH { get; set; }

        /// <summary>
        /// smart meter of this battery
        /// <seealso cref="SmartMeter"/>
        /// </summary>
        public Guid SmartMeterId { get; set; }
        public SmartMeter SmartMeter { get; set; }
    }
}
