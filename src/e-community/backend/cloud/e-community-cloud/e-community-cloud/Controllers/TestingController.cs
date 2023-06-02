using e_community_cloud.Dtos;
using e_community_cloud_lib.BusinessLogic.Implementations;
using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.Database.Local;
using e_community_cloud_lib.Models.Distribution;
using e_community_cloud_lib.NonEntities;
using e_community_cloud_lib.Util.Extensions;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Serilog;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace e_community_cloud.Controllers {
    [ApiController]
    [Route("[controller]/[action]")]
    public class TestingController : ControllerBase {
        private readonly IDistributionService mDistributionService;
        private readonly IMonitoringService mMonitoringService;
        private readonly IFCMService mFCMService;

        public TestingController(IDistributionService _distributionService, IMonitoringService _monitoringService, IFCMService _fcmService) { 
            mDistributionService = _distributionService;
            mMonitoringService = _monitoringService;
            mFCMService = _fcmService;
        }

        [ProducesResponseType(typeof(OkDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [Authorize]
        [HttpPost]
        public async Task<IActionResult> StartDistribution() {
            Log.Information($"Testing/StartDistribution");
            await mDistributionService.StartDistribution(DateTime.UtcNow);
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

        [ProducesResponseType(typeof(OkDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [Authorize]
        [HttpPost]
        public async Task<IActionResult> StartMonitoring(bool IsFiveAfter) {
            Log.Information($"Testing/StartMonitoring");
            var timestamp = DateTime.UtcNow;
            if (IsFiveAfter) {
                timestamp = timestamp
                    .AddMinutes(-timestamp.Minute)
                    .AddMinutes(5);
            }
            await mMonitoringService.StartMonitoring(timestamp);
            return Ok(new OkDto());
        }

        [ProducesResponseType(typeof(OkDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [Authorize]
        [HttpPost]
        public async Task<IActionResult> SendSampleFCMNotification() {
            var memberId = (Guid)User.GetMemberId();
            Log.Information($"Testing/SendSampleFCMNotification/{memberId}");

            var notification = mFCMService.Offline;
            notification.BodyArgs = new List<string>() { "Testing" };

            await mFCMService.SendPushNotificationMember(notification, memberId);
            return Ok(new OkDto());
        }
    }
}
