using e_community_cloud_lib.Database.Community;
using e_community_cloud_lib.Util.BaseClasses;
using System.Collections.Generic;

namespace e_community_cloud_lib.Database.General
{
    /// <summary>
    /// legal form of the member/eCommunity (like organization, GmbH, private)
    /// <see cref="EntityBase"/>
    /// </summary>
    public class LegalForm: EntityBase
    {
        /// <summary>
        /// name of the legal form
        /// </summary>
        public string Name { get; set; }

        /// <summary>
        /// list of eCommunities of this legal form
        /// </summary>
        public IList<ECommunity> ECommunities { get; set; }

        /// <summary>
        /// list of members of this legal form
        /// </summary>
        public IList<Member> Members { get; set; }
    }
}
