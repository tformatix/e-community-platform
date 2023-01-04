using e_community_cloud.Dtos;
using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.Models.Distribution;
using e_community_cloud_lib.Util.Extensions;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Serilog;
using System.Threading.Tasks;

namespace e_community_cloud.Controllers {
    [ApiController]
    [Route("[controller]/[action]")]
    public class MonitoringController :ControllerBase {
        private readonly IMonitoringService mMonitoringService;

        public MonitoringController(IMonitoringService _monitoringService) {
            mMonitoringService = _monitoringService;
        }

        [ProducesResponseType(typeof(OkDto), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ErrorDto), StatusCodes.Status400BadRequest)]
        [Authorize]
        [HttpPost]
        public async Task<IActionResult> MeterDataMonitoring([FromBody] MeterDataMonitoringModel _meterDataMonitoringModel) {
            var memberId = User.GetMemberId();
            Log.Information($"Monitoring/MeterDataMonitoring::{memberId}");

            // meter data for monitoring arrived
            await mMonitoringService.MeterDataMonitoringArrived(_meterDataMonitoringModel);

            return Ok(new OkDto());
        }
    }
}
