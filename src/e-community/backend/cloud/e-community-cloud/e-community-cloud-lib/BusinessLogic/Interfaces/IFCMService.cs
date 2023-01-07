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
    /// <summary>
    /// Firebase Cloud Messaging
    /// </summary>
    public interface IFCMService
    {
        /// <summary>
        /// notification data for a new distribution
        /// </summary>
        FCMAndroidData NewDistribution { get; }

        /// <summary>
        /// notification data for a final distribution
        /// </summary>
        FCMAndroidData FinalDistribution { get; }

        /// <summary>
        /// notification data for a offline smart meter
        /// </summary>
        FCMAndroidData Offline { get; }

        /// <summary>
        /// notification data for a non-compliance (projected > forecast)
        /// </summary>
        FCMAndroidData NonComplianceMore { get; }

        /// <summary>
        /// notification data for a non-compliance (projected < forecast)
        /// </summary>
        FCMAndroidData NonComplianceLess { get; }

        /// <summary>
        /// updates firebase cloud messaging registration token
        /// </summary>
        /// <param name="_memberId">id of the member</param>
        /// <param name="_token">FCM token</param>
        Task RegisterFCMToken(Guid _memberId, string _token);

        /// <summary>
        /// sends message to all devices from a member
        /// </summary>
        /// <param name="_fcmAndroidData">FCM notification data for Android</param>
        /// <param name="_memberId">id of the member</param>
        Task<BatchResponse> SendPushNotificationMember(FCMAndroidData _fcmAndroidData, Guid _memberId);

        /// <summary>
        /// sends message to a list of fcm tokens
        /// </summary>
        /// <param name="_fcmAndroidData">FCM notification data for Android</param>
        /// <param name="_fcmTokens">fcm tokens</param>
        Task<BatchResponse> SendPushNotificationMulticast(FCMAndroidData _fcmAndroidData, List<MemberFCMToken> _fcmTokens);
    }
}
