using e_community_cloud_lib.Util.BaseClasses;
using System.Collections.Generic;

namespace e_community_cloud_lib.Database.Community
{
    /// <summary>
    /// specifies the type to a eCommunity (de: Erneuerbare Energiegemeinschaft or Bürgerenergiegemeinschaft)
    /// <see cref="EntityBase"/>
    /// </summary>
    public class ECommunityType : EntityBase
    {
        /// <summary>
        /// name of eCommunity type
        /// </summary>
        public string Name { get; set; }

        /// <summary>
        /// discounted percentage for grid price (local withing trafo)
        /// </summary>
        public double DiscountLocal { get; set; }

        /// <summary>
        /// discounted percentage for grid price (regional & low voltage, grid level 6 & 7)
        /// </summary>
        public double DiscountLowRegional { get; set; }

        /// <summary>
        /// discounted percentage for grid price (high voltage, grid level 4 & 5)
        /// </summary>
        public double DiscountHighRegional { get; set; }

        /// <summary>
        /// list of eCommunities which are this type
        /// </summary>
        public IList<ECommunity> ECommunities { get; set; }
    }
}
