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

        public async Task<BatchResponse> SendPushNotificationMember(string _titleKey, List<string> _titleArgs, string _bodyKey, List<string> _bodyArgs, string _badge, Guid _memberId)
        {
            var fcmTokens = mDb.MemberFCMToken
                .Where(x => x.MemberId == _memberId && x.ValidUntil > DateTime.UtcNow)
                .ToList();

            return await SendPushNotificationMulticast(_titleKey, _titleArgs, _bodyKey, _bodyArgs, _badge, fcmTokens);
        }

        public async Task<BatchResponse> SendPushNotificationMulticast(string _titleKey, List<string> _titleArgs, string _bodyKey, List<string> _bodyArgs, string _badge, List<MemberFCMToken> _fcmTokens)
        {
            var message = new MulticastMessage
            {
                Android = new()
                {
                    Notification = new()
                    {
                        TitleLocKey = _titleKey,
                        TitleLocArgs = _titleArgs,
                        BodyLocKey = _bodyKey,
                        BodyLocArgs = _bodyArgs,
                    }
                },
                Data = new Dictionary<string, string>()
                {
                    { "badge", _badge }
                },
                Tokens = _fcmTokens
                    .Select(x => x.Token)
                    .ToList()
            };

            var response = await FirebaseMessaging.DefaultInstance.SendMulticastAsync(message);

            if (response.FailureCount > 0)
            {
                // remove invalid tokens
                for (var i = 0; i < response.Responses.Count; i++)
                {
                    if (!response.Responses[i].IsSuccess)
                    {
                        var statusCode = (int)response.Responses[i].Exception.HttpResponse.StatusCode;
                        if (statusCode == 400 || statusCode == 404)
                        {
                            mDb.MemberFCMToken.Remove(_fcmTokens[i]);
                        }
                    }
                }

                await mDb.SaveChangesAsync();
            }

            Log.Information("Successfully sent notifications: " + response);
            return response;
        }
    }
}
