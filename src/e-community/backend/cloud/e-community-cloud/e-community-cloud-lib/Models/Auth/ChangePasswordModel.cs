using System.ComponentModel.DataAnnotations;

namespace e_community_cloud_lib.Models.Auth
{
    public class ChangePasswordModel
    {
        [Required]
        public string NewPassword { get; set; }

        [Required]
        public string CurrentPassword { get; set; }

    }
}
