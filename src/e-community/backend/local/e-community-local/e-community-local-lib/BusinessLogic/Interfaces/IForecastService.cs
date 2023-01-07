using e_community_local_lib.CloudData;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_local_lib.BusinessLogic.Interfaces {
    /// <summary>
    /// dummy forecast service
    /// </summary>
    public interface IForecastService {
        /// <returns>hourly forecast (dummy: hour before)</returns>
        Task<ForecastModel> GetHourlyForecast();
    }
}
