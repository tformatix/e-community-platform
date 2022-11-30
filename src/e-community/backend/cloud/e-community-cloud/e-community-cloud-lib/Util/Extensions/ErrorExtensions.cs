using Microsoft.AspNetCore.Identity;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace e_community_cloud_lib.Util.Extensions
{
    public static class ErrorExtensions
    {
        public static string GetErrorList(this IEnumerable<IdentityError> _errors)
        {
            return string.Join(", ", _errors.Select(x => x.Description));
        }
    }
}
