using System;
using System.Collections.Generic;

namespace e_community_local_lib.Database.PriceRate
{
    /// <summary>
    /// energy supplier rate
    /// </summary>
    public class SupplierPriceRate
    {
        public Guid Id { get; set; }

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
    }
}
