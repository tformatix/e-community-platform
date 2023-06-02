using System;

namespace e_community_cloud_lib.Models.History
{
    /// <summary>
    /// base class of meter data
    /// </summary>
    public class MeterDataBase
    {
        public int Id { get; set; }

        /// <summary>
        /// date and time of measuring point
        /// </summary>
        public DateTime Timestamp { get; set; }

        /// <summary>
        /// meter reading energy A+
        /// </summary>
        public int ActiveEnergyPlus { get; set; }

        /// <summary>
        /// meter reading energy A-
        /// </summary>
        public int ActiveEnergyMinus { get; set; }

        /// <summary>
        /// meter reading energy R+
        /// </summary>
        public int ReactiveEnergyPlus { get; set; }

        /// <summary>
        /// meter reading energy R-
        /// </summary>
        public int ReactiveEnergyMinus { get; set; }

        /// <summary>
        /// current power P+
        /// </summary>
        public int ActivePowerPlus { get; set; }

        /// <summary>
        /// current power P-
        /// </summary>
        public int ActivePowerMinus { get; set; }

        /// <summary>
        /// current power R+
        /// </summary>
        public int ReactivePowerPlus { get; set; }

        /// <summary>
        /// current power R-
        /// </summary>
        public int ReactivePowerMinus { get; set; }

        /// <summary>
        /// dept collection counter
        /// </summary>
        public int PrepaymentCounter { get; set; }
    }
}
