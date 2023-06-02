using e_community_cloud_lib.Database.Local;
using e_community_cloud_lib.Models.Distribution;
using e_community_cloud_lib.NonEntities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.BusinessLogic.Interfaces {

    /// <summary>
    /// handles monitoring of offline status and non-compliances of smart meters
    /// </summary>
    public interface IMonitoringService {
        /// <summary>
        /// starts new monitoring session: prepare database for it, remove old entries, ... (every 5 minutes)
        /// </summary>
        /// <param name="_timestamp">current timestamp</param>
        Task StartMonitoring(DateTime _timestamp);

        /// <summary>
        /// meter data for monitoring arrived from local devices (smart Meter)
        /// </summary>
        /// <param name="_meterDataMonitoringModel">meter data</param>
        Task MeterDataMonitoringArrived(MeterDataMonitoringModel _meterDataMonitoringModel);

        /// <param name="_smartMeterId">id of smart meter</param>
        /// <param name="_durationDays">days to consider</param>
        /// <returns>performance of the forecasts for a specific smart meter</returns>
        Task<Performance> GetPerformance(Guid _smartMeterId, int _durationDays);

        /// <param name="_memberId">id of member</param>
        /// <returns>current monitoring status (everything fine, offline smart meters and non-compliances of forecast)</returns>
        Task<List<MonitoringStatus>> GetMonitoringStatuses(Guid _memberId);

        /// <summary>
        /// member is allowed to mute the non-compliance messages for the current hour
        /// this method toggles the database flag for it
        /// </summary>
        /// <param name="_smartMeterId">id of smart meter</param>
        Task ToggleMuteCurrentHour(Guid _smartMeterId);
    }
}
