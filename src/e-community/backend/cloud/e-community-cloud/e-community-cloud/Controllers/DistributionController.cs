using e_community_cloud.Dtos;
using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.Models.Distribution;
using e_community_cloud_lib.Util.Extensions;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Serilog;
using System;
using System.Linq;
using System.Threading.Tasks;

namespace e_community_cloud.Controllers {
    [ApiController]
    [Route("[controller]/[action]")]
    public class DistributionController : ControllerBase {

        private readonly IDistributionService mDistributionService;
        private readonly IAuthService mAuthService;

        public DistributionController(IDistributionService _distributionService, IAuthService _authService) {
            mDistributionService = _distributionService;
            mAuthService = _authService;
        }

        [ProducesResponseType(typeof(OkDto), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ErrorDto), StatusCodes.Status400BadRequest)]
        [Authorize]
        [HttpPost]
        public async Task<IActionResult> HourlyForecast([FromBody] ForecastModel _forecastModel) { 
            var memberId = (Guid)User.GetMemberId();
            Log.Information($"Distribution/HourlyForecast({memberId})");

            // forecast arrived
            await mAuthService.EnsureSmartMeter(memberId, _forecastModel.SmartMeterId);
            await mDistributionService.ForecastArrived(_forecastModel);

            return Ok(new OkDto());
        }

        [ProducesResponseType(typeof(CurrentPortionDto), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ErrorDto), StatusCodes.Status400BadRequest)]
        [Authorize]
        [HttpGet]
        public async Task<IActionResult> CurrentPortion(Guid _smartMeterId) {
            var memberId = (Guid)User.GetMemberId();
            Log.Information($"Distribution/CurrentPortion/{memberId}/{_smartMeterId}");
            await mAuthService.EnsureSmartMeter(memberId, _smartMeterId);

            var currentPortion = await mDistributionService.GetCurrentPortion(_smartMeterId);
            if(currentPortion != null) {
                return Ok(currentPortion.CopyPropertiesTo(new CurrentPortionDto()));
            }
            
            return NotFound();
        }

        [ProducesResponseType(typeof(NewDistributionDto), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ErrorDto), StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [Authorize]
        [HttpGet]
        public async Task<IActionResult> NewDistribution() {
            var memberId = (Guid)User.GetMemberId();
            Log.Information($"Distribution/NewPortions/{memberId}");

            var newDistribution = await mDistributionService.GetNewDistribution(memberId);
            if (newDistribution != null) {
                return Ok(new NewDistributionDto() {
                    MissingSmartMeterCount = newDistribution.MissingSmartMeterCount,
                    UnassignedActiveEnergyMinus = newDistribution.UnassignedActiveEnergyMinus,
                    NewPortions = newDistribution.SmartMeterPortions
                        .Select(x => x.CopyPropertiesTo(new NewPortionDto() {
                            SmartMeterName = x.SmartMeter.Name
                        }))
                        .ToList()
                });
            }
            return NotFound();
        }

        [ProducesResponseType(typeof(OkDto), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ErrorDto), StatusCodes.Status400BadRequest)]
        [Authorize]
        [HttpPost]
        public async Task<IActionResult> HourlyPortionAck([FromBody] PortionAckModel _portionAckModel) {
            var memberId = (Guid)User.GetMemberId();
            Log.Information($"Distribution/HourlyPortionAck({memberId})");

            // member acknowledged portion (change or accept)
            await mAuthService.EnsureSmartMeter(memberId, _portionAckModel.SmartMeterId);
            await mDistributionService.PortionAck(_portionAckModel);

            return Ok(new OkDto());
        }
    }
}
