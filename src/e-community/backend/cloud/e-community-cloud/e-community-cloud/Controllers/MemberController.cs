using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using e_community_cloud.Dtos;
using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.Util.Extensions;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Serilog;

namespace e_community_cloud.Controllers
{
    [ApiController]
    [Route("[controller]/[action]")]
    public class MemberController : ControllerBase 
    {
        private readonly IMemberService mMemberService;

        public MemberController(IMemberService _memberService)
        {
            mMemberService = _memberService;
        }

        [ProducesResponseType(typeof(MinimalMemberDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [Authorize]
        [HttpGet]
        public async Task<IActionResult> GetMinimalMember() {
            var memberId = (Guid)User.GetMemberId();
            Log.Information($"Member/GetMinimalMember::{memberId}");

            var member = await mMemberService.GetMember(memberId);
            return Ok(member.CopyPropertiesTo(new MinimalMemberDto()));
        }

        [ProducesResponseType(typeof(MemberDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [Authorize]
        [HttpGet]
        public async Task<IActionResult> GetMember() {
            var memberId = (Guid)User.GetMemberId();
            Log.Information($"Member/GetMember::{memberId}");

            var member = await mMemberService.GetMember(memberId);
            return Ok(member.CopyPropertiesTo(new MemberDto()));
        }
    }
}
