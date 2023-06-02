using e_community_cloud_lib.Database.Community;
using e_community_cloud_lib.Util.Enums;
using e_community_cloud_lib.Database.Local;
using Microsoft.AspNetCore.Identity;
using System;
using System.Collections.Generic;

namespace e_community_cloud_lib.Database.General
{
    /// <summary>
    /// table for storing the "users"
    /// a member can be a private person, organization, club, GmbH, ...
    /// <see cref="IdentityUser"/>
    /// </summary>
    public class Member : IdentityUser<Guid>
    {
        /// <summary>
        /// street number of residence
        /// </summary>
        public string StreetNr { get; set; }

        /// <summary>
        /// zip code of residence
        /// </summary>
        public string ZipCode { get; set; }

        /// <summary>
        /// city name of residence
        /// </summary>
        public string CityName { get; set; }

        /// <summary>
        /// country code (e.g. AT) of residence
        /// </summary>
        public string CountryCode { get; set; }

        /// <summary>
        /// is email address public to other users
        /// </summary>
        public bool IsEmailPublic { get; set; }

        /// <summary>
        /// id of the transformer (Trafo)
        /// </summary>
        public int TransformerId { get; set; }

        /// <summary>
        /// id of the substation (Umspannwerk)
        /// </summary>
        public int SubstationId { get; set; }

        /// <summary>
        /// highest grid level of member
        /// </summary>
        public int GridLevel { get; set; }

        /// <summary>
        /// supply mode of member (how much energy to eCommunity pool)
        /// </summary>
        public SupplyMode? SupplyMode { get; set; }

        /// <summary>
        /// language of member
        /// <seealso cref="Language"/>
        /// </summary>
        public Guid LanguageId { get; set; }
        public Language Language { get; set; }

        /// <summary>
        /// legal form of member
        /// <seealso cref="LegalForm"/>
        /// </summary>
        public Guid? LegalFormId { get; set; }
        public LegalForm LegalForm { get; set; }

        /// <summary>
        /// list of smart meters of the member
        /// </summary>
        public IList<SmartMeter> SmartMeters { get; set; }

        /// <summary>
        /// list of their permissions and memberships in eCommunities
        /// </summary>
        public IList<ECommunityMembership> ECommunityMemberships { get; set; }

        /// <summary>
        /// list of their firebase cloud messaging tokens
        /// </summary>
        public IList<MemberFCMToken> MemberFCMTokens { get; set; }
        
        /// <summary>
        /// stores the personal address for the blockchain account
        /// </summary>
        //public string BlockchainAddress { get; set; }
    }
}
