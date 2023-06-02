using e_community_cloud_lib.Util.Enums;
using e_community_cloud_lib.Database.Local;
using e_community_cloud_lib.Util.BaseClasses;
using System;
using System.Collections.Generic;

namespace e_community_cloud_lib.Database.PriceRate
{
    /// <summary>
    /// power grid price rate
    /// <see cref="EntityBase"/>
    /// </summary>
    public class GridPriceRate: EntityBase
    {
        /// <summary>
        /// name of grid price rate
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
        /// list of charges
        /// </summary>
        public IList<Charge> Charges { get; set; }

        /// <summary>
        /// list of smart meters which are served by this price rate
        /// </summary>
        public IList<SmartMeter> SmartMeters { get; set; }
    }
}
