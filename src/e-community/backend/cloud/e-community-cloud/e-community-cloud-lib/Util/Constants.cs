using e_community_cloud_lib.Util.Enums;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.Util
{
    public class Constants
    {
        public const string DEFAULT_LANGUAGE = "en";

        public const int AUTO_SEND_RT_DATA_MILLISECONDS = 5000; // 5 seconds

        public const int DISTRIBUTION_MONITOR_INTERVAL_MINUTES = 5;

        public const int DISTRIBUTION_MINIMUM_ENERGY_WH = 100;

        public static readonly ECommunityPermission[] ACTIVE_MEMBER_PERMISSIONS = new ECommunityPermission[] { ECommunityPermission.Admin, ECommunityPermission.Member };
    }
}
