using e_community_cloud_lib.Util.BaseClasses;
using System;
using e_community_cloud_lib.Database.Local;

namespace e_community_cloud_lib.Database.Community
{
    /// <summary>
    /// all transactions to the eCommunity from the Smart Meters
    /// <see cref="EntityBase"/>
    /// </summary>
    public class ECommunityTransaction : EntityBase
    {
        /// <summary>
        /// energy consumption (in Wh)
        /// </summary>
        public int ECommunityEnergyPlus { get; set; }

        /// <summary>
        /// energy feed in (in Wh)
        /// </summary>
        public int ECommunityEnergyMinus { get; set; }

        /// <summary>
        /// price for 1 kWh (energy consumption)
        /// </summary>
        public double ECommunityPricePlus { get; set; }

        /// <summary>
        /// price for 1 kWh (energy feed in)
        /// </summary>
        public double ECommunityPriceMinus { get; set; }

        /// <summary>
        /// energy supplier consumption (in Wh)
        /// </summary>
        public int SupplierEnergyPlus { get; set; }

        /// <summary>
        /// energy supplier feed in (in Wh)
        /// </summary>
        public int SupplierEnergyMinus { get; set; }

        /// <summary>
        /// energy supplier consumption price for 1 kWh
        /// </summary>
        public double SupplierPricePlus { get; set; }

        /// <summary>
        /// energy supplier feed in price for 1 kWh
        /// </summary>
        public double SupplierPriceMinus { get; set; }

        /// <summary>
        /// power grid price for 1 kWh
        /// </summary>
        public double GridPricePlus { get; set; }

        /// <summary>
        /// reference to an entry of a specific meter data entry of an eCommunity
        /// <seealso cref="MeterDataECommunity"/>
        /// </summary>
        public Guid MeterDataECommunityId { get; set; }
        public MeterDataECommunity MeterDataECommunity { get; set; }

        /// <summary>
        /// reference to a Smart Meter
        /// <seealso cref="SmartMeter"/>
        /// </summary>
        public Guid SmartMeterId { get; set; }
        public SmartMeter SmartMeter { get; set; }
    }
}
