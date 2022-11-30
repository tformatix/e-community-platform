using e_community_local_lib.Database.General;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_local_lib.NonEntities
{
    public class Status
    {
        public Guid? MemberId { get; set; }
        public bool IsConnectedToInternet { get; set; }
        public bool IsWiredConnected { get; set; }
        public string WifiSSID { get; set; }
        public SmartMeter SmartMeter { get; set; }
    }
}
