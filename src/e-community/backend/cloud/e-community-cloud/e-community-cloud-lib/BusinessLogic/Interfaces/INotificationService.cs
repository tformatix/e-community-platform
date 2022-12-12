using e_community_cloud_lib.Database.General;
using FirebaseAdmin.Messaging;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.BusinessLogic.Interfaces
{
    public interface INotificationService
    {
        Task RegisterFCMToken(Guid _memberId, string _token);
        Task<BatchResponse> SendPushNotificationMember(string _title, string _content, Guid _memberId);
        Task<BatchResponse> SendPushNotificationMulticast(string _title, string _content, List<MemberFCMToken> _fcmTokens);
    }
}
