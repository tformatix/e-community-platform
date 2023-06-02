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
using e_community_cloud_lib.NonEntities;

namespace e_community_cloud_lib.BusinessLogic.Implementations {
    public class FCMService : IFCMService {
        private readonly ECommunityCloudContext mDb;
        private readonly IConfiguration mConfiguration;

        public FCMService(ECommunityCloudContext _db, IConfiguration _configuration) {
            mDb = _db;
            mConfiguration = _configuration;
        }

        public FCMAndroidData NewDistribution => new() {
            Id = "e_community",
            TitleKey = "notification_new_distribution_title",
            BodyKey = "notification_new_distribution_body",
        };

        public FCMAndroidData FinalDistribution => new() {
            Id = "e_community",
            TitleKey = "notification_final_distribution_title",
            BodyKey = "notification_final_distribution_body",
        };

        public FCMAndroidData Offline => new() {
            Id = "e_community",
            TitleKey = "notification_offline_title",
            BodyKey = "notification_offline_body",
        };

        public FCMAndroidData NonComplianceMore => new() {
            Id = "e_community",
            TitleKey = "notification_non_compliance_title",
            BodyKey = "notification_non_compliance_more_body",
        };

        public FCMAndroidData NonComplianceLess => new() {
            Id = "e_community",
            TitleKey = "notification_non_compliance_title",
            BodyKey = "notification_non_compliance_less_body",
        };

        public async Task RegisterFCMToken(Guid _memberId, string _token) {
            var fcmToken = mDb.MemberFCMToken
                .FirstOrDefault(x => x.Token == _token);

            var validUntil = DateTime.UtcNow.AddMonths(mConfiguration.GetValue<int>("FCMTokenLifetimeMonth"));
            if (fcmToken != null) {
                // extend lifetime
                if(fcmToken.MemberId != _memberId) {
                    mDb.MemberFCMToken.Remove(fcmToken);
                    await mDb.SaveChangesAsync();
                    mDb.MemberFCMToken.Add(new MemberFCMToken() {
                        MemberId = _memberId,
                        Token = _token,
                        ValidUntil = validUntil
                    });
                } else {
                    fcmToken.ValidUntil = validUntil;
                }
            }
            else {
                // register new token
                mDb.MemberFCMToken.Add(new MemberFCMToken() {
                    MemberId = _memberId,
                    Token = _token,
                    ValidUntil = validUntil
                });
            }
            await mDb.SaveChangesAsync();
        }

        public async Task<BatchResponse> SendPushNotificationMember(FCMAndroidData _fcmAndroidData, Guid _memberId) {
            // get valid FCM-Tokens for a member
            var fcmTokens = mDb.MemberFCMToken
                .Where(x => x.MemberId == _memberId && x.ValidUntil > DateTime.UtcNow)
                .ToList();
            await mDb.SaveChangesAsync();

            return await SendPushNotificationMulticast(_fcmAndroidData, fcmTokens);
        }

        public async Task<BatchResponse> SendPushNotificationMulticast(FCMAndroidData _fcmAndroidData, List<MemberFCMToken> _fcmTokens) {
            var message = new MulticastMessage {
                Android = new() {
                    Notification = new() {
                        TitleLocKey = _fcmAndroidData.TitleKey,
                        TitleLocArgs = _fcmAndroidData.TitleArgs,
                        BodyLocKey = _fcmAndroidData.BodyKey,
                        BodyLocArgs = _fcmAndroidData.BodyArgs,
                    },
                    Data = new Dictionary<string, string>(){
                        { "id", _fcmAndroidData.Id }
                    }
                },
                Tokens = _fcmTokens
                    .Select(x => x.Token)
                    .ToList()
            };

            var response = await FirebaseMessaging.DefaultInstance.SendMulticastAsync(message);

            if (response.FailureCount > 0) {
                // remove invalid tokens
                for (var i = 0; i < response.Responses.Count; i++) {
                    if (!response.Responses[i].IsSuccess) {
                        var statusCode = (int)response.Responses[i].Exception.HttpResponse.StatusCode;
                        if (statusCode == 400 || statusCode == 404) {
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
