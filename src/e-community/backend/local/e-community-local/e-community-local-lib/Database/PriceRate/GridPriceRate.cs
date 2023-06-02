using System;
using System.Collections.Generic;

namespace e_community_local_lib.Database.PriceRate
{
    /// <summary>
    /// power grid price rate
    /// </summary>
    public class GridPriceRate
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
        /// tax rate (in %)
        /// </summary>
        public double TaxRate { get; set; }

        /// <summary>
        /// network access level
        /// grid level 1: extra-high voltage grid
        /// grid level 2: substation network
        /// grid level 3: high voltage network
        /// grid level 4: substation network between high and medium voltage
        /// grid level 5: medium voltage network
        /// grid level 6: transformer stations between medium and low voltage
        /// grid level 7: low voltage network
        /// </summary>
        public int GridLevel { get; set; }
    }
}
