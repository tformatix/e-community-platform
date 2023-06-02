using System;
using System.ComponentModel.DataAnnotations;

namespace e_community_cloud_lib.Models.Auth
{
    public class RegisterMemberModel
    {
        [Required, EmailAddress]
        public string Email { get; set; }

        [Required]
        public string UserName { get; set; }

        [Required]
        public string Password { get; set; }

        [Required]
        public string LanguageName { get; set; }
    }
}
