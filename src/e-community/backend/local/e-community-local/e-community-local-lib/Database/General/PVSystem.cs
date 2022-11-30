using System;

namespace e_community_local_lib.Database.General
{
    /// <summary>
    /// photovoltaic system (energy from the sun) 
    /// </summary>
    public class PVSystem
    {
        public Guid Id { get; set; }

        /// <summary>
        /// maximum power peak (measured in wp - Watt Peak)
        /// </summary>
        public double PeakWP { get; set; }
    }
}
