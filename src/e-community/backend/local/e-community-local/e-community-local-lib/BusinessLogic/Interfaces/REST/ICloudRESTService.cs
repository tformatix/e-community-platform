using e_community_local_lib.CloudData;
using e_community_local_lib.CloudData.Local;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_local_lib.BusinessLogic.Interfaces.REST
{
    public interface ICloudRESTService
    {
        /// <summary>
        /// refreshes tokens in database
        /// </summary>
        /// <param name="_refreshToken">refresh token</param>
        /// <returns>login DTO from cloud</returns>
        Task<LoginDto> Refresh(string _refreshToken);

        /// <summary>
        /// refreshes tokens in database
        /// reads refresh token from database
        /// </summary>
        /// <param name="_refreshToken">refresh token</param>
        /// <returns>login DTO from cloud</returns>
        Task<LoginDto> RefreshFromDb();

        /// <param name="_smartMeterId">id of smart meter</param>
        /// <param name="accessToken">access token</param>
        /// <returns>all necessary data for local usage from cloud</returns>
        Task<CloudLocalDto> GetLocalDataPairing(Guid _smartMeterId, string _refreshToken);

        /// <summary>
        /// sends hourly forecast to cloud
        /// </summary>
        /// <param name="_forecastModel">load profile and flexibility</param>
        Task SendHourlyForecast(ForecastModel _forecastModel);

        /// <summary>
        /// sends meter data for monitoring
        /// <param name="_meterDataMonitoringModel">meter data</param>
        /// </summary>
        Task SendMeterDataMonitoring(MeterDataMonitoringModel _meterDataMonitoringModel);
    }
}
