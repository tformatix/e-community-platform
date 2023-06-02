using e_community_cloud_lib.Database.Community;
using e_community_cloud_lib.Database.General;
using e_community_cloud_lib.Util.Enums;
using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Security.Claims;
using System.Text;

namespace e_community_cloud_lib.Util.Extensions
{
    public static class MemberExtensions
    {
        /// <param name="_principal">Member</param>
        /// <returns>member id based on token</returns>
        public static Guid? GetMemberId(this ClaimsPrincipal _principal)
        {
            var success = Guid.TryParse(_principal.Claims.FirstOrDefault(c => c.Type == JwtRegisteredClaimNames.Sub || c.Type == ClaimTypes.NameIdentifier)?.Value, out var res);
            if (!success)
            {
                return null;
            }
            return res;
        }
    }
}
