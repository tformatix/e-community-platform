using e_community_cloud_lib.Util.Enums;
using e_community_cloud_lib.Database.General;
using System;

namespace e_community_cloud_lib.Database.Community
{
    /// <summary>
    /// eCommunity and its member with the designated permission
    /// </summary>
    public class ECommunityMembership
    {
        /// <summary>
        /// eCommunity (part of composite primary key)
        /// </summary>
        public Guid ECommunityId { get; set; }
        public ECommunity ECommunity { get; set; }

        /// <summary>
        /// member (part of composite primary key)
        /// </summary>
        public Guid MemberId { get; set; }
        public Member Member { get; set; }

        /// <summary>
        /// distribution of the overall energy feed (in percent)
        /// </summary>
        public Double? DistributionPercentage { get; set; }

        /// <summary>
        /// entry date of the member into the eCommunity
        /// </summary>
        public DateTime EntryDate { get; set; }

        /// <summary>
        /// date the member left the eCommunity
        /// </summary>
        public DateTime? LeftDate { get; set; }

        /// <summary>
        /// permission of the member
        /// </summary>
        public ECommunityPermission ECommunityPermission { get; set; }
    }
}
