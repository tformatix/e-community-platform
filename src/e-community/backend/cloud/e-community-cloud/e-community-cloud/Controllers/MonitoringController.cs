using e_community_cloud.Dtos;
using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.Models.Distribution;
using e_community_cloud_lib.Util.Extensions;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Serilog;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace e_community_cloud.Controllers {

    [ApiController]
    [Route("[controller]/[action]")]
    public class MonitoringController : ControllerBase {
        private readonly IMonitoringService mMonitoringService;
        private readonly IAuthService mAuthService;

        public MonitoringController(IMonitoringService _monitoringService, IAuthService _authService) {
            mMonitoringService = _monitoringService;
            mAuthService = _authService;
        }

        [ProducesResponseType(typeof(OkDto), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ErrorDto), StatusCodes.Status400BadRequest)]
        [Authorize]
        [HttpPost]
        public async Task<IActionResult> MeterDataMonitoring([FromBody] MeterDataMonitoringModel _meterDataMonitoringModel) {
            var memberId = (Guid)User.GetMemberId();
            Log.Information($"Monitoring/MeterDataMonitoring::{memberId}");

            // meter data for monitoring arrived
            await mAuthService.EnsureSmartMeter(memberId, _meterDataMonitoringModel.SmartMeterId);
            await mMonitoringService.MeterDataMonitoringArrived(_meterDataMonitoringModel);

            return Ok(new OkDto());
        }

        [ProducesResponseType(typeof(PerformanceDto), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ErrorDto), StatusCodes.Status400BadRequest)]
        [Authorize]
        [HttpGet]
        public async Task<IActionResult> Performance(Guid _smartMeterId, int _durationDays) {
            var memberId = (Guid)User.GetMemberId();
            Log.Information($"Monitoring/Performance/{memberId}/{_smartMeterId}/{_durationDays}");

            await mAuthService.EnsureSmartMeter(memberId, _smartMeterId);
            var performance = await mMonitoringService.GetPerformance(_smartMeterId, _durationDays);

            if (performance == null) {
                return NotFound();
            }

            return Ok(performance.CopyPropertiesTo(new PerformanceDto()));
        }

        [ProducesResponseType(typeof(List<MonitoringStatusDto>), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ErrorDto), StatusCodes.Status400BadRequest)]
        [Authorize]
        [HttpGet]
        public async Task<IActionResult> MonitoringStatus() {
            var memberId = (Guid)User.GetMemberId();
            Log.Information($"Monitoring/MonitoringStatus/{memberId}");

            var monitoringStatuses = await mMonitoringService.GetMonitoringStatuses(memberId);

            if(monitoringStatuses == null) {
                return NotFound();
            }

            return Ok(
                monitoringStatuses
                    .Select(x => new MonitoringStatusDto() {
                        SmartMeterId = x.SmartMeterPortion.SmartMeterId,
                        SmartMeterName = x.SmartMeterPortion.SmartMeter.Name,
                        Forecast = x.Forecast,
                        IsNonComplianceMuted = x.SmartMeterPortion.SmartMeter.IsNonComplianceMuted,
                        ProjectedActiveEnergyPlus = x.MeterDataMonitoring.ProjectedActiveEnergyPlus
                    })
                    .ToList()
            );
        }

        [ProducesResponseType(typeof(OkDto), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ErrorDto), StatusCodes.Status400BadRequest)]
        [Authorize]
        [HttpGet]
        public async Task<IActionResult> ToggleMuteCurrentHour(Guid _smartMeterId) {
            var memberId = (Guid)User.GetMemberId();
            Log.Information($"Monitoring/ToggleMuteCurrentHour/{_smartMeterId}");

            // meter data for monitoring arrived
            await mAuthService.EnsureSmartMeter(memberId, _smartMeterId);
            await mMonitoringService.ToggleMuteCurrentHour(_smartMeterId);

            return Ok(new OkDto());
        }
    }
}
