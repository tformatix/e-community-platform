using e_community_cloud_lib.Util.BaseClasses;
using System;

namespace e_community_cloud_lib.Database.Local
{
    /// <summary>
    /// photovoltaic system (energy from the sun) 
    /// <see cref="EntityBase"/>
    /// </summary>
    public class PVSystem: EntityBase
    {
        /// <summary>
        /// maximum power peak (measured in wp - Watt Peak)
        /// </summary>
        public double PeakWP { get; set; }

        /// <summary>
        /// smart meter of this pv
        /// <seealso cref="SmartMeter"/>
        /// </summary>
        public Guid SmartMeterId { get; set; }
        public SmartMeter SmartMeter { get; set; }
    }
}
