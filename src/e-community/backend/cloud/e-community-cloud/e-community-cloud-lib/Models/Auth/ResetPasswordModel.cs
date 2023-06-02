using System;
using System.ComponentModel.DataAnnotations;

namespace e_community_cloud_lib.Models.Auth
{
    public class ResetPasswordModel
    {
        [Required]
        public Guid MemberId { get; set; }

        [Required]
        public string NewPassword { get; set; }

        [Required]
        public string ResetToken { get; set; }
    }
}
