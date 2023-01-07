using e_community_cloud_lib.Database.Local;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.NonEntities {
    public class MonitoringStatus {
        /// <summary>
        /// current meter data for monitoring
        /// </summary>
        public MeterDataMonitoring  MeterDataMonitoring { get; set; }

        /// <summary>
        /// current smart meter portion
        /// </summary>
        public SmartMeterPortion SmartMeterPortion { get; set; }

        /// <summary>
        /// current forecast
        /// </summary>
        public int Forecast { get; set; }
    }
}
