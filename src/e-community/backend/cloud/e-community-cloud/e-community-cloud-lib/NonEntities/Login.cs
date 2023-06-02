using System;
using System.Collections.Generic;
using System.Text;

namespace e_community_cloud_lib.NonEntities
{
    public class Login
    {
        public Guid MemberId { get; set; }
        public string AccessToken { get; set; }
        public string RefreshToken { get; set; }
    }
}
