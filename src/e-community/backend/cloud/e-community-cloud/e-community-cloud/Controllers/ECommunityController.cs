using e_community_cloud.Dtos;
using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.Util.Extensions;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Serilog;
using System.Threading.Tasks;
using System;
using System.Linq;

namespace e_community_cloud.Controllers {

    [ApiController]
    [Route("[controller]/[action]")]
    public class ECommunityController : ControllerBase {
        private readonly IECommunityService mECommunityService;

        public ECommunityController(IECommunityService _eCommunityService) {
            mECommunityService = _eCommunityService;
        }

        [ProducesResponseType(typeof(MinimalECommunityDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [Authorize]
        [HttpGet]
        public async Task<IActionResult> GetMinimalECommunity() {
            var memberId = (Guid)User.GetMemberId();
            Log.Information($"ECommunity/GetMinimalECommunity::{memberId}");

            var eCommunity = await mECommunityService.GetECommunity(memberId);
            if(eCommunity == null) {
                return NotFound();
            }

            return Ok(new MinimalECommunityDto() {
                Id = eCommunity.Id,
                Name = eCommunity.Name,
                Members = eCommunity.ECommunityMemberships
                    .Select(x => x.Member.CopyPropertiesTo(new MinimalMemberDto()))
                    .OrderBy(x => x.UserName)
                    .ToList()
            });
        }
    }
}
