using e_community_cloud_lib.Util.Enums;
using System;
using System.Collections.Generic;
using System.Text;

namespace e_community_cloud_lib.Util.Extensions
{
    public static class GuidExtensions
    {
        public static String GetGroupName(this Guid _id, GroupType _groupType)
        {
            return $"{_groupType}_{_id}";
        }
    }
}
