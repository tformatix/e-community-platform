using e_community_cloud_lib.Database.General;
using FirebaseAdmin.Messaging;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.BusinessLogic.Interfaces
{
    public interface IFCMService
    {
        /// <summary>
        /// updates firebase cloud messaging registration token
        /// </summary>
        /// <param name="_memberId">id of the member</param>
        /// <param name="_token">FCM token</param>
        Task RegisterFCMToken(Guid _memberId, string _token);

        /// <summary>
        /// sends message to all devices from a member
        /// </summary>
        /// <param name="_titleKey">string ressource key of title</param>
        /// <param name="_titleArgs">string ressource arguments of title</param>
        /// <param name="_bodyKey">string ressource key of body</param>
        /// <param name="_bodyArgs">string ressource arguments of body</param>
        /// <param name="_badge">badge over nav view</param>
        /// <param name="_memberId">id of the member</param>
        Task<BatchResponse> SendPushNotificationMember(string _titleKey, List<string> _titleArgs, string _bodyKey, List<string> _bodyArgs, string _badge, Guid _memberId);

        /// <summary>
        /// sends message to a list of fcm tokens
        /// </summary>
        /// <param name="_titleKey">string ressource key of title</param>
        /// <param name="_titleArgs">string ressource arguments of title</param>
        /// <param name="_bodyKey">string ressource key of body</param>
        /// <param name="_bodyArgs">string ressource arguments of body</param>
        /// <param name="_badge">badge over nav view</param>
        /// <param name="_fcmTokens">fcm tokens</param>
        Task<BatchResponse> SendPushNotificationMulticast(string _titleKey, List<string> _titleArgs, string _bodyKey, List<string> _bodyArgs, string _badge, List<MemberFCMToken> _fcmTokens);
    }
}
