using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.BusinessLogic.Interfaces.SignalR;
using e_community_cloud_lib.Database;
using e_community_cloud_lib.Database.Community;
using e_community_cloud_lib.Database.General;
using e_community_cloud_lib.Database.Local;
using e_community_cloud_lib.Models.Distribution;
using e_community_cloud_lib.NonEntities;
using e_community_cloud_lib.Util;
using Microsoft.EntityFrameworkCore;
using MimeKit.Encodings;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.BusinessLogic.Implementations {
    public class MonitoringService : IMonitoringService {

        private readonly ECommunityCloudContext mDb;
        private readonly IFCMService mFCMService;
        private readonly ILocalSignalRSenderService mLocalSignalRSenderService;
        private readonly IDistributionService mDistributionService;

        public MonitoringService(ECommunityCloudContext _db, IFCMService _fcmService, ILocalSignalRSenderService _localSignalRSenderService, IDistributionService _distributionService) {
            mDb = _db;
            mFCMService = _fcmService;
            mLocalSignalRSenderService = _localSignalRSenderService;
            mDistributionService = _distributionService;
        }

        public async Task StartMonitoring(DateTime _timestamp) {
            await HandlePreviousMonitoring(_timestamp);

            var monitoring = new Monitoring() {
                IsCalculating = true,
                Timestamp = _timestamp
            };
            await mDb.Monitoring.AddAsync(monitoring);
            await mDb.SaveChangesAsync();

            var eCommunityMemberships = await mDb.ECommunityMembership
                .Include(x => x.Member)
                .ThenInclude(x => x.SmartMeters)
                .Where(x => Constants.ACTIVE_MEMBER_PERMISSIONS.Contains(x.ECommunityPermission))
                .ToListAsync();

            await mDb.MeterDataMonitoring.AddRangeAsync(eCommunityMemberships
                    .SelectMany(x => x.Member.SmartMeters)
                    .Select(x => new MeterDataMonitoring() {
                        SmartMeterId = x.Id,
                        MonitoringId = monitoring.Id,
                        ActiveEnergyMinus = null,
                        ActiveEnergyPlus = null,
                        ProjectedActiveEnergyPlus = null,
                        NonCompliance = false,
                    })
                );

            await mDb.SmartMeter.ForEachAsync(x => {
                x.IsNonComplianceMuted = false;
            });

            await mDb.SaveChangesAsync();

            mLocalSignalRSenderService.RequestMeterDataMonitoring();
        }

        private async Task HandlePreviousMonitoring(DateTime _timestamp) {
            var calculatingMonitoring = await GetCalculatingMonitoring();
            if (calculatingMonitoring != null) {
                calculatingMonitoring.IsCalculating = false;

                calculatingMonitoring.MeterDataMonitorings
                    .Where(x => x.ActiveEnergyMinus == null)
                    .ToList()
                    .ForEach(async x => {
                        // offline -> send notification
                        var fcmAndroidData = mFCMService.Offline;
                        fcmAndroidData.BodyArgs = new List<string>() { x.SmartMeter.Name };
                        await mFCMService.SendPushNotificationMember(fcmAndroidData, x.SmartMeter.MemberId);
                    });

                if (_timestamp.Minute == Constants.DISTRIBUTION_MONITOR_INTERVAL_MINUTES) {
                    // 5 minutes after full hour (e.g. 12:05): store actual energy into portions and delete all monitoring data except current

                    var previousTimestamp = calculatingMonitoring.Timestamp.AddHours(-1);
                    var monitoringHourBefore = await mDb.Monitoring
                        .Include(x => x.MeterDataMonitorings)
                        .OrderBy(x => x.Id)
                        .FirstOrDefaultAsync();

                    if (monitoringHourBefore != null && monitoringHourBefore.Timestamp.Hour == previousTimestamp.Hour && monitoringHourBefore.Timestamp.Minute == previousTimestamp.Minute) {
                        // monitoring entry before an hour available

                        var currentDistributions = await GetCurrentDistributions();
                        foreach (var distribution in currentDistributions) {
                            foreach (var portion in distribution.SmartMeterPortions) {
                                var currentMeterData = calculatingMonitoring.MeterDataMonitorings
                                    .FirstOrDefault(x => x.SmartMeterId == portion.SmartMeterId);
                                var meterDataHourBefore = monitoringHourBefore.MeterDataMonitorings
                                    .FirstOrDefault(x => x.SmartMeterId == portion.SmartMeterId);

                                if (calculatingMonitoring != null && meterDataHourBefore != null &&
                                    currentMeterData.ActiveEnergyPlus != null && meterDataHourBefore.ActiveEnergyPlus != null &&
                                    currentMeterData.ActiveEnergyMinus != null && meterDataHourBefore.ActiveEnergyMinus != null
                                ) {
                                    portion.ActualActiveEnergyPlus = currentMeterData.ActiveEnergyPlus - meterDataHourBefore.ActiveEnergyPlus;
                                    portion.ActualActiveEnergyMinus = currentMeterData.ActiveEnergyMinus - meterDataHourBefore.ActiveEnergyMinus;
                                }
                            }
                        }
                    }

                    mDb.Monitoring.RemoveRange(
                        mDb.Monitoring
                            .Where(x => x.Id != calculatingMonitoring.Id)
                    );
                }


                await mDb.SaveChangesAsync();
            }
        }

        public async Task MeterDataMonitoringArrived(MeterDataMonitoringModel _meterDataMonitoringModel) {
            var calculatingMonitoring = await GetCalculatingMonitoring();

            var meterDataMontoring = calculatingMonitoring.MeterDataMonitorings
                .FirstOrDefault(x => x.SmartMeterId == _meterDataMonitoringModel.SmartMeterId);

            if (meterDataMontoring != null) {
                meterDataMontoring.ActiveEnergyMinus = _meterDataMonitoringModel.ActiveEnergyMinus;
                meterDataMontoring.ActiveEnergyPlus = _meterDataMonitoringModel.ActiveEnergyPlus;

                var meterDataFullHour = await mDb.MeterDataMonitoring
                    .Where(x => x.SmartMeterId == _meterDataMonitoringModel.SmartMeterId)
                    .OrderBy(x => x.MonitoringId)
                    .Include(x => x.Monitoring)
                    .FirstOrDefaultAsync(x => !x.Monitoring.IsCalculating); // automatically first because of deletion of unnecessary values

                if (meterDataFullHour != null && meterDataFullHour.ActiveEnergyPlus != null) {
                    var timeDifference = calculatingMonitoring.Timestamp - meterDataFullHour.Monitoring.Timestamp;
                    var energy = _meterDataMonitoringModel.ActiveEnergyPlus - meterDataFullHour.ActiveEnergyPlus;

                    var multiplier = 60.0 / (double)timeDifference.TotalMinutes;
                    var projected = (int)(energy * multiplier);
                    meterDataMontoring.ProjectedActiveEnergyPlus = projected;

                    var currentPortion = await mDistributionService.GetCurrentPortion(_meterDataMonitoringModel.SmartMeterId, true);
                    if (currentPortion != null) {
                        int forecast = currentPortion.EstimatedActiveEnergyPlus + currentPortion.Flexibility;
                        if (!IsGoodForecast(forecast, projected)) {
                            // bad forecast (non compliance of forecast)
                            meterDataMontoring.NonCompliance = true;

                            if (!currentPortion.SmartMeter.IsNonComplianceMuted && currentPortion.SumFeedIn > Constants.DISTRIBUTION_MINIMUM_ENERGY_WH) {
                                // only send notification if not muted and if feed-in over threshold
                                var fcmAndroidData = mFCMService.NonCompliance;
                                fcmAndroidData.BodyArgs = new List<string>() {
                                    currentPortion.SmartMeter.Name,
                                    (projected - forecast).ToString(),
                                    "Wh"
                                };
                                await mFCMService.SendPushNotificationMember(fcmAndroidData, currentPortion.SmartMeter.MemberId);
                            }
                        }
                    }
                }

                await mDb.SaveChangesAsync();
            }
        }

        public async Task<Performance> GetPerformance(Guid _smartMeterId, int _durationDays) {
            var performance = new Performance() {
                GoodForecastCount = 0,
                WrongForecasted = 0
            };

            var notBefore = DateTime.UtcNow
                .AddDays(-_durationDays);

            await mDb.SmartMeterPortion
                .Include(x => x.ECommunityDistribution)
                .Where(x => x.SmartMeterId == _smartMeterId && x.ECommunityDistribution.Timestamp > notBefore)
                .ForEachAsync(x => {
                    if (x.ActualActiveEnergyPlus != null) {
                        var forecast = x.EstimatedActiveEnergyPlus + x.Flexibility;
                        var actual = (int)x.ActualActiveEnergyPlus;
                        if (IsGoodForecast(forecast, actual)) {
                            performance.GoodForecastCount++;
                        }

                        performance.ForecastCount++;
                        performance.WrongForecasted += Math.Abs(forecast - actual);
                    }
                });

            return performance;
        }

        public async Task<List<MeterDataMonitoring>> GetRelevantMeterDataMonitorings(Guid _memberId) {
            var monitoring = await mDb.Monitoring
                .OrderByDescending(x => x.Id)
                .Include(x => x.MeterDataMonitorings)
                .ThenInclude(x => x.SmartMeter)
                .FirstOrDefaultAsync(x => !x.IsCalculating);

            return monitoring.MeterDataMonitorings
                .Where(x => x.SmartMeter.MemberId == _memberId && (x.NonCompliance || x.ActiveEnergyPlus == null ))
                .ToList();
        }

        public async Task MuteCurrentHour(Guid _smartMeterId) {
            var smartMeter = await mDb.SmartMeter
                .FirstOrDefaultAsync(x => x.Id == _smartMeterId);

            if (smartMeter != null) {
                smartMeter.IsNonComplianceMuted = true;
                await mDb.SaveChangesAsync();
            }
        }

        private bool IsGoodForecast(int _forecast, int _actual) {
            var min = _forecast * (1 - Constants.DISTRIBUTION_GOOD_FORECAST_PERCENT);
            var max = _forecast * (1 + Constants.DISTRIBUTION_GOOD_FORECAST_PERCENT);

            return _actual >= min && _actual <= max;
        }

        private async Task<List<ECommunityDistribution>> GetCurrentDistributions() {
            var nonCalulating = await mDb.ECommunityDistribution
                .Include(x => x.SmartMeterPortions)
                .ThenInclude(x => x.SmartMeter)
                .Where(x => !x.IsCalculating)
                .ToListAsync();

            var maxTimestamp = nonCalulating.Max(x => x.Timestamp);

            return nonCalulating
                .Where(x => x.Timestamp == maxTimestamp)
                .ToList();
        }

        private async Task<Monitoring> GetCalculatingMonitoring() {
            return await mDb.Monitoring
                .Include(x => x.MeterDataMonitorings)
                .ThenInclude(x => x.SmartMeter)
                .FirstOrDefaultAsync(x => x.IsCalculating);
        }
    }
}