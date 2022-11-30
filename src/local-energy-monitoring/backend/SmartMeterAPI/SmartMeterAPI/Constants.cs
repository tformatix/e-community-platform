using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartMeterAPI
{
    public class Constants
    {
        public const string PREFIX_1000 = "k";
        public const string ENERGY_UNIT = "Wh";
        public const string POWER_UNIT = "W";
        public const int TIME_DIFF_HIST = 15;
        public const int DB_ERROR_CODE = 555;
        public const int AES_KEY_WRONG_YEAR = 2000;
        public const String SIGNAL_R_CLIENT_METHOD = "RealTime";
    }
}
