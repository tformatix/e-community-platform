using e_community_cloud_lib.Database.General;
using e_community_cloud_lib.NonEntities;
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
        FCMAndroidData NewDistribution { get; }
        FCMAndroidData FinalDistribution { get; }

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
        /// <param name="_memberId">id of the member</param>
        Task<BatchResponse> SendPushNotificationMember(FCMAndroidData _fcmAndroidData, Guid _memberId);

        /// <summary>
        /// sends message to a list of fcm tokens
        /// </summary>
        /// <param name="_titleKey">string ressource key of title</param>
        /// <param name="_titleArgs">string ressource arguments of title</param>
        /// <param name="_bodyKey">string ressource key of body</param>
        /// <param name="_bodyArgs">string ressource arguments of body</param>
        /// <param name="_fcmTokens">fcm tokens</param>
        Task<BatchResponse> SendPushNotificationMulticast(FCMAndroidData _fcmAndroidData, List<MemberFCMToken> _fcmTokens);
    }
}
