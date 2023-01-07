using System;
using System.Collections.Generic;

namespace e_community_cloud.Dtos {
    public class MinimalECommunityDto {
        public Guid Id { get; set; }

        /// <summary>
        /// official name of eCommunity
        /// </summary>
        public string Name { get; set; }

        /// <summary>
        /// list of minimal members
        /// </summary>
        public List<MinimalMemberDto> Members { get; set; }
    }
}
