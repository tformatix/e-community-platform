using e_community_cloud.Dtos;
using e_community_cloud_lib.BusinessLogic.Implementations;
using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.Models.Distribution;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Serilog;
using System.Threading.Tasks;

namespace e_community_cloud.Controllers {
    [ApiController]
    [Route("[controller]/[action]")]
    public class TestingController : ControllerBase {
        private readonly IDistributionService mDistributionService;

        public TestingController(IDistributionService _distributionService) {
            mDistributionService = _distributionService;
        }

        [ProducesResponseType(typeof(OkDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [Authorize]
        [HttpPost]
        public async Task<IActionResult> StartDistribution() {
            Log.Information($"Testing/StartDistribution");
            await mDistributionService.StartDistribution();
            return Ok(new OkDto());
        }

        [ProducesResponseType(typeof(OkDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [Authorize]
        [HttpPost]
        public async Task<IActionResult> Distribute() {
            Log.Information($"Testing/StartDistribution");
            await mDistributionService.Distribute();
            return Ok(new OkDto());
        }

        [ProducesResponseType(typeof(OkDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [Authorize]
        [HttpPost]
        public async Task<IActionResult> FinalizeDistribution() {
            Log.Information($"Testing/StartDistribution");
            await mDistributionService.FinalizeDistribution();
            return Ok(new OkDto());
        }
    }
}
