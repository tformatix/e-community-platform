using e_community_cloud_lib.Database.Community;
using e_community_cloud_lib.Util.Enums;
using e_community_cloud_lib.Database.Local;
using e_community_cloud_lib.Util.BaseClasses;
using System;
using System.Collections.Generic;

namespace e_community_cloud_lib.Database.PriceRate
{
    /// <summary>
    /// energy supplier rate
    /// <see cref="EntityBase"/>
    /// </summary>
    public class SupplierPriceRate: EntityBase
    {
        /// <summary>
        /// name of price rate
        /// </summary>
        public string Name { get; set; }

        /// <summary>
        /// base rate
        /// </summary>
        public double BaseRate { get; set; }

        /// <summary>
        /// working price for 1 kWh (energy consumption in €)
        /// </summary>
        public double WorkingPricePlus { get; set; }

        /// <summary>
        /// working price for 1 kWh (energy feed in €)
        /// </summary>
        public double WorkingPriceMinus { get; set; }

        /// <summary>
        /// performance price (needed for calculation of BaseRate)
        /// </summary>
        public double PricePerPeak { get; set; }

        /// <summary>
        /// tax rate (in %)
        /// </summary>
        public double TaxRate { get; set; }

        /// <summary>
        /// provider of the power grid
        /// <seealso cref="Provider"/>
        /// </summary>
        public Guid ProviderId { get; set; }
        public Provider Provider { get; set; }

        /// <summary>
        /// type of this price rate
        /// </summary>
        public PriceRateType PriceRateType { get; set; }

        /// <summary>
        /// list of smart meters which are served by this price rate
        /// </summary>
        public IList<SmartMeter> SmartMeters { get; set; }

        /// <summary>
        /// list of eCommunities which are using this price rate for their own pricing
        /// </summary>
        public IList<ECommunity> ECommunities { get; set; }
    }
}
