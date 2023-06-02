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

        public const int DISTRIBUTION_MINIMUM_ENERGY_WH = 100; // feed in from which the user is informed

        public const double DISTRIBUTION_GOOD_FORECAST_PERCENT = 0.1; // +/- 10% deviation from forecast -> good

        public static readonly ECommunityPermission[] ACTIVE_MEMBER_PERMISSIONS = new ECommunityPermission[] { ECommunityPermission.Admin, ECommunityPermission.Member };

        public const int KILOWATT = 1000;
        public const int MEGAWATT = 1000000;
        public const int GIGAWATT = 1000000000;

        public const string WATT_UNIT = "W";
        public const string KILOWATT_UNIT = "kW";
        public const string MEGAWATT_UNIT = "MW";
        public const string GIGAWATT_UNIT = "GW";
        public const string HOUR_UNIT = "h";
    }
}
