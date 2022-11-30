using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_local_lib.Util
{
    public class Constants
    {
        public const string CONNECTIVITY_TEST_URL = "http://google.com/generate_204";
        
        public const string PYTHON_EXE = @"/usr/bin/python3";

        public const string CONNECT_NETWORK_COMMAND = "python3 /home/michael/Documents/dev/FH/4_Semester/DAB/Solr/SliderR/app.py start --stop-all";

        public const string ADD_NETWORK_COMMAND = "Util/Extensions/Python/networking.py add -s \"test\" -p \"test\"";

        public const string NETWORK_DISCOVERY = "Util/Extensions/Python/networking.py discover-networks";

        public const string IS_WIRED_CONNECTED = "Util/Extensions/Python/networking.py is-wired-connected";

        public const string GET_WIFI_SSID = "Util/Extensions/Python/networking.py get-wifi-ssid";
        
        public const string STATUS_ETH0_UP = "up";

        public const int RT_EXTEND_MILLISECONDS = 360000; // 6 minutes
    }
}
