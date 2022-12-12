using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using e_community_cloud.Dtos;
using e_community_cloud_lib.BusinessLogic.Implementations;
using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.Models;
using e_community_cloud_lib.Util.Extensions;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Serilog;

namespace e_community_cloud.Controllers
{
    [ApiController]
    [Route("[controller]/[action]")]
    public class NotificationController : ControllerBase 
    {
        private readonly INotificationService mNotificationService;

        public NotificationController(INotificationService _notificationService)
        {
            mNotificationService = _notificationService;
        }

        [ProducesResponseType(typeof(OkDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [Authorize]
        [HttpPost]
        public async Task<IActionResult> RegisterFCMTokenAsync([FromBody] string _fcmToken) {
            var memberId = (Guid)User.GetMemberId();
            Log.Information($"Notification/RegisterFCMToken::{memberId}");

            await mNotificationService.RegisterFCMToken(memberId, _fcmToken);
            return Ok(new OkDto());
        }
    }
}
