using System;
using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.BusinessLogic.Interfaces.SignalR;
using e_community_cloud_lib.Models.History;
using e_community_cloud_lib.Util.Extensions;
using e_community_cloud.Dtos;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Serilog;

namespace e_community_cloud.Controllers;

[ApiController]
[Route("[controller]/[action]")]
public class HistoryController : ControllerBase
{
    private readonly ILocalSignalRSenderService mLocalSignalRSenderService;

    public HistoryController(ILocalSignalRSenderService _localSignalRSenderService)
    {
        mLocalSignalRSenderService = _localSignalRSenderService;
    }

    [ProducesResponseType(typeof(OkDto), 200)]
    [ProducesResponseType(typeof(ErrorDto), 400)]
    [Authorize]
    [HttpPost]
    public IActionResult GetHistory([FromBody] RequestHistoryModel _requestHistoryModel)
    {
        Log.Information($"History/GetHistory::{_requestHistoryModel.RequestedMemberId}");
        mLocalSignalRSenderService.RequestHistoryData(_requestHistoryModel);
        return Ok(new OkDto());
    }
}