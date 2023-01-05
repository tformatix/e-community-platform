using e_community_cloud_lib.Database.Local;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.NonEntities {
    public class MonitoringStatus {
        public MeterDataMonitoring  MeterDataMonitoring { get; set; }
        public SmartMeterPortion SmartMeterPortion { get; set; }
        public int Forecast { get; set; }
    }
}
