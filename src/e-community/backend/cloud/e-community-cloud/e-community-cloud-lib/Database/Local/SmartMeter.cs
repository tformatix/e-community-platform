using e_community_cloud_lib.Database.General;
using e_community_cloud_lib.Database.PriceRate;
using e_community_cloud_lib.Util.BaseClasses;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;

namespace e_community_cloud_lib.Database.Local
{
    /// <summary>
    /// representation of the local (raspberry) device
    /// <see cref="EntityBase"/>
    /// </summary>
    public class SmartMeter : EntityBase
    {
        /// <summary>
        /// name of the device
        /// </summary>
        public string Name { get; set; }

        /// <summary>
        /// short description of the device
        /// </summary>
        public string Description { get; set; }

        /// <summary>
        /// are non compliance messages muted
        /// </summary>
        public bool IsNonComplianceMuted { get; set; }

        /// <summary>
        /// marks the main raspberry (only one weather calculation per household)
        /// </summary>
        public bool IsMain { get; set; }

        /// <summary>
        /// device measures energy consumption
        /// </summary>
        public bool MeasuresConsumption { get; set; }

        /// <summary>
        /// device measures energy feed in
        /// </summary>
        public bool MeasuresFeedIn { get; set; }

        /// <summary>
        /// everything of the energy feed in goes directly back to the power grid
        /// </summary>
        public bool IsDirectFeedIn { get; set; }

        /// <summary>
        /// just the overflow of the energy feed in goes back to the power grid
        /// </summary>
        public bool IsOverflowFeedIn { get; set; }

        /// <summary>
        /// each change of data relevant for local devices (Raspberry) gets its own id
        /// </summary>
        public Guid LocalStorageId { get; set; }

        /// <summary>
        /// member which belongs this smart meter
        /// </summary>
        [Required]
        public Guid MemberId { get; set; }
        public Member Member { get; set; }

        /// <summary>
        /// supplier price rate
        /// <seealso cref="SupplierPriceRate"/>
        /// </summary>
        public Guid? SupplierPriceRateId { get; set; }
        public SupplierPriceRate SupplierPriceRate { get; set; }

        /// <summary>
        /// grid price rate
        /// <seealso cref="GridPriceRate"/>
        /// </summary>
        public Guid? GridPriceRateId { get; set; }
        public GridPriceRate GridPriceRate { get; set; }

        /// <summary>
        /// list of pv systems
        /// </summary>
        public IList<PVSystem> PVSystems { get; set; }

        /// <summary>
        /// list of battery systems
        /// </summary>
        public IList<BatterySystem> BatterySystems { get; set; }

        /// <summary>
        /// list of meter data
        /// </summary>
        public IList<MeterDataProfile> MeterDataProfiles { get; set; }

        /// <summary>
        /// list of estimated energy portions for smart meter
        /// </summary>
        public IList<SmartMeterPortion> SmartMeterPortions { get; set; }

        public IList<MeterDataMonitoring> MeterDataMonitorings{ get; set; }
    }
}
