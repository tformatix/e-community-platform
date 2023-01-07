using System;

namespace e_community_cloud.Dtos {
    public class MonitoringStatusDto {
        public Guid SmartMeterId { get; set; }

        /// <summary>
        /// name of smart meter
        /// </summary>
        public string SmartMeterName { get; set; }

        /// <summary>
        /// are non-compliance messages muted for the next hour (smart meter dependant)
        /// </summary>
        public bool IsNonComplianceMuted { get; set; }

        /// <summary>
        /// current forecast
        /// </summary>
        public int? Forecast { get; set; }

        /// <summary>
        /// projected energy A+ (consumption) for the next hour
        /// </summary>
        public int? ProjectedActiveEnergyPlus { get; set; }
    }
}
