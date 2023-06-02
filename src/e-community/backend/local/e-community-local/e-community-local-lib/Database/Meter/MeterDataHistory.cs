using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text;

namespace e_community_local_lib.Database.Meter
{
    /// <summary>
    /// history data of meter data (storing weather information per entry too)
    /// <see cref="MeterDataBase"/>
    /// </summary>
    public class MeterDataHistory : MeterDataBase
    {
        /// <summary>
        /// cloudiness (in %)
        /// </summary>
        public double? Cloudiness { get; set; }

        /// <summary>
        /// temperature (in °C)
        /// </summary>
        public double? Temperature { get; set; }

        /// <summary>
        /// rain volume for the last 1 hour (in mm)
        /// </summary>
        public double? RainVolume { get; set; }

        /// <summary>
        /// snow volume for the last 1 hour (in mm)
        /// </summary>
        public double? SnowVolume { get; set; }

        /// <summary>
        /// visibility (in m)
        /// the maximum value of the visibility is 10km
        /// </summary>
        public double? Visability { get; set; }

        /// <summary>
        /// working price for 1 kWh (energy consumption in €)
        /// </summary>
        public double? WorkingPricePlus { get; set; }

        /// <summary>
        /// working price for 1 kWh (energy feedin in €)
        /// </summary>
        public double? WorkingPriceMinus { get; set; }

        /// <summary>
        /// correspondending event
        /// <seealso cref="EventCase"/>
        /// </summary>
        public Guid? EventCaseId { get; set; }
        public EventCase EventCase { get; set; }

        public TResult ParseMeterData<TResult>()
        {
            throw new NotImplementedException();
        }
    }
}
