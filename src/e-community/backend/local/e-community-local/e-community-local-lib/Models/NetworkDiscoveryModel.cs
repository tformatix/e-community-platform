using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_local_lib.Models
{
    [Serializable]
    public class NetworkDiscoveryModel
    {
        public string SSID { get; set; }

        public string Quality { get; set; }
    }
}
