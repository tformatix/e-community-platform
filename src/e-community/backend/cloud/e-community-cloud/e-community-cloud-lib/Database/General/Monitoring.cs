using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using e_community_cloud_lib.Database.Local;

namespace e_community_cloud_lib.Database.General
{
    /// <summary>
    /// monitoring at a specific timestamp
    /// </summary>
    public class Monitoring
    {
        public int Id { get; set; }

        /// <summary>
        /// date and time of the distribution
        /// </summary>
        public DateTime Timestamp { get; set; }

        /// <summary>
        /// is this monitoring currently calculated
        /// </summary>
        public bool IsCalculating { get; set; }

        /// <summary>
        /// list of meter data
        /// </summary>
        public IList<MeterDataMonitoring> MeterDataMonitorings { get; set; }
    }
}
