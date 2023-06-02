using e_community_cloud_lib.BusinessLogic.Implementations;
using e_community_cloud_lib.Database.General;
using MailKit.Net.Smtp;
using Microsoft.Extensions.Configuration;
using MimeKit;
using System;
using System.IO;
using System.Threading.Tasks;
using System.Web;

namespace e_community_cloud_lib.BusinessLogic.Interfaces
{
    /// <summary>
    /// <seealso cref="IEmailService"/>
    /// </summary>
    public class EmailService : IEmailService
    {
        /// <summary>
        /// path to mail folder (contains mails in html representation)
        /// </summary>
        private const string PATH_TO_HTML = "Util/Mails/";
        
        /// <summary>
        /// placeholder in mail html's
        /// </summary>
        private const string PLACEHOLDER_URL = "###PLACEHOLDER_URL###";

        private readonly IConfiguration mConfiguration;

        public EmailService(IConfiguration _configuration)
        {
            mConfiguration = _configuration;
        }

        public bool SendConfirmationEmail(Member _member, string _token)
        {
            var path = mConfiguration.GetValue<string>("Web");
            var confirmUrl = $"{path}/auth/confirmEmail?t={HttpUtility.UrlEncode(_token)}&mid={HttpUtility.UrlEncode(_member.Id.ToString())}";

            string html = File.ReadAllText($"{PATH_TO_HTML}confirm-email.html")
                .Replace(PLACEHOLDER_URL, confirmUrl);

            return SendEmail(_member.Email, _member.UserName, "Confirm Email", html);
        }

        public bool SendForgotPasswordMail(Member _member, string _token)
        {
            var path = mConfiguration.GetValue<string>("Web");
            var resetUrl = $"{path}/auth/resetPassword?t={HttpUtility.UrlEncode(_token)}&mid={HttpUtility.UrlEncode(_member.Id.ToString())}";

            string html = File.ReadAllText($"{PATH_TO_HTML}reset-password.html")
                .Replace(PLACEHOLDER_URL, resetUrl);

            return SendEmail(_member.Email, _member.UserName, "Reset Password", html);
        }

        private bool SendEmail(string _toMail, string _toName, string _subject, string _html)
        {
            try
            {
                var section = mConfiguration.GetSection("Mail");
                var server = section.GetValue<string>("Server");
                var port = section.GetValue<int>("Port");
                var fromMail = section.GetValue<string>("Email");
                var fromName = section.GetValue<string>("Name");
                var pw = section.GetValue<string>("Password");

                var mailMessage = new MimeMessage();
                mailMessage.From.Add(new MailboxAddress(fromName, fromMail));
                mailMessage.To.Add(new MailboxAddress(_toName, _toMail));
                mailMessage.Subject = _subject;
                mailMessage.Body = new BodyBuilder() { HtmlBody = _html }.ToMessageBody();

                using (var smtpClient = new SmtpClient())
                {
                    smtpClient.Connect(server, port, true);
                    smtpClient.Authenticate(fromMail, pw);
                    smtpClient.Send(mailMessage);
                    smtpClient.Disconnect(true);
                }

                return true;
            }
            catch (Exception)
            {
                return false;
            }
        }
    }
}
