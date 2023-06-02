using e_community_cloud_lib.Endpoints.Dtos;
using System;
using System.Collections.Generic;
using System.Timers;

namespace e_community_cloud_lib.NonEntities
{
    /// <summary>
    /// buffer of real time meter data
    /// </summary>
    public class RTMeterDataBuffer
    {
        /// <summary>
        /// current date and time
        /// </summary>
        public DateTime Timestamp { get; set; }

        /// <summary>
        /// timer, which wait x seconds until the data is sent (even if not all are present)
        /// </summary>
        public Timer Timer { get; set; }

        /// <summary>
        /// measurements at timestamp
        /// </summary>
        public List<MeterDataRTDto> MeterData { get; set; }
    }
}
