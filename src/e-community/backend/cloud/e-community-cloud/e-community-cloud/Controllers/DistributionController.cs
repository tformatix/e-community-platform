using e_community_cloud.Dtos;
using e_community_cloud_lib.Models.Distribution;
using e_community_cloud_lib.Util.Extensions;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Serilog;
using System.Threading.Tasks;

namespace e_community_cloud.Controllers
{
    [ApiController]
    [Route("[controller]/[action]")]
    public class DistributionController : ControllerBase {
        [ProducesResponseType(typeof(OkDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [Authorize]
        [HttpPost]
        public async Task<IActionResult> HourlyForecast([FromBody] ForecastModel _forecastModel) {
            var memberId = User.GetMemberId();
            Log.Information($"Distribution/HourlyForecast::{memberId}");

            // TODO (2)

            return Ok(new OkDto());
        }

        [ProducesResponseType(typeof(OkDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [Authorize]
        [HttpPost]
        public async Task<IActionResult> HourlyPortionAck([FromBody] PortionAckModel _portionAckModel) {
            var memberId = User.GetMemberId();
            Log.Information($"Distribution/MeterDataMonitoring::{memberId}");

            // TODO (4)/(5)/(6)

            return Ok(new OkDto());
        }

        [ProducesResponseType(typeof(OkDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [Authorize]
        [HttpPost]
        public async Task<IActionResult> MeterDataMonitoring([FromBody] MeterDataMonitoringModel _meterDataMonitoringModel) {
            var memberId = User.GetMemberId();
            Log.Information($"Distribution/MeterDataMonitoring::{memberId}");

            // TODO: Monitoring (2)

            return Ok(new OkDto());
        }
    }
}
