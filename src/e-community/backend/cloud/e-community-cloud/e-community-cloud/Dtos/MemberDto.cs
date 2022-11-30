using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace e_community_cloud.Dtos
{
    public class MemberDto
    {
        public Guid Id { get; set; }
        public String UserName { get; set; }
        public String Email { get; set; }
    }
}
