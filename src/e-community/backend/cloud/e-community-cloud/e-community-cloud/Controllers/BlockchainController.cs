using System;
using System.Collections.Generic;
using System.Linq;
using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.BusinessLogic.Interfaces.SignalR;
using e_community_cloud_lib.Database.Community;
using e_community_cloud_lib.Database.General;
using e_community_cloud_lib.Util.Extensions;
using e_community_cloud.Dtos;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Serilog;

namespace e_community_cloud.Controllers;

[ApiController]
[Route("[controller]/[action]")]
public class BlockchainController : ControllerBase
{
    private readonly IBlockchainService mBlockchainService;
    private readonly ILocalSignalRSenderService mLocalSignalRSenderService;
    
    public BlockchainController(IBlockchainService _blockchainService, ILocalSignalRSenderService _localSignalRSenderService)
    {
        mBlockchainService = _blockchainService;
        mLocalSignalRSenderService = _localSignalRSenderService;
    }

    [ProducesResponseType(typeof(OkDto), 200)]
    [ProducesResponseType(typeof(ErrorDto), 400)]
    [Authorize]
    [HttpGet]
    public IActionResult GetAccountBalance()
    {
        var memberId = (Guid)User.GetMemberId();
        Log.Information($"Blockchain/GetAccountBalance::{memberId}");
        mLocalSignalRSenderService.RequestBlockchainAccountBalance(memberId);

        return Ok(new OkDto());
    }
}