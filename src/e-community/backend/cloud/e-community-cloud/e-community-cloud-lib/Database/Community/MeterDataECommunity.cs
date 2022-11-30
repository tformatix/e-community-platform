using e_community_cloud_lib.Util.BaseClasses;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;

namespace e_community_cloud_lib.Database.Community
{
    /// <summary>
    /// holds the current +/- values of the eCommunity (for example: how many Wh are available in the pool?)
    /// <see cref="EntityBase"/>
    /// </summary>
    public class MeterDataECommunity: EntityBase
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
        /// meter data of this eCommunity
        /// <seealso cref="ECommunity"/>
        /// </summary>
        public Guid ECommunityId { get; set; }
        public ECommunity ECommunity { get; set; }

        /// <summary>
        /// list of transactions of this meter data entry
        /// </summary>
        public IList<ECommunityTransaction> ECommunityTransactions { get; set; }
    }
}
