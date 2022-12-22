using e_community_cloud.Dtos;
using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.Models.Auth;
using e_community_cloud_lib.Util;
using e_community_cloud_lib.Util.Extensions;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Serilog;
using System;
using System.Security.Claims;
using System.Threading.Tasks;

namespace e_community_cloud.Controllers
{
    [ApiController]
    [Route("[controller]/[action]")]
    public class AuthController : ControllerBase
    {
        private readonly IAuthService mAuthService;

        public AuthController(IAuthService _authService)
        {
            mAuthService = _authService;
        }

        [ProducesResponseType(typeof(MemberDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [AllowAnonymous]
        [HttpPost]
        public async Task<IActionResult> Register([FromBody] RegisterMemberModel _registerMemberModel)
        {
            Log.Information($"Auth/Register::{_registerMemberModel.Email}");
            var response = await mAuthService.Register(_registerMemberModel);
            return Ok(response.CopyPropertiesTo(new MemberDto()));
        }

        [ProducesResponseType(typeof(LoginDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [AllowAnonymous]
        [HttpPost]
        public async Task<IActionResult> Login([FromBody] LoginMemberModel _loginMemberModel)
        {
            Log.Information($"Auth/Login::{_loginMemberModel.Email}");
            var response = await mAuthService.Login(_loginMemberModel);
            return Ok(response.CopyPropertiesTo(new LoginDto()));
        }

        [ProducesResponseType(typeof(LoginDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [AllowAnonymous]
        [HttpPost]
        public async Task<IActionResult> Refresh([FromBody] string _refreshToken)
        {
            var response = await mAuthService.Refresh(_refreshToken);
            Log.Information($"Auth/Refresh::{response.MemberId}");
            return Ok(response.CopyPropertiesTo(new LoginDto()));
        }

        [ProducesResponseType(typeof(OkDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [AllowAnonymous]
        [HttpPost]
        public async Task<IActionResult> ConfirmEmail([FromBody] ConfirmEmailModel _confirmEmailModel)
        {
            Log.Information($"Auth/ConfirmEmail::{_confirmEmailModel.MemberId}");
            await mAuthService.ConfirmEmail(_confirmEmailModel);
            return Ok(new OkDto());
        }

        [ProducesResponseType(typeof(OkDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [AllowAnonymous]
        [HttpPost]
        public async Task<IActionResult> ResendConfirmationEmail([FromBody] LoginMemberModel _loginMemberModel)
        {
            Log.Information($"Auth/ResendConfirmationEmail::{_loginMemberModel.Email}");
            await mAuthService.ResendConfirmationEmail(_loginMemberModel);
            return Ok(new OkDto());
        }

        [ProducesResponseType(typeof(OkDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [AllowAnonymous]
        [HttpPost]
        public async Task<IActionResult> ForgotPassword([FromBody] ForgotPasswordModel _forgotPasswordModel)
        {
            Log.Information($"Auth/ForgotPassword::{_forgotPasswordModel.Email}");
            await mAuthService.ForgotPassword(_forgotPasswordModel);
            return Ok(new OkDto());
        }

        [ProducesResponseType(typeof(OkDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [AllowAnonymous]
        [HttpPost]
        public async Task<IActionResult> ResetPassword([FromBody] ResetPasswordModel _resetPasswordModel)
        {
            Log.Information($"Auth/ResetPassword::{_resetPasswordModel.MemberId}");
            await mAuthService.ResetPassword(_resetPasswordModel);
            return Ok(new OkDto());
        }

        [ProducesResponseType(typeof(OkDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [Authorize]
        [HttpPost]
        public async Task<IActionResult> ChangePassword([FromBody] ChangePasswordModel _changePasswordModel)
        {
            var memberId = (Guid)User.GetMemberId();
            Log.Information($"Auth/ChangePassword::{memberId}");
            await mAuthService.ChangePassword(memberId, _changePasswordModel);
            return Ok(new OkDto());
        }
    }
}
