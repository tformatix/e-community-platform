using System;

namespace e_community_cloud_lib.LocalDtos
{
    /// <summary>
    /// photovoltaic system (energy from the sun) 
    /// </summary>
    public class LocalPVSystemDto
    {
        public Guid Id { get; set; }

        /// <summary>
        /// maximum power peak (measured in wp - Watt Peak)
        /// </summary>
        public double PeakWP { get; set; }
    }
}
