using e_community_local.Dtos;
using e_community_local_lib.BusinessLogic.Interfaces;
using e_community_local_lib.Database;
using e_community_local_lib.Database.Meter;
using e_community_local_lib.Models;
using e_community_local_lib.Util.Extensions;
using Microsoft.AspNetCore.Mvc;
using Serilog;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace e_community_local.Controllers
{
    [ApiController]
    [Route("[controller]/[action]")]
    public class PairingController : ControllerBase
    {
        private readonly IPairingService mPairingService;

        public PairingController(IPairingService _pairingService)
        {
            mPairingService = _pairingService;
        }

        [ProducesResponseType(typeof(StatusDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [HttpGet]
        public async Task<IActionResult> Status()
        {
            Log.Information("Pairing/Status");
            var status = await mPairingService.CurrentStatus();
            var statusDto = status.CopyPropertiesTo(new StatusDto(), new string[] { "SmartMeter" });
            statusDto.SmartMeter = status.SmartMeter?.CopyPropertiesTo(new SmartMeterDto());
            return Ok(statusDto);
        }

        [ProducesResponseType(typeof(OkDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [HttpPost]
        public async Task<IActionResult> NetworkAdd([FromBody] NetworkConnectModel _networkConnectModel)
        {
            Log.Information($"Pairing/NetworkAdd::{_networkConnectModel.SSID}");
            await mPairingService.NetworkAdd(_networkConnectModel);
            return Ok(new OkDto());
        }

        [ProducesResponseType(typeof(List<NetworkDiscoveryDto>), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [HttpGet]
        public IActionResult NetworkDiscovery()
        {
            Log.Information("Pairing/NetworkDiscovery");
            var networks = mPairingService.NetworkDiscovery();
            return Ok(networks.Select(x => x.CopyPropertiesTo(new NetworkDiscoveryDto())));
        }

        [ProducesResponseType(typeof(OkDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [HttpPost]
        public async Task<IActionResult> CloudConnect([FromBody] CloudConnectModel _cloudConnectModel)
        {
            Log.Information("Pairing/CloudConnect");
            await mPairingService.CloudConnect(_cloudConnectModel);
            return Ok(new OkDto());
        }
    }
}
