using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using SmartMeterAPI.DTOs;
using SmartMeterAPI.Services;
using SmartMeterAPIDb;

namespace SmartMeterAPI.Controllers
{
    [Route("[controller]/[action]")]
    [ApiController]
    public class SmartMeterController : ControllerBase
    {
        private readonly SmartMeterService smartMeterService;
        public SmartMeterController(SmartMeterService smartMeterService)
        {
            this.smartMeterService = smartMeterService;
        }

        /// <summary>
        /// check connection with backend
        /// </summary>
        /// <returns>OK with "Hello!"</returns>
        [HttpGet]
        [ProducesResponseType(StatusCodes.Status200OK)]
        public IActionResult CheckConnection()
        {
            Console.WriteLine("--------GET::CheckConnection()--------");
            return Ok("Hello!");
        }

        /// <summary>
        /// Information for setting up the history view
        /// </summary>
        /// <remarks>
        /// - LatestTimestamp = earliest Timestamp in Database (FromTimestamp)
        /// - MaxTimeResolution = Time-Difference between latest and earliest Dates in Minutes (TimeResolution)
        /// - InitTimestamp = one month before OR LatestTimestamp (if less than a month)
        /// </remarks>
        /// <param name="key">AES Key without spaces</param>
        /// <response code="200">ok</response> 
        /// <response code="500">caught exception</response> 
        /// <response code="555">faulty database</response> 
        /// <returns>Setup Information</returns>
        [HttpGet("{key}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        [ProducesResponseType(555)]
        public IActionResult HistorySetup(String key)
        {
            Console.WriteLine("--------GET::HistorySetup()--------");
            if(!key.Equals(SmartMeterService.GetAESKey())) // check if same AES Key
                return GetActionResult(StatusCodes.Status400BadRequest, "Wrong AES Key");
            try
            {
                if (smartMeterService.GetSizeHist() <= 0)
                    return GetActionResult(Constants.DB_ERROR_CODE, "Database empty"); // self defined Database Error
            }
            catch (Exception)
            {
                return GetActionResult(Constants.DB_ERROR_CODE, "Database not available"); // Database Error
            }
            try
            {
                var firstEntry = smartMeterService.GetFirstEntryHist();
                var lastEntry = smartMeterService.GetLastEntryHist();
                var timeDifference = lastEntry.TIMESTAMP.Subtract(firstEntry.TIMESTAMP);
                var initTimestamp = firstEntry.TIMESTAMP;
                if (timeDifference.TotalDays > 30)
                {
                    initTimestamp = lastEntry.TIMESTAMP.AddMonths(-1);
                }

                return Ok(new HistSetupDto()
                {
                    LatestTimestamp = firstEntry.TIMESTAMP,
                    MaxTimeResolution = Convert.ToInt32(timeDifference.TotalMinutes),
                    InitTimestamp = initTimestamp
                });
            }
            catch (Exception exc)
            {
                return GetActionResult(StatusCodes.Status500InternalServerError, exc.ToString());
            }
        }

        /// <summary>
        /// History Data
        /// </summary>
        /// <remarks>
        /// - filtered by timestamp and time resolution
        /// - difference between energy (Zählerstand) calculated
        /// - min/max/avg/unit calculated
        /// </remarks>
        /// <param name="historyDto">AES Key without spaces, From/To Timestamp and Timeresolution in min</param>
        /// <returns>history data</returns>
        /// <response code="200">ok</response> 
        /// <response code="400">faulty user input</response> 
        /// <response code="500">caught exception</response> 
        /// <response code="555">faulty database</response> 
        [HttpPost]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        [ProducesResponseType(555)]
        public IActionResult History([FromBody] HistoryDto historyDto)
        {
            Console.WriteLine("--------GET::History()--------");

            #region Error Handling
            if (!historyDto.AesKey.Equals(SmartMeterService.GetAESKey())) // check if same AES Key
                return GetActionResult(StatusCodes.Status400BadRequest, "Wrong AES Key");
            try
            {
                if (smartMeterService.GetSizeHist() <= 0)
                    return GetActionResult(Constants.DB_ERROR_CODE, "Database empty"); // self defined Database Error
            }
            catch (Exception)
            {
                return GetActionResult(Constants.DB_ERROR_CODE, "Database not available"); // Database Error
            }
            if (historyDto.FromTimestamp.CompareTo(historyDto.ToTimestamp) >= 0)
                return GetActionResult(StatusCodes.Status400BadRequest, "Inconsistent Dates (From >= To)");
            var firstEntry = smartMeterService.GetFirstEntryHist();
            if (firstEntry.TIMESTAMP.Year == Constants.AES_KEY_WRONG_YEAR)
                return GetActionResult(Constants.DB_ERROR_CODE, "AES Key is wrong (change Key and delete Database)"); // Database Error
            if (historyDto.FromTimestamp.CompareTo(firstEntry.TIMESTAMP) < 0)
                return GetActionResult(StatusCodes.Status400BadRequest, "FromTimestamp to early");
            if (historyDto.TimeResolution < Constants.TIME_DIFF_HIST)
                return GetActionResult(StatusCodes.Status400BadRequest, "TimeResolution is lower than 15");
            if (historyDto.ToTimestamp.Subtract(historyDto.FromTimestamp).TotalMinutes < historyDto.TimeResolution)
                return GetActionResult(StatusCodes.Status400BadRequest, "TimeResolution doesn't fit in time difference");
            #endregion
            try
            {
                var meterDataValues = smartMeterService.GetHistoryData(historyDto.FromTimestamp, historyDto.ToTimestamp, historyDto.TimeResolution);
                if (meterDataValues == null)
                    return GetActionResult(StatusCodes.Status400BadRequest, "No data available in this period");
                return Ok(CalcParams(new MeterDataHistDto()
                {
                    MeterDataValues = meterDataValues
                        .Select(x => x.ParseMeterData())
                        .ToList(),
                    Max = new MeterDataDto<double>(),
                    Min = new MeterDataDto<double>(),
                    Avg = new MeterDataDto<double>(),
                    Unit = new MeterDataDto<string>()
                }));
            }
            catch (Exception exc)
            {
                return GetActionResult(StatusCodes.Status500InternalServerError, exc.ToString()); // Internal Server Error
            }
        }

        #region Params
        /// <summary>
        /// calc params (min,max,avg,unit)
        /// </summary>
        private MeterDataHistDto CalcParams(MeterDataHistDto meterData)
        {
            CalcMax(meterData);
            CalcMin(meterData);
            CalcAvg(meterData);
            CalcUnit(meterData);

            return meterData;
        }

        private void CalcMax(MeterDataHistDto meterData)
        {
            meterData.Max.ActiveEnergyPlus = meterData.MeterDataValues.Max(x => x.ActiveEnergyPlus);
            meterData.Max.ActiveEnergyMinus = meterData.MeterDataValues.Max(x => x.ActiveEnergyMinus);
            meterData.Max.ReactiveEnergyPlus = meterData.MeterDataValues.Max(x => x.ReactiveEnergyPlus);
            meterData.Max.ReactiveEnergyMinus = meterData.MeterDataValues.Max(x => x.ReactiveEnergyMinus);
            meterData.Max.ActivePowerPlus = meterData.MeterDataValues.Max(x => x.ActivePowerPlus);
            meterData.Max.ActivePowerMinus = meterData.MeterDataValues.Max(x => x.ActivePowerMinus);
            meterData.Max.ReactivePowerPlus = meterData.MeterDataValues.Max(x => x.ReactivePowerPlus);
            meterData.Max.ReactivePowerMinus = meterData.MeterDataValues.Max(x => x.ReactivePowerMinus);
        }

        private void CalcMin(MeterDataHistDto meterData)
        {
            meterData.Min.ActiveEnergyPlus = meterData.MeterDataValues.Min(x => x.ActiveEnergyPlus);
            meterData.Min.ActiveEnergyMinus = meterData.MeterDataValues.Min(x => x.ActiveEnergyMinus);
            meterData.Min.ReactiveEnergyPlus = meterData.MeterDataValues.Min(x => x.ReactiveEnergyPlus);
            meterData.Min.ReactiveEnergyMinus = meterData.MeterDataValues.Min(x => x.ReactiveEnergyMinus);
            meterData.Min.ActivePowerPlus = meterData.MeterDataValues.Min(x => x.ActivePowerPlus);
            meterData.Min.ActivePowerMinus = meterData.MeterDataValues.Min(x => x.ActivePowerMinus);
            meterData.Min.ReactivePowerPlus = meterData.MeterDataValues.Min(x => x.ReactivePowerPlus);
            meterData.Min.ReactivePowerMinus = meterData.MeterDataValues.Min(x => x.ReactivePowerMinus);
        }

        private void CalcAvg(MeterDataHistDto meterData)
        {
            meterData.Avg.ActiveEnergyPlus = Convert.ToInt32(meterData.MeterDataValues.Average(x => x.ActiveEnergyPlus));
            meterData.Avg.ActiveEnergyMinus = Convert.ToInt32(meterData.MeterDataValues.Average(x => x.ActiveEnergyMinus));
            meterData.Avg.ReactiveEnergyPlus = Convert.ToInt32(meterData.MeterDataValues.Average(x => x.ReactiveEnergyPlus));
            meterData.Avg.ReactiveEnergyMinus = Convert.ToInt32(meterData.MeterDataValues.Average(x => x.ReactiveEnergyMinus));
            meterData.Avg.ActivePowerPlus = Convert.ToInt32(meterData.MeterDataValues.Average(x => x.ActivePowerPlus));
            meterData.Avg.ActivePowerMinus = Convert.ToInt32(meterData.MeterDataValues.Average(x => x.ActivePowerMinus));
            meterData.Avg.ReactivePowerPlus = Convert.ToInt32(meterData.MeterDataValues.Average(x => x.ReactivePowerPlus));
            meterData.Avg.ReactivePowerMinus = Convert.ToInt32(meterData.MeterDataValues.Average(x => x.ReactivePowerMinus));
        }

        private void CalcUnit(MeterDataHistDto meterData)
        {
            if (meterData.Avg.ActiveEnergyPlus >= 1000)
            {
                meterData.Avg.ActiveEnergyPlus /= 1000;
                meterData.Max.ActiveEnergyPlus /= 1000;
                meterData.Min.ActiveEnergyPlus /= 1000;
                meterData.MeterDataValues.ForEach(x => x.ActiveEnergyPlus /= 1000);
                meterData.Unit.ActiveEnergyPlus = $"{Constants.PREFIX_1000}{Constants.ENERGY_UNIT}";
            }
            else
                meterData.Unit.ActiveEnergyPlus = Constants.ENERGY_UNIT;

            if (meterData.Avg.ActiveEnergyMinus >= 1000)
            {
                meterData.Avg.ActiveEnergyMinus /= 1000;
                meterData.Max.ActiveEnergyMinus /= 1000;
                meterData.Min.ActiveEnergyMinus /= 1000;
                meterData.MeterDataValues.ForEach(x => x.ActiveEnergyMinus /= 1000);
                meterData.Unit.ActiveEnergyMinus = $"{Constants.PREFIX_1000}{Constants.ENERGY_UNIT}";
            }
            else
                meterData.Unit.ActiveEnergyMinus = Constants.ENERGY_UNIT;

            if (meterData.Avg.ReactiveEnergyPlus >= 1000)
            {
                meterData.Avg.ReactiveEnergyPlus /= 1000;
                meterData.Max.ReactiveEnergyPlus /= 1000;
                meterData.Min.ReactiveEnergyPlus /= 1000;
                meterData.MeterDataValues.ForEach(x => x.ReactiveEnergyPlus /= 1000);
                meterData.Unit.ReactiveEnergyPlus = $"{Constants.PREFIX_1000}{Constants.ENERGY_UNIT}";
            }
            else
                meterData.Unit.ReactiveEnergyPlus = Constants.ENERGY_UNIT;

            if (meterData.Avg.ReactiveEnergyMinus >= 1000)
            {
                meterData.Avg.ReactiveEnergyMinus /= 1000;
                meterData.Max.ReactiveEnergyMinus /= 1000;
                meterData.Min.ReactiveEnergyMinus /= 1000;
                meterData.MeterDataValues.ForEach(x => x.ReactiveEnergyMinus /= 1000);
                meterData.Unit.ReactiveEnergyMinus = $"{Constants.PREFIX_1000}{Constants.ENERGY_UNIT}";
            }
            else
                meterData.Unit.ReactiveEnergyMinus = Constants.ENERGY_UNIT;

            if (meterData.Avg.ActivePowerPlus >= 1000)
            {
                meterData.Avg.ActivePowerPlus /= 1000;
                meterData.Max.ActivePowerPlus /= 1000;
                meterData.Min.ActivePowerPlus /= 1000;
                meterData.MeterDataValues.ForEach(x => x.ActivePowerPlus /= 1000);
                meterData.Unit.ActivePowerPlus = $"{Constants.PREFIX_1000}{Constants.POWER_UNIT}";
            }
            else
                meterData.Unit.ActivePowerPlus = Constants.POWER_UNIT;

            if (meterData.Avg.ActivePowerMinus >= 1000)
            {
                meterData.Avg.ActivePowerMinus /= 1000;
                meterData.Max.ActivePowerMinus /= 1000;
                meterData.Min.ActivePowerMinus /= 1000;
                meterData.MeterDataValues.ForEach(x => x.ActivePowerMinus /= 1000);
                meterData.Unit.ActivePowerMinus = $"{Constants.PREFIX_1000}{Constants.POWER_UNIT}";
            }
            else
                meterData.Unit.ActivePowerMinus = Constants.POWER_UNIT;

            if (meterData.Avg.ReactivePowerPlus >= 1000)
            {
                meterData.Avg.ReactivePowerPlus /= 1000;
                meterData.Max.ReactivePowerPlus /= 1000;
                meterData.Min.ReactivePowerPlus /= 1000;
                meterData.MeterDataValues.ForEach(x => x.ReactivePowerPlus /= 1000);
                meterData.Unit.ReactivePowerPlus = $"{Constants.PREFIX_1000}{Constants.POWER_UNIT}";
            }
            else
                meterData.Unit.ReactivePowerPlus = Constants.POWER_UNIT;

            if (meterData.Avg.ReactivePowerMinus >= 1000)
            {
                meterData.Avg.ReactivePowerMinus /= 1000;
                meterData.Max.ReactivePowerMinus /= 1000;
                meterData.Min.ReactivePowerMinus /= 1000;
                meterData.MeterDataValues.ForEach(x => x.ReactivePowerMinus /= 1000);
                meterData.Unit.ReactivePowerMinus = $"{Constants.PREFIX_1000}{Constants.POWER_UNIT}";
            }
            else
                meterData.Unit.ReactivePowerMinus = Constants.POWER_UNIT;
        }
        #endregion

        /// <summary>
        /// Action Result is printed to console and returned afterwards
        /// </summary>
        /// <param name="status">Statuscode</param>
        /// <param name="message">Return-Message</param>
        /// <returns>Action Result</returns>
        private IActionResult GetActionResult(int status, String message)
        {
            var error = $"--------ERROR (Status {status} | Date {DateTime.Now.ToShortDateString()} {DateTime.Now.ToShortTimeString()})::{message}--------";
            System.IO.File.AppendAllText("smartmeter_api.log", $"{error}\n");
            Console.Error.WriteLine(error);
            return StatusCode(status, message);
        }

    }
}

