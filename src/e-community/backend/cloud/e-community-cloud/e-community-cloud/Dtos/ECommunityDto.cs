using System;

namespace e_community_cloud.Dtos
{
    public class ECommunityDto
    {
        public Guid Id { get; set; }
        public String Name { get; set; }
        public bool IsPublic { get; set; }
        public bool IsOfficial { get; set; }
        public bool IsClosed { get; set; }
    }
}
