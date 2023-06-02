using e_community_cloud_lib.Database.General;
using e_community_cloud_lib.Models.Auth;
using e_community_cloud_lib.NonEntities;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.BusinessLogic.Interfaces
{
    public interface IAuthService
    {
        /// <summary>
        /// register a new member (send confirmation email)
        /// </summary>
        /// <param name="_registerMemberModel">contains data for new member</param>
        /// <returns></returns>
        Task<Member> Register(RegisterMemberModel _registerMemberModel);

        /// <summary>
        /// login with a member
        /// </summary>
        /// <param name="_loginMemberModel">email and password</param>
        /// <returns></returns>
        Task<Login> Login(LoginMemberModel _loginMemberModel);

        /// <summary>
        /// refresh login with refresh token
        /// </summary>
        /// <param name="_refreshToken">refresh token</param>
        /// <returns></returns>
        Task<Login> Refresh(string _refreshToken);

        /// <summary>
        /// confirm email
        /// </summary>
        /// <param name="_confirmEmailModel">member id and token</param>
        /// <returns></returns>
        Task ConfirmEmail(ConfirmEmailModel _confirmEmailModel);

        /// <summary>
        /// resend confirmation email
        /// </summary>
        /// <param name="_loginMemberModel">email and password</param>
        /// <returns></returns>
        Task ResendConfirmationEmail(LoginMemberModel _loginMemberModel);

        /// <summary>
        /// member forgot password (send email)
        /// </summary>
        /// <param name="_forgotPasswordModel">email</param>
        /// <returns></returns>
        Task ForgotPassword(ForgotPasswordModel _forgotPasswordModel);

        /// <summary>
        /// reset forgot password (from email)
        /// </summary>
        /// <param name="_changePasswordModel">member id, new password and reset token</param>
        /// <returns></returns>
        Task ResetPassword(ResetPasswordModel _changePasswordModel);

        /// <summary>
        /// change password of member
        /// </summary>
        /// <param name="_currentMemberId">member id</param>
        /// <param name="_changePasswordModel">new password and current password</param>
        /// <returns></returns>
        Task ChangePassword(Guid _currentMemberId, ChangePasswordModel _changePasswordModel);

        /// <summary>
        /// does smart meter belong to member
        /// </summary>
        /// <param name="_memberId">member id</param>
        /// <param name="_smartMeterId">smart meter id</param>
        /// <returns></returns>
        Task EnsureSmartMeter(Guid _memberId, Guid _smartMeterId);
    }
}
