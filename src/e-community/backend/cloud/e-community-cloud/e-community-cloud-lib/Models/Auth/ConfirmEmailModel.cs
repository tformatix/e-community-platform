using System;
using System.ComponentModel.DataAnnotations;

namespace e_community_cloud_lib.Models.Auth
{
    public class ConfirmEmailModel
    {
        [Required]
        public Guid MemberId { get; set; }

        [Required]
        public string ConfirmToken { get; set; }
    }
}
