using System;
using System.Collections.Generic;

namespace e_community_local_lib.CloudData.Local
{
    /// <summary>
    /// represents a eCommunity (de: Energiegemeinschaft)
    /// </summary>
    public class CloudECommunityDto
    {
        public Guid Id { get; set; }
        public bool IsPowerFeedAllowed { get; set; }
    }
}
