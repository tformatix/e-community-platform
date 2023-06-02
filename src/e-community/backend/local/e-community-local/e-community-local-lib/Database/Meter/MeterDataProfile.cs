using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_local_lib.Database.Meter
{
    /// <summary>
    /// energy profile of the member (values are the calculated average)
    /// calculation will run frequently (every 1h for example)
    /// </summary>
    public class MeterDataProfile: MeterDataBase
    {
        /// <summary>
        /// working price for 1 kWh (energy consumption in €)
        /// </summary>
        public double WorkingPricePlus { get; set; }

        /// <summary>
        /// working price for 1 kWh (energy feedin in €)
        /// </summary>
        public double WorkingPriceMinus { get; set; }
    }
}
