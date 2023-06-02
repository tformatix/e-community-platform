using System;
using System.Collections.Generic;
using System.Linq;
using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.BusinessLogic.Interfaces.SignalR;
using e_community_cloud_lib.Database.Community;
using e_community_cloud_lib.Database.General;
using e_community_cloud_lib.Models.Blockchain;
using e_community_cloud_lib.Util.Extensions;
using e_community_cloud.Dtos;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
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

    [ProducesResponseType(typeof(OkDto), 200)]
    [ProducesResponseType(typeof(ErrorDto), 400)]
    [Authorize]
    [HttpPost]
    public IActionResult CreateConsentContract([FromBody] ConsentContractModel _consentContractModel)
    {
        var memberId = (Guid)User.GetMemberId();
        // generate a unique id
        _consentContractModel.ContractId = Guid.NewGuid().ToString();

        Log.Information($"Blockchain/CreateConsentContract::memberId: {memberId}; contractId: {_consentContractModel.ContractId}");
        
        mLocalSignalRSenderService.CreateConsentContract(memberId, _consentContractModel);
        return Ok(new OkDto());
    }
    
    [ProducesResponseType(typeof(OkDto), 200)]
    [ProducesResponseType(typeof(ErrorDto), 400)]
    [Authorize]
    [HttpPost]
    public IActionResult UpdateContractState([FromBody]  UpdateContractState _updateContractState)
    {
        var memberId = (Guid)User.GetMemberId();

        Log.Information($"Blockchain/UpdateContractState::contractId: {_updateContractState.ContractId} state: {_updateContractState.UpdateState}");
        
        mLocalSignalRSenderService.UpdateContractState(memberId, _updateContractState);
        return Ok(new OkDto());
    }
    
    [ProducesResponseType(typeof(OkDto), 200)]
    [ProducesResponseType(typeof(ErrorDto), 400)]
    [Authorize]
    [HttpGet]
    public IActionResult GetContractsForMember()
    {
        var memberId = (Guid)User.GetMemberId();
        Log.Information($"Blockchain/GetContractsForMember::{memberId}");
        
        mLocalSignalRSenderService.RequestContractsForMember(memberId);
        return Ok(new OkDto());
    }
    
    [ProducesResponseType(typeof(string), 200)]
    [ProducesResponseType(typeof(ErrorDto), 400)]
    [Authorize]
    [HttpPost]
    public string GetBlockchainAddressForMember([FromBody] string _memberId)
    {
        Log.Information($"Blockchain/GetBlockchainAddressForMember::{_memberId}");

        var address = _memberId switch
        {
            // tobi
            "bac8e613-83e5-4a23-44af-08da17d5b27d" => "0x6c11d23f23d8149c022bde529ca7dcdee3b378f6",
            // michi
            "1f302407-9258-4ef2-a474-08da3744278c" => "0x05dec64eaf4c27989af44d2616383fb8e2359a5d",
            _ => "not_found"
        };

        return address;
    }
    
    [ProducesResponseType(typeof(OkDto), 200)]
    [ProducesResponseType(typeof(ErrorDto), 400)]
    [Authorize]
    [HttpPost]
    public IActionResult DepositToContract([FromBody] ConsentContractModel _consentContract)
    {
        var memberId = (Guid)User.GetMemberId();
        Log.Information($"Blockchain/DepositToContract::{_consentContract.ContractId}");

        mLocalSignalRSenderService.RequestDepositToContract(memberId, _consentContract);
        return Ok(new OkDto());
    }
    
    [ProducesResponseType(typeof(OkDto), 200)]
    [ProducesResponseType(typeof(ErrorDto), 400)]
    [Authorize]
    [HttpPost]
    public IActionResult WithdrawFromContract([FromBody] ConsentContractModel _consentContract)
    {
        var memberId = (Guid)User.GetMemberId();
        Log.Information($"Blockchain/WithdrawFromContract::{_consentContract.ContractId}");

        mLocalSignalRSenderService.RequestWithdrawFromContract(memberId, _consentContract);
        return Ok(new OkDto());
    }
}