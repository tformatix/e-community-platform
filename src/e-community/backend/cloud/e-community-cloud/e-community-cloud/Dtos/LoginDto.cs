using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace e_community_cloud.Dtos
{
    public class LoginDto
    {
        public Guid MemberId { get; set; }
        public string AccessToken { get; set; }
        public string RefreshToken { get; set; }
    }
}
