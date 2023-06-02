using e_community_cloud.Dtos;
using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.BusinessLogic.Interfaces.SignalR;
using e_community_cloud_lib.Models.SmartMeter;
using e_community_cloud_lib.Util.Extensions;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Serilog;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace e_community_cloud.Controllers {
    [ApiController]
    [Route("[controller]/[action]")]
    public class SmartMeterController : ControllerBase
    {
        private readonly ISmartMeterService mSmartMeterService;
        private readonly ILocalSignalRSenderService mLocalSignalRSenderService;

        public SmartMeterController(ISmartMeterService _smartMeterService, ILocalSignalRSenderService _localSignalRSenderService)
        {
            mSmartMeterService = _smartMeterService;
            mLocalSignalRSenderService = _localSignalRSenderService;
        }

        [ProducesResponseType(typeof(List<MinimalSmartMeterDto>), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [Authorize]
        [HttpGet]
        public async Task<IActionResult> GetMinimalSmartMeters() {
            var memberId = (Guid)User.GetMemberId();
            Log.Information($"SmartMeter/GetMinimalSmartMeters::{memberId}");

            var smartMeters = await mSmartMeterService.GetSmartMeters(memberId);

            return Ok(smartMeters
                .Select(x => x.CopyPropertiesTo(new MinimalSmartMeterDto()))
                .ToList());
        }

        [ProducesResponseType(typeof(List<SmartMeterDto>), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [Authorize]
        [HttpGet]
        public async Task<IActionResult> GetSmartMeters() {
            var memberId = (Guid)User.GetMemberId();
            Log.Information($"SmartMeter/GetSmartMeters::{memberId}");

            var smartMeters = await mSmartMeterService.GetSmartMeters(memberId);

            return Ok(smartMeters
                .Select(x => x.CopyPropertiesTo(new MinimalSmartMeterDto()))
                .ToList());
        }

        [HttpPut]
        [ProducesResponseType(typeof(OkDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [Authorize]
        public async Task<IActionResult> Update([FromBody] UpdateSmartMeterModel _updateSmartMeterModel)
        {
            var memberId = User.GetMemberId();
            Log.Information($"SmartMeter/Update::{_updateSmartMeterModel.Id}");
            await mSmartMeterService.Update(_updateSmartMeterModel);
            return Ok(new OkDto());
        }

        [HttpGet]
        [ProducesResponseType(typeof(OkDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [Authorize]
        public IActionResult RequestRTData()
        {
            var memberId = User.GetMemberId();
            Log.Information($"SmartMeter/RequestRTData::{memberId}");
            mLocalSignalRSenderService.RequestRTData(memberId);
            return Ok(new OkDto());
        }

        [HttpGet]
        [ProducesResponseType(typeof(OkDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [Authorize]
        public IActionResult ExtendRTData() {
            var memberId = User.GetMemberId();
            Log.Information($"SmartMeter/ContinueRTData::{memberId}");
            mLocalSignalRSenderService.ExtendRTData(memberId);
            return Ok(new OkDto());
        }

        [HttpGet]
        [ProducesResponseType(typeof(OkDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [Authorize]
        public IActionResult StopRTData()
        {
            var memberId = User.GetMemberId();
            Log.Information($"SmartMeter/StopRTData::{memberId}");
            mLocalSignalRSenderService.StopRTData(memberId);
            return Ok(new OkDto());
        }
    }
}
