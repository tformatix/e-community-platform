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
        
        // PAIRING Constants

        public const string ADD_NETWORK_COMMAND = "Util/Extensions/Python/networking.py add -s \"test\" -p \"test\"";

        public const string NETWORK_DISCOVERY = "Util/Extensions/Python/networking.py discover-networks";

        public const string IS_WIRED_CONNECTED = "Util/Extensions/Python/networking.py is-wired-connected";

        public const string GET_WIFI_SSID = "Util/Extensions/Python/networking.py get-wifi-ssid";
        
        public const string STATUS_ETH0_UP = "up";
        
        // BLOCKCHAIN Constants

        public const string GET_ACCOUNT_BALANCE = "Util/Extensions/Python/goquorum.py account-balance";

        public const string CREATE_CONSENT_CONTRACT = "Util/Extensions/Python/goquorum.py create-consent-contract ";
        
        public const string DEPOSIT_TO_CONTRACT = "Util/Extensions/Python/goquorum.py deposit-to-contract ";
        
        public const string WITHDRAW_FROM_CONTRACT = "Util/Extensions/Python/goquorum.py withdraw-from-contract ";

        public const string CONTRACTS_FOR_MEMBER = "Util/Extensions/Python/goquorum.py get-contracts-for-member ";

        public const string ACCEPT_CONTRACT = "Util/Extensions/Python/goquorum.py accept-contract ";
        
        public const string REJECT_CONTRACT = "Util/Extensions/Python/goquorum.py reject-contract ";
        
        public const string REVOKE_CONTRACT = "Util/Extensions/Python/goquorum.py revoke-contract ";

        public const int RT_EXTEND_MILLISECONDS = 360000; // 6 minutes

        public const int MAX_MONITORING_RT_ATTEMPTS_SECONDS = 10;
        
        // History
        
        public const int TIME_DIFF_HIST = 15;
    }
}
