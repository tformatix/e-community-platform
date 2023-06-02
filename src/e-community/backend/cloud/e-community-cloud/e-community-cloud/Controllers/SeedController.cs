using e_community_cloud.Dtos;
using e_community_cloud_lib.BusinessLogic.Interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace e_community_cloud.Controllers
{
    [ApiController]
    [Route("[controller]/[action]")]
    public class SeedController : ControllerBase
    {
        private readonly ISeedService mSeedService;

        public SeedController(ISeedService _seedService)
        {
            mSeedService = _seedService;
        }

        [HttpGet]
        [ProducesResponseType(typeof(OkDto), 200)]
        [Authorize]
        public IActionResult DeleteGeneralTables()
        {
            mSeedService.RemoveGeneralTables();
            return Ok(new OkDto());
        }

        [HttpGet]
        [ProducesResponseType(typeof(OkDto), 200)]
        [Authorize]
        public IActionResult AddGeneralTables()
        {
            mSeedService.AddGeneralTables();
            return Ok(new OkDto());
        }
    }
}
