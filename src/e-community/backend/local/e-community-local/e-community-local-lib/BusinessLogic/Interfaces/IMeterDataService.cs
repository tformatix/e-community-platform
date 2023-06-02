using e_community_local_lib.CloudData;
using e_community_local_lib.Database.Meter;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_local_lib.BusinessLogic.Interfaces {
    /// <summary>
    /// handles meter data
    /// </summary>
    public interface IMeterDataService {
        /// <returns>meter data for monitoring</returns>
        Task<MeterDataMonitoringModel> GetMeterDataMonitoring();
    }
}
