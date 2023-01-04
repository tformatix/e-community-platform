using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.BusinessLogic.Interfaces.SignalR;
using e_community_cloud_lib.Database;
using e_community_cloud_lib.Database.Community;
using e_community_cloud_lib.Database.General;
using e_community_cloud_lib.Database.Local;
using e_community_cloud_lib.Models.Distribution;
using e_community_cloud_lib.Util;
using Microsoft.EntityFrameworkCore;
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
                        Acknowledged = false
                    })
                );
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
                        .FirstOrDefaultAsync(x => x.Timestamp.Hour == previousTimestamp.Hour && x.Timestamp.Minute == previousTimestamp.Minute);

                    if (monitoringHourBefore != null) {
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

                    var currentPortion = await mDistributionService.GetCurrentSmartMeterPortion(_meterDataMonitoringModel.SmartMeterId, true);
                    if (currentPortion != null) {
                        if (!IsGoodForecast(currentPortion.EstimatedActiveEnergyPlus, currentPortion.Flexibility, projected)) {
                            // bad forecast (non compliance of forecast)
                            meterDataMontoring.NonCompliance = true;

                            var fcmAndroidData = mFCMService.NonCompliance;
                            fcmAndroidData.BodyArgs = new List<string>() {
                                currentPortion.SmartMeter.Name,
                                (projected - currentPortion.EstimatedActiveEnergyPlus).ToString(),
                                "Wh"
                            };
                            await mFCMService.SendPushNotificationMember(fcmAndroidData, currentPortion.SmartMeter.MemberId);
                        }
                    }
                }

                await mDb.SaveChangesAsync();
            }
        }

        private bool IsGoodForecast(int _forecast, int _flexibility, int _actual) {
            var min = (_forecast + _flexibility) * (1 - Constants.DISTRIBUTION_GOOD_FORECAST_PERCENT);
            var max = (_forecast + _flexibility) * (1 + Constants.DISTRIBUTION_GOOD_FORECAST_PERCENT);

            return _actual >= min && _actual <= max;
        }

        private async Task<List<ECommunityDistribution>> GetCurrentDistributions() {
            var nonCalulating = await mDb.ECommunityDistribution
                .OrderByDescending(x => x.Id)
                .Include(x => x.SmartMeterPortions)
                .ThenInclude(x => x.SmartMeter)
                .Where(x => !x.IsCalculating)
                .ToListAsync();

            var maxId = nonCalulating.Max(x => x.Id);

            return nonCalulating
                .Where(x => x.Id == maxId)
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