
using e_community_local_lib.CloudData.Local;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_local_lib.Models
{
    public class CloudConnectModel
    {
        public string RefreshToken { get; set; }
        public CloudSmartMeterDto SmartMeter { get; set; }
    }
}
