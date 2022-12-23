using e_community_cloud_lib.Util.Enums;
using e_community_cloud_lib.Database.General;
using e_community_cloud_lib.Database.PriceRate;
using e_community_cloud_lib.Util.BaseClasses;
using System;
using System.Collections.Generic;

namespace e_community_cloud_lib.Database.Community
{
    /// <summary>
    /// represents a eCommunity (de: Energiegemeinschaft)
    /// <see cref="EntityBase"/>
    /// </summary>
    public class ECommunity: EntityBase
    {
        /// <summary>
        /// official name of eCommunity
        /// </summary>
        public string Name { get; set; }

        /// <summary>
        /// flag if the eCommunity is visible to the public
        /// </summary>
        public bool IsPublic { get; set; }

        /// <summary>
        /// flag if the eCommunity is offical registered (at the state of austria)
        /// </summary>
        public bool IsOfficial { get; set; }

        /// <summary>
        /// flag if the eCommunity is closed for new member
        /// </summary>
        public bool IsClosed { get; set; }

        /// <summary>
        /// short description of the eCommunity itself (e.g. WhatsApp group link)
        /// </summary>
        public string Description { get; set; }

        /// <summary>
        /// days which the member has to be in eCommunity
        /// </summary>
        public int MemberDayBinding { get; set; }

        /// <summary>
        /// distribution mode of eCommunity
        /// </summary>
        public DistributionMode? DistributionMode { get; set; }

        /// <summary>
        /// price rate, which describes the conditions of the eCommunity (what member pays for energy of the eCommunity)
        /// <seealso cref="SupplierPriceRate"/>
        /// </summary>
        public Guid? SupplierPriceRateId { get; set; }
        public SupplierPriceRate SupplierPriceRate { get; set; }

        /// <summary>
        /// legal form of eCommunity
        /// <seealso cref="LegalForm"/>
        /// </summary>
        public Guid? LegalFormId { get; set; }
        public LegalForm LegalForm { get; set; }

        /// <summary>
        /// type of eCommunity (EEG or Bürgerenergiegemeinschaft)
        /// <seealso cref="ECommunityType"/>
        /// </summary>
        public Guid? ECommunityTypeId { get; set; }
        public ECommunityType ECommunityType { get; set; }

        /// <summary>
        /// list of members and their permissions of the eCommunity
        /// </summary>
        public IList<ECommunityMembership> ECommunityMemberships { get; set; }

        /// <summary>
        /// list of energy distributions
        /// </summary>
        public IList<ECommunityDistribution> ECommunityDistributions { get; set; }
    }
}
