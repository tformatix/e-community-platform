using System;
using System.Threading.Tasks;
using e_community_cloud.Dtos;
using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.Util.Extensions;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Serilog;

namespace e_community_cloud.Controllers {

    [ApiController]
    [Route("[controller]/[action]")]
    public class FCMController : ControllerBase {
        private readonly IFCMService mFCMService;

        public FCMController(IFCMService _fcmService) {
            mFCMService = _fcmService;
        }

        [ProducesResponseType(typeof(OkDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [Authorize]
        [HttpPost]
        public async Task<IActionResult> RegisterFCMToken([FromBody] string _fcmToken) {
            var memberId = (Guid)User.GetMemberId();
            Log.Information($"Notification/RegisterFCMToken::{memberId}");

            await mFCMService.RegisterFCMToken(memberId, _fcmToken);
            return Ok(new OkDto());
        }
    }
}
