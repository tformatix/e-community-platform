using System;
using System.Collections.Generic;

namespace e_community_local_lib.Database.General
{
    /// <summary>
    /// represents a eCommunity (de: Energiegemeinschaft)
    /// </summary>
    public class ECommunity
    {
        public Guid Id { get; set; }
        public bool IsPowerFeedAllowed { get; set; }
    }
}
