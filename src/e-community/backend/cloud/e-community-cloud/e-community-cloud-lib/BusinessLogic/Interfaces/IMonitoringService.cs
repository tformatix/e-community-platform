using e_community_cloud_lib.Database.Local;
using e_community_cloud_lib.Models.Distribution;
using e_community_cloud_lib.NonEntities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.BusinessLogic.Interfaces {
    public interface IMonitoringService {
        Task StartMonitoring(DateTime _timestamp);

        Task MeterDataMonitoringArrived(MeterDataMonitoringModel _meterDataMonitoringModel);

        Task<Performance> GetPerformance(Guid _smartMeterId, int _durationDays);

        Task<List<MeterDataMonitoring>> GetRelevantMeterDataMonitorings(Guid _memberId);

        Task MuteCurrentHour(Guid _smartMeterId);
    }
}
