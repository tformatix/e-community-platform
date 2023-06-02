using e_community_cloud_lib.Util.BaseClasses;
using System;

namespace e_community_cloud_lib.Database.Local
{
    /// <summary>
    /// energy profile of the member (values are the calculated average)
    /// calculation will run frequently (every 1h for example)
    /// <see cref="EntityBase"/>
    /// </summary>
    public class MeterDataProfile : EntityBase
    {
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

        /// <summary>
        /// working price for 1 kWh (energy consumption in €)
        /// </summary>
        public double WorkingPricePlus { get; set; }

        /// <summary>
        /// working price for 1 kWh (energy feed in €)
        /// </summary>
        public double WorkingPriceMinus { get; set; }

        /// <summary>
        /// smart meter to this data
        /// <seealso cref="SmartMeter"/>
        /// </summary>
        public Guid SmartMeterId { get; set; }
        public SmartMeter SmartMeter { get; set; }

        /// <summary>
        /// correspondending event
        /// <seealso cref="EventCase"/>
        /// </summary>
        public Guid EventCaseId { get; set; }
        public EventCase EventCase { get; set; }
    }
}
