using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.Database.General
{
    /// <summary>
    /// FCM (firebase cloud messaging) tokens for members
    /// </summary>
    public class MemberFCMToken
    {
        /// <summary>
        /// member (part of composite primary key)
        /// </summary>
        public Guid MemberId { get; set; }
        public Member Member { get; set; }

        /// <summary>
        /// firebase cloud messaging token
        /// </summary>
        public string Token { get; set; }

        /// <summary>
        /// valid until (null also invalid)
        /// </summary>
        public DateTime? ValidUntil { get; set; }
    }
}
