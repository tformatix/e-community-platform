using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.Database.General;
using e_community_cloud_lib.Database;
using FirebaseAdmin.Messaging;
using Serilog;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.Extensions.Configuration;

namespace e_community_cloud_lib.BusinessLogic.Implementations
{
    public class NotificationService : INotificationService
    {
        private readonly ECommunityCloudContext mDb;
        private readonly IConfiguration mConfiguration;


        public NotificationService(ECommunityCloudContext _db, IConfiguration _configuration)
        {
            mDb = _db;
            mConfiguration = _configuration;
        }

        public async Task RegisterFCMToken(Guid _memberId, string _token)
        {
            var fcmToken = mDb.MemberFCMToken
                .FirstOrDefault(x => x.MemberId == _memberId && x.Token == _token);

            var validUntil = DateTime.UtcNow.AddMonths(mConfiguration.GetValue<int>("FCMTokenLifetimeMonth"));
            if (fcmToken != null)
            {
                fcmToken.ValidUntil = validUntil;
            }
            else
            {
                mDb.MemberFCMToken.Add(new MemberFCMToken()
                {
                    MemberId = _memberId,
                    Token = _token,
                    ValidUntil = validUntil
                });
            }
            await mDb.SaveChangesAsync();
        }

        public async Task<BatchResponse> SendPushNotificationMember(string _title, string _content, Guid _memberId)
        {
            var fcmTokens = mDb.MemberFCMToken
                .Where(x => x.MemberId == _memberId && x.ValidUntil > DateTime.UtcNow)
                .ToList();

            return await SendPushNotificationMulticast(_title, _content, fcmTokens);
        }

        public async Task<BatchResponse> SendPushNotificationMulticast(string _title, string _content, List<MemberFCMToken> _fcmTokens)
        {
            var message = new MulticastMessage
            {
                Notification = new()
                {
                    Title = _title,
                    Body = _content
                },
                Tokens = _fcmTokens
                    .Select(x => x.Token)
                    .Distinct()
                    .ToList()
            };
            var response = await FirebaseMessaging.DefaultInstance.SendMulticastAsync(message);

            if (response.FailureCount > 0)
            {
                response.Responses.Where(x => !x.IsSuccess).ToList().ForEach(_singleResponse =>
                {
                    var statusCode = (int)_singleResponse.Exception.HttpResponse.StatusCode;
                    if (statusCode == 400 || statusCode == 404)
                    {
                        // TODO: token invalid --> delete from DB
                    }
                });
            }

            Log.Information("Successfully sent notifications: " + response);
            return response;
        }
    }
}
