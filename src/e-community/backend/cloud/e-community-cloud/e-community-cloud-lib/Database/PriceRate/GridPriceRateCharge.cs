using System;

namespace e_community_cloud_lib.Database.PriceRate
{
    /// <summary>
    /// a power grid rate can have multiple charges
    /// </summary>
    public class GridPriceRateCharge
    {
        /// <summary>
        /// grid price rate (part of composite primary key)
        /// <seealso cref="GridPriceRate"/>
        /// </summary>
        public Guid GridPriceRateId { get; set; }
        public GridPriceRate GridPriceRate { get; set; }

        /// <summary>
        /// charge (part of composite primary key)
        /// <seealso cref="Charge"/>
        /// </summary>
        public Guid ChargeId { get; set; }
        public Charge Charge { get; set; }
    }
}
