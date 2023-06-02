using e_community_cloud_lib.Database.General;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.BusinessLogic.Implementations
{
    public interface IEmailService
    {
        /// <summary>
        /// send forgot password email
        /// </summary>
        /// <param name="_member">triggered member</param>
        /// <param name="_token">password forgot token</param>
        /// <returns>success/unsuccess</returns>
        bool SendForgotPasswordMail(Member _member, string _token);

        /// <summary>
        /// send confirmation email
        /// </summary>
        /// <param name="_member">triggered member</param>
        /// <param name="_token">password forgot token</param>
        /// <returns>success/unsuccess</returns>
        bool SendConfirmationEmail(Member _member, string _token);
    }
}
