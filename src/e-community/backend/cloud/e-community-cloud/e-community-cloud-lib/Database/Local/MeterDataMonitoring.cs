using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.Database.Local {
    public class MeterDataMonitoring {
        public int MonitoringId { get; set; }
        public Monitoring Monitoring { get; set; }
        public Guid SmartMeterId { get; set; }
        public SmartMeter SmartMeter { get; set; }
        public int? ActiveEnergyPlus { get; set; }
        public int? ActiveEnergyMinus { get; set; }
        public bool Acknowledged { get; set; }
    }
}
