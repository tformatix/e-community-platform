using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.Database;
using e_community_cloud_lib.Database.General;
using e_community_cloud_lib.Models.Auth;
using e_community_cloud_lib.NonEntities;
using e_community_cloud_lib.Util;
using e_community_cloud_lib.Util.BusinessLogic;
using e_community_cloud_lib.Util.Extensions;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.IdentityModel.Tokens;
using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Security.Claims;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.BusinessLogic.Implementations
{
    /// <summary>
    /// <seealso cref="IAuthService"/>
    /// </summary>
    public class AuthService : IAuthService
    {
        private readonly UserManager<Member> mUserManager;
        private readonly SignInManager<Member> mSignInManager;
        private readonly IConfiguration mConfiguration;
        private readonly ECommunityCloudContext mDb;
        private readonly IEmailService mEmailService;
        private Language mDefaultLanguage;

        public AuthService(UserManager<Member> _userManager, SignInManager<Member> _signInManager, IConfiguration _configuration, ECommunityCloudContext _db, IEmailService _emailService)
        {
            mConfiguration = _configuration;
            mUserManager = _userManager;
            mSignInManager = _signInManager;
            mDb = _db;
            mEmailService = _emailService;
        }

        public async Task<Member> Register(RegisterMemberModel _registerMemberModel)
        {
            var nme = mUserManager.NormalizeEmail(_registerMemberModel.Email);
            var existing = await mDb.Member.FirstOrDefaultAsync(u => u.NormalizedEmail == nme && u.EmailConfirmed);
            // ignore accounts where email is not confirmed
            if (existing != null && existing.EmailConfirmed)
            {
                throw new ServiceException(ServiceException.Type.EMAIL_TAKEN);
            }

            var language = await mDb.Language
                    .Where(x => x.Name == _registerMemberModel.LanguageName)
                    .FirstOrDefaultAsync();

            // switch to default language
            if(language == null)
            {
                if (mDefaultLanguage == null)
                {
                    mDefaultLanguage = await mDb.Language
                        .Where(x => x.Name == Constants.DEFAULT_LANGUAGE)
                        .FirstOrDefaultAsync();
                }
                language = mDefaultLanguage;
            }

            var member = new Member
            {
                UserName = _registerMemberModel.UserName,
                Email = _registerMemberModel.Email,
                Language = language
            };

            IdentityResult result = await mUserManager.CreateAsync(member, _registerMemberModel.Password);

            if (!result.Succeeded)
            {
                throw new ServiceException(ServiceException.Type.CREATE_ACCOUNT_FAILED);
            }

            member = await mUserManager.FindByEmailAsync(_registerMemberModel.Email);

            await SendConfirmationEmail(member);

            return member;
        }

        public async Task<Login> Login(LoginMemberModel _loginMemberModel)
        {
            var member = await TryLoginMember(_loginMemberModel);

            var jwtConfig = mConfiguration.GetSection("Jwt");
            var utcNow = DateTime.UtcNow;

            string accessToken = GetAccessToken(member, utcNow, jwtConfig);
            string refreshToken = GetRefreshToken(member, utcNow, jwtConfig);
            return new Login
            {
                MemberId = member.Id,
                AccessToken = accessToken,
                RefreshToken = refreshToken
            };
        }

        public async Task<Login> Refresh(string _refreshToken)
        {
            var jwtConfig = mConfiguration.GetSection("Jwt");
            var member = await ValidateRefreshToken(_refreshToken, jwtConfig);

            var utcNow = DateTime.UtcNow;
            string accessToken = GetAccessToken(member, utcNow, jwtConfig);
            string newRefreshToken = GetRefreshToken(member, utcNow, jwtConfig);
            return new Login
            {
                MemberId = member.Id,
                AccessToken = accessToken,
                RefreshToken = newRefreshToken
            };
        }

        public async Task ConfirmEmail(ConfirmEmailModel _confirmEmailModel)
        {
            var member = await mUserManager.FindByIdAsync(_confirmEmailModel.MemberId.ToString());
            if (member.EmailConfirmed)
            {
                throw new ServiceException(ServiceException.Type.EMAIL_ALREADY_CONFIRMED);
            }

            var res = await mUserManager.ConfirmEmailAsync(member, _confirmEmailModel.ConfirmToken);
            if (!res.Succeeded)
            {
                throw new ServiceException(ServiceException.Type.EMAIL_CONFIRM_FAILED);
            }
        }

        public async Task ResendConfirmationEmail(LoginMemberModel _loginMemberModel)
        {
            var member = await mUserManager.FindByEmailAsync(_loginMemberModel.Email);
            if (member == null)
            {
                throw new ServiceException(ServiceException.Type.INVALID_CREDENTIALS);
            }

            if (member.EmailConfirmed)
            {
                throw new ServiceException(ServiceException.Type.EMAIL_ALREADY_CONFIRMED);
            }

            await SendConfirmationEmail(member);
        }

        public async Task ForgotPassword(ForgotPasswordModel _forgotPasswordModel)
        {
            var member = await mUserManager.FindByEmailAsync(_forgotPasswordModel.Email);

            if (member != null) {

                Validate.MemberEmailIsConfirmed(member);

                var token = await mUserManager.GeneratePasswordResetTokenAsync(member);
                var resp = mEmailService.SendForgotPasswordMail(member, token);
                if (!resp) {
                    throw new ServiceException(ServiceException.Type.COULDNT_SEND_EMAIL);
                }
            }
        }

        public async Task ResetPassword(ResetPasswordModel _resetPasswordModel)
        {
            var member = await mUserManager.FindByIdAsync(_resetPasswordModel.MemberId.ToString());
            if (member == null)
            {
                throw new ServiceException(ServiceException.Type.INVALID_CREDENTIALS);
            }
            var res = await mUserManager.ResetPasswordAsync(member, _resetPasswordModel.ResetToken, _resetPasswordModel.NewPassword);
            if (!res.Succeeded)
            {
                throw new ServiceException(ServiceException.Type.RESET_PASSWORD_FAILED);
            }
        }

        public async Task ChangePassword(Guid _currentMemberId, ChangePasswordModel _changePasswordModel)
        {
            var member = await mUserManager.FindByIdAsync(_currentMemberId.ToString());

            Validate.MemberEmailIsConfirmed(member);

            var res = await mUserManager.ChangePasswordAsync(member, _changePasswordModel.CurrentPassword, _changePasswordModel.NewPassword);
            if (!res.Succeeded)
            {
                throw new ServiceException(ServiceException.Type.CHANGE_PASSWORD_FAILED);
            }
        }

        /// <summary>
        /// send confirmation mail
        /// </summary>
        /// <param name="_member">issued member</param>
        private async Task SendConfirmationEmail(Member _member)
        {
            var token = await mUserManager.GenerateEmailConfirmationTokenAsync(_member);
            var resp = mEmailService.SendConfirmationEmail(_member, token);

            if (!resp)
            {
                throw new ServiceException(ServiceException.Type.COULDNT_SEND_EMAIL);
            }
        }

        /// <summary>
        /// try to login member (checks several attributes)
        /// </summary>
        /// <param name="_loginMemberModel">email + password</param>
        /// <returns>logged in member</returns>
        private async Task<Member> TryLoginMember(LoginMemberModel _loginMemberModel)
        {
            var member = await mDb.Member.FirstOrDefaultAsync(u => u.NormalizedEmail == mUserManager.NormalizeEmail(_loginMemberModel.Email));

            if (member == null)
            {
                throw new ServiceException(ServiceException.Type.INVALID_CREDENTIALS);
            }

            Validate.MemberEmailIsConfirmed(member);

            var result = await mSignInManager.CheckPasswordSignInAsync(member, _loginMemberModel.Password, false);
            if (result.Succeeded)
            {
                await mUserManager.ResetAccessFailedCountAsync(member);
                return member;
            }
            else
            {
                await mUserManager.AccessFailedAsync(member);
            }
            if (result.IsLockedOut)
            {
                throw new ServiceException(ServiceException.Type.ACCOUNT_LOCKED);
            }
            throw new ServiceException(ServiceException.Type.INVALID_CREDENTIALS);
        }

        /// <summary>
        /// create new access token
        /// </summary>
        /// <param name="_member">issued member</param>
        /// <param name="_utcNow">current date</param>
        /// <param name="_jwtConfig">appsetting.json --> JWT</param>
        /// <returns>access token</returns>
        private string GetAccessToken(Member _member, DateTime _utcNow, IConfigurationSection _jwtConfig)
        {
            var claims = new List<Claim>
            {
                new Claim(JwtRegisteredClaimNames.Sub, _member.Id.ToString()),
                new Claim(JwtRegisteredClaimNames.Iat, _utcNow.ToString()),

            };

            //foreach (var role in await userManager.GetRolesAsync(member))
            //{
            //    claims.Add(new Claim(ClaimTypes.Role, role));
            //}

            var signingKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_jwtConfig.GetValue<string>("AccessKey")));
            var signingCredentials = new SigningCredentials(signingKey, SecurityAlgorithms.HmacSha256);

            // generate token
            var jwt = new JwtSecurityToken(
                signingCredentials: signingCredentials,
                claims: claims,
                notBefore: _utcNow,
                expires: _utcNow.AddMinutes(_jwtConfig.GetValue<int>("AccessTokenLifetimeMinutes")),
                issuer: _jwtConfig.GetValue<string>("Issuer"),
                audience: _jwtConfig.GetValue<string>("Audience")
                );
            return new JwtSecurityTokenHandler().WriteToken(jwt);
        }

        /// <summary>
        /// create new refresh token
        /// </summary>
        /// <param name="_member">issued member</param>
        /// <param name="_utcNow">current date</param>
        /// <param name="_jwtConfig">appsetting.json --> JWT</param>
        /// <returns>refresh token</returns>
        private string GetRefreshToken(Member _member, DateTime _utcNow, IConfigurationSection _jwtConfig)
        {
            var claims = new List<Claim>
            {
                new Claim(JwtRegisteredClaimNames.Sub, _member.Id.ToString()),
                new Claim(JwtRegisteredClaimNames.Iat, _utcNow.ToString()),

            };

            var signingKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_jwtConfig.GetValue<string>("RefreshKey")));
            var signingCredentials = new SigningCredentials(signingKey, SecurityAlgorithms.HmacSha256);

            // generate token
            var jwt = new JwtSecurityToken(
                signingCredentials: signingCredentials,
                claims: claims,
                notBefore: _utcNow,
                expires: _utcNow.AddDays(_jwtConfig.GetValue<int>("RefreshTokenLifetimeDays")),
                issuer: _jwtConfig.GetValue<string>("Issuer"),
                audience: _jwtConfig.GetValue<string>("Audience")
                );
            return new JwtSecurityTokenHandler().WriteToken(jwt);
        }

        /// <summary>
        /// validates refresh token
        /// </summary>
        /// <param name="_refreshToken">refresh token</param>
        /// <param name="_jwtConfig">appsetting.json --> JWT</param>
        /// <returns>member based on claims</returns>
        private async Task<Member> ValidateRefreshToken(string _refreshToken, IConfigurationSection _jwtConfig)
        {
            // validation parameters
            var tokenValidationParameters = new TokenValidationParameters()
            {
                ValidateIssuer = true,
                ValidateAudience = true,
                IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_jwtConfig.GetValue<string>("RefreshKey"))),
                ValidIssuer = _jwtConfig.GetValue<string>("Issuer"),
                ValidAudience = _jwtConfig.GetValue<string>("Audience")
            };

            var tokenHandler = new JwtSecurityTokenHandler();

            // validate token
            Member member;
            try
            {
                ClaimsPrincipal principal = tokenHandler.ValidateToken(_refreshToken, tokenValidationParameters, out var validatedToken);
                var memberId = principal?.GetMemberId();
                member = await mDb.Member.FirstAsync(x => x.Id == memberId);
            }
            catch (Exception)
            {
                throw new ServiceException(ServiceException.Type.INVALID_REFRESH_TOKEN);
            }

            if (await mUserManager.IsLockedOutAsync(member))
            {
                throw new ServiceException(ServiceException.Type.ACCOUNT_LOCKED);
            }
            return member;
        }

        public async Task EnsureSmartMeter(Guid _memberId, Guid _smartMeterId) {
            var smartMeter = await mDb.SmartMeter
                .FirstOrDefaultAsync(x => x.Id == _smartMeterId && x.MemberId == _memberId);

            if(smartMeter == null) {
                // smart meter doesn't belong to member
                throw (new ServiceException(ServiceException.Type.SMART_METER_NOT_TO_USER));
            }
        }
    }
}
