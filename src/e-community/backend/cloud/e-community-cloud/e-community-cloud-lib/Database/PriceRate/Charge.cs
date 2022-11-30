using e_community_cloud_lib.Util.BaseClasses;
using System.Collections.Generic;

namespace e_community_cloud_lib.Database.PriceRate
{
    /// <summary>
    /// some charges a member has to do (e.g. electricity charge, green electricity, ...)
    /// <see cref="EntityBase"/>
    /// </summary>
    public class Charge: EntityBase
    {
        /// <summary>
        /// name of charge
        /// </summary>
        public string Name { get; set; }

        /// <summary>
        /// base rate of the charge (in €)
        /// </summary>
        public double BaseRate { get; set; }

        /// <summary>
        /// working price for 1 Wh
        /// </summary>
        public double WorkingPricePlus { get; set; }

        /// <summary>
        /// tax rate for the charge (in %)
        /// </summary>
        public double TaxRate { get; set; }

        /// <summary>
        /// flag if it is necesarry for the eCommunity
        /// </summary>
        public bool ApplyToECommunity { get; set; }

        /// <summary>
        /// list of grid price rates which are using this charge
        /// </summary>
        public IList<GridPriceRateCharge> GridPriceRateCharges { get; set; }
    }
}
