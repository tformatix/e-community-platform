using e_community_cloud_lib.Util.BaseClasses;
using System.Collections.Generic;

namespace e_community_cloud_lib.Database.PriceRate
{
    /// <summary>
    /// provider of the energy/grid (like EnergieAG, Linz AG, ...)
    /// </summary>
    public class Provider: EntityBase
    {
        /// <summary>
        /// provider name
        /// </summary>
        public string Name { get; set; }

        /// <summary>
        /// list of grid price rates
        /// </summary>
        public IList<GridPriceRate> GridPriceRates { get; set; }

        /// <summary>
        /// list of supplier price rates
        /// </summary>
        public IList<SupplierPriceRate> SupplierPriceRates { get; set; }
    }
}
