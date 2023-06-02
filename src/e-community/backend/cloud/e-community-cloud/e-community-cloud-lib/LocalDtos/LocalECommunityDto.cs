using System;
using System.Collections.Generic;

namespace e_community_cloud_lib.LocalDtos
{
    /// <summary>
    /// represents a eCommunity (de: Energiegemeinschaft)
    /// </summary>
    public class LocalECommunityDto
    {
        public Guid Id { get; set; }
        public bool IsPowerFeedAllowed { get; set; }
    }
}
