using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.Database.Local {
    public class Monitoring {
        public int Id { get; set; }
        public DateTime Timestamp { get; set; }
        public bool IsCurrent { get; set; }
        public IList<MeterDataMonitoring> MeterDataMonitorings { get; set; }
    }
}
