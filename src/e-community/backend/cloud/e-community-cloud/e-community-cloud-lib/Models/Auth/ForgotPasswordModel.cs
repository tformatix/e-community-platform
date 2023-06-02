﻿using System.ComponentModel.DataAnnotations;

namespace e_community_cloud_lib.Models.Auth
{
    public class ForgotPasswordModel
    {

        [Required, EmailAddress]
        public string Email { get; set; }

    }
}
