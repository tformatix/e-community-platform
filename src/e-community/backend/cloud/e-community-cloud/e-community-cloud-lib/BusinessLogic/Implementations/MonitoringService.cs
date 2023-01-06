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
using System.Net.NetworkInformation;
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
                        ProjectedActiveEnergyMinus = null,
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

                if (_timestamp.Minute == Constants.DISTRIBUTION_MONITOR_INTERVAL_MINUTES) {
                    // 5 minutes after full hour (e.g. 12:05): store actual energy into portions and delete all monitoring data except current
                    await FivePastFullHour(calculatingMonitoring);
                }
                await CheckMonitoring(calculatingMonitoring, _timestamp);

                await mDb.SaveChangesAsync();
            }
        }

        private async Task FivePastFullHour(Monitoring _monitoring) {
            var previousTimestamp = _monitoring.Timestamp.AddHours(-1);
            var monitoringHourBefore = await mDb.Monitoring
                .Include(x => x.MeterDataMonitorings)
                .OrderBy(x => x.Id)
                .FirstOrDefaultAsync();

            if (monitoringHourBefore != null && monitoringHourBefore.Timestamp.Hour == previousTimestamp.Hour && monitoringHourBefore.Timestamp.Minute == previousTimestamp.Minute) {
                // monitoring entry before an hour available

                var currentDistributions = await GetCurrentDistributions();
                foreach (var distribution in currentDistributions) {
                    foreach (var portion in distribution.SmartMeterPortions) {
                        var currentMeterData = _monitoring.MeterDataMonitorings
                            .FirstOrDefault(x => x.SmartMeterId == portion.SmartMeterId);
                        var meterDataHourBefore = monitoringHourBefore.MeterDataMonitorings
                            .FirstOrDefault(x => x.SmartMeterId == portion.SmartMeterId);

                        if (_monitoring != null && meterDataHourBefore != null &&
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
                    .Where(x => x.Id != _monitoring.Id)
            );
        }

        private async Task CheckMonitoring(Monitoring _monitoring, DateTime _timestamp) {
            foreach (var meterDataMonitoring in _monitoring.MeterDataMonitorings) {
                if (meterDataMonitoring.ProjectedActiveEnergyPlus == null) {
                    // offline -> send notification
                    var fcmAndroidData = mFCMService.Offline;
                    fcmAndroidData.BodyArgs = new List<string>() { meterDataMonitoring.SmartMeter.Name };
                    await mFCMService.SendPushNotificationMember(fcmAndroidData, meterDataMonitoring.SmartMeter.MemberId);
                }
                else if (_timestamp.Minute > 0 && _timestamp.Minute < (60 - 2 * Constants.DISTRIBUTION_MONITOR_INTERVAL_MINUTES)) {
                    // online -> check forecast deviation (only between 12:05 and 12:45)
                    var currentPortion = await mDistributionService.GetCurrentPortion(meterDataMonitoring.SmartMeterId, true);

                    if (currentPortion != null && currentPortion.IsRelevant) {
                        int forecast = GetForecastValue(currentPortion.EstimatedActiveEnergyPlus, currentPortion.Flexibility, currentPortion.Deviation);

                        if (!IsGoodForecast(forecast, (int)meterDataMonitoring.ProjectedActiveEnergyPlus)) {
                            // bad forecast (non compliance of forecast)
                            meterDataMonitoring.NonCompliance = true;

                            if (!currentPortion.SmartMeter.IsNonComplianceMuted) {
                                // only send notification if not muted and if feed-in over threshold
                                var deviation = (meterDataMonitoring.ProjectedActiveEnergyPlus ?? 0) - forecast;
                                var fcmAndroidData = (deviation > 0) ? mFCMService.NonComplianceMore : mFCMService.NonComplianceLess;
                                fcmAndroidData.BodyArgs = new List<string>() {
                                    currentPortion.SmartMeter.Name,
                                    Formatter.formatMeterData(deviation, true)
                                };

                                await mFCMService.SendPushNotificationMember(fcmAndroidData, currentPortion.SmartMeter.MemberId);
                            }
                        }
                    }
                }
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
                        .Where(x => x.SmartMeterId == meterDataMontoring.SmartMeterId)
                        .OrderBy(x => x.MonitoringId)
                        .Include(x => x.Monitoring)
                        .FirstOrDefaultAsync(x => !x.Monitoring.IsCalculating); // automatically first because of deletion of unnecessary values

                if (meterDataFullHour != null && meterDataFullHour.ActiveEnergyPlus != null) {
                    var timeDifference = meterDataMontoring.Monitoring.Timestamp - meterDataFullHour.Monitoring.Timestamp;

                    var multiplier = 60.0 / (double)timeDifference.TotalMinutes;
                    var projectedPlus = (int)((meterDataMontoring.ActiveEnergyPlus - meterDataFullHour.ActiveEnergyPlus) * multiplier);
                    var projectedMinus = (int)((meterDataMontoring.ActiveEnergyMinus - meterDataFullHour.ActiveEnergyMinus) * multiplier);

                    meterDataMontoring.ProjectedActiveEnergyPlus = projectedPlus;
                    meterDataMontoring.ProjectedActiveEnergyMinus = projectedMinus;
                }

                await mDb.SaveChangesAsync();
            }
        }

        public async Task<Performance> GetPerformance(Guid _smartMeterId, int _durationDays) {
            var performance = new Performance() {
                GoodForecastCount = 0,
                WrongForecasted = 0
            };

            var notBefore = (_durationDays >= 0) ? DateTime.UtcNow.AddDays(-_durationDays) : DateTime.MinValue;

            await mDb.SmartMeterPortion
                .Include(x => x.ECommunityDistribution)
                .ThenInclude(x => x.SmartMeterPortions)
                .Where(x => x.ECommunityDistribution.IsRelevant &&
                    x.ActualActiveEnergyPlus != null &&
                    x.SmartMeterId == _smartMeterId &&
                    x.ECommunityDistribution.Timestamp > notBefore
                )
                .ForEachAsync(x => {
                    var forecast = GetForecastValue(x.EstimatedActiveEnergyPlus, x.Flexibility, x.Deviation);
                    var actual = (int)x.ActualActiveEnergyPlus;
                    if (IsGoodForecast(forecast, actual)) {
                        performance.GoodForecastCount++;
                    }

                    performance.ForecastCount++;
                    performance.WrongForecasted += Math.Abs(forecast - actual);
                });

            return performance;
        }

        public async Task<List<MonitoringStatus>> GetMonitoringStatuses(Guid _memberId) {
            var monitoring = await mDb.Monitoring
                .OrderByDescending(x => x.Id)
                .Include(x => x.MeterDataMonitorings)
                .ThenInclude(x => x.SmartMeter)
                .FirstOrDefaultAsync(x => !x.IsCalculating);

            if (monitoring == null) {
                return null;
            }

            var meterDataMonitoring = monitoring.MeterDataMonitorings
                .Where(x => x.SmartMeter.MemberId == _memberId && (x.NonCompliance || x.ProjectedActiveEnergyPlus == null))
                .ToList();

            if (meterDataMonitoring.Count == 0) {
                return null;
            }

            var distribution = await mDb.ECommunityDistribution
                .Include(x => x.SmartMeterPortions)
                .ThenInclude(x => x.SmartMeter)
                .OrderByDescending(x => x.Id)
                .FirstOrDefaultAsync(x => x.IsRelevant && !x.IsCalculating && x.SmartMeterPortions.Any(portion => portion.SmartMeter.MemberId == _memberId));

            if (distribution == null) {
                return null;
            }

            return meterDataMonitoring
                .Select(x => {
                    var portion = distribution.SmartMeterPortions
                        .FirstOrDefault(p => p.SmartMeterId == x.SmartMeterId);
                    if (portion != null) {
                        return new MonitoringStatus() {
                            MeterDataMonitoring = x,
                            SmartMeterPortion = portion,
                            Forecast = GetForecastValue(portion.EstimatedActiveEnergyPlus, portion.Flexibility, portion.Deviation)
                        };
                    }
                    return null;
                })
                .ToList();
        }

        public async Task ToggleMuteCurrentHour(Guid _smartMeterId) {
            var smartMeter = await mDb.SmartMeter
                .FirstOrDefaultAsync(x => x.Id == _smartMeterId);

            if (smartMeter != null) {
                smartMeter.IsNonComplianceMuted = !smartMeter.IsNonComplianceMuted;
                await mDb.SaveChangesAsync();
            }
        }

        private static bool IsGoodForecast(int _forecast, int _actual) {
            var min = _forecast * (1 - Constants.DISTRIBUTION_GOOD_FORECAST_PERCENT);
            var max = _forecast * (1 + Constants.DISTRIBUTION_GOOD_FORECAST_PERCENT);

            return _actual >= min && _actual <= max;
        }

        private static int GetForecastValue(int _estimated, int _flexibility, int _deviation) {
            var forecast = _estimated;
            if (Math.Abs(_deviation) > Math.Abs(_flexibility)) {
                // |deviation| > |flexibility| --> not optimizable with flexibility
                // should comply flexibility
                forecast += _flexibility;
            }
            else {
                // |deviation| <= |flexibility| --> deviation in flexibility range
                // should comply deviation
                forecast += _deviation;
            }
            return forecast;
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