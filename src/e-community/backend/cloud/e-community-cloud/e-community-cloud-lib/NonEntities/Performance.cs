using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.NonEntities {
    public class Performance {
        /// <summary>
        /// number of relevant forecasts
        /// </summary>
        public int ForecastCount { get; set; }

        /// <summary>
        /// how many forecasts were good (in range)
        /// </summary>
        public int GoodForecastCount { get; set; }

        /// <summary>
        /// how much energy was wrongly forecasted
        /// </summary>
        public int WrongForecasted { get; set; }
    }
}
