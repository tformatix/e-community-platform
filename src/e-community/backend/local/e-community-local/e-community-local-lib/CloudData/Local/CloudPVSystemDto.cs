using System;

namespace e_community_local_lib.CloudData.Local
{
    /// <summary>
    /// photovoltaic system (energy from the sun) 
    /// </summary>
    public class CloudPVSystemDto
    {
        public Guid Id { get; set; }

        /// <summary>
        /// maximum power peak (measured in wp - Watt Peak)
        /// </summary>
        public double PeakWP { get; set; }
    }
}
