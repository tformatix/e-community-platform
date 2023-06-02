using Microsoft.AspNetCore.SignalR;
using Microsoft.Extensions.Hosting;
using SmartMeterAPI.Hubs;
using SmartMeterAPI.DTOs;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.Extensions.DependencyInjection;
using SmartMeterAPIDb;

namespace SmartMeterAPI.Services
{
    /// <summary>
    /// background service (sends data with SignalR)
    /// </summary>
    public class SmartMeterBackgroundService : BackgroundService
    {
        private readonly IHubContext<SmartMeterHub> hubContext;
        private IServiceProvider serviceProvider;
        private MeterDataDto<String> units;

        /// <summary>
        /// init background service --> ExecuteAsync() runs in background
        /// </summary>
        public SmartMeterBackgroundService(IHubContext<SmartMeterHub> hubContext, IServiceProvider serviceProvider)
        {
            this.hubContext = hubContext;
            this.serviceProvider = serviceProvider;
            this.units = InitUnits();
        }

        /// <summary>
        /// Initialize the units
        /// They remain the same because otherwise they would be displayed incorrectly in the diagram
        /// </summary>
        private MeterDataDto<String> InitUnits()
        {
            return new MeterDataDto<String>
            {
                ActiveEnergyPlus = Constants.ENERGY_UNIT,
                ActiveEnergyMinus = Constants.ENERGY_UNIT,
                ReactiveEnergyPlus = Constants.ENERGY_UNIT,
                ReactiveEnergyMinus = Constants.ENERGY_UNIT,
                ActivePowerPlus = Constants.POWER_UNIT,
                ActivePowerMinus = Constants.POWER_UNIT,
                ReactivePowerPlus = Constants.POWER_UNIT,
                ReactivePowerMinus = Constants.POWER_UNIT
            };
        }

        /// <summary>
        /// executed in background
        /// waits 1 min at the beginning (sync raspberry with smart meter SND_NKE)
        /// sends Real Time Data every second
        /// </summary>
        protected override async Task ExecuteAsync(CancellationToken stoppingToken)
        {
            var prevId = -1;
            using (var scope = serviceProvider.CreateScope())
            {
                var smartMeterService = scope.ServiceProvider.GetRequiredService<SmartMeterService>(); // get service
                String aesKey = $"AES{SmartMeterService.GetAESKey()}END"; // get AES Key for method
                await Task.Delay(60000); // wait 1 minute (SND NKE smart meter)
                Console.WriteLine($"--------SignalR::started {aesKey}--------");
                while (!stoppingToken.IsCancellationRequested) // loop
                {
                    try
                    {
                        if (SmartMeterHub.GetConnectionCount() > 0) // don't send data if nobody is connected
                        {
                            var meterDataValues = smartMeterService.GetLatestRealTime();
                            if (meterDataValues.ID != prevId) // don't send same entry twice
                            {
                                prevId = meterDataValues.ID;
                                var realTimeData = new MeterDataRTDto()
                                {
                                    Unit = units,
                                    MeterDataValues = meterDataValues.ParseMeterData()
                                };
                                await hubContext.Clients.All.SendAsync(aesKey, realTimeData); // SignalR
                            }
                        }
                    }
                    catch (Exception exc)
                    {
                        Console.WriteLine($"--------SignalR::RealTime table currently deleted (Error Msg: {exc.Message})--------"); // slave deletes database every 90 seconds
                    }
                    await Task.Delay(1000); // wait 1 second
                }
            }
        }
    }
}
