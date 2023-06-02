using SmartMeterAPI.DTOs;
using SmartMeterAPIDb;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;

namespace SmartMeterAPI.Services
{
    /// <summary>
    /// reads database
    /// </summary>
    public class SmartMeterService
    {
        private const String CONFIG_PATH = "../slave/mbus-slave-ima-cpp-webdemo/config.ini";
        //private const String CONFIG_PATH = "C:\\Users\\tobias.fischer\\Documents\\00_Work\\Smart_Metering\\config.ini";
        private readonly SmartMeterAPIContext db;
        private static String AESKey = null;

        public SmartMeterService(SmartMeterAPIContext db)
        {
            this.db = db;
        }

        /// <summary>
        /// returns size of history table
        /// </summary>
        public int GetSizeHist() => db.METERDATA_HIST.Count();

        /// <summary>
        /// returns first entry of history table
        /// </summary>
        public METERDATA_HIST GetFirstEntryHist() => db.METERDATA_HIST
                                                        .OrderBy(x => x.ID)
                                                        .FirstOrDefault();

        /// <summary>
        /// returns last entry of history table
        /// </summary>
        public METERDATA_HIST GetLastEntryHist() => db.METERDATA_HIST
                                                      .OrderByDescending(x => x.ID) // SQL doesn't know LastOrDefault()
                                                      .FirstOrDefault();

        /// <summary>
        /// returns history data from database
        /// </summary>
        /// <param name="fromTimestamp">Begin Timestamp</param>
        /// <param name="toTimestamp">End Timestamp</param>
        /// <param name="timeResolution">Timeresolution - aggregates data (15min; 1440 min (1 day))</param>
        public List<METERDATA_HIST> GetHistoryData(DateTime fromTimestamp, DateTime toTimestamp, int timeResolution)
        {
            int timeStep = timeResolution / Constants.TIME_DIFF_HIST;

            // filter history table with given timestamps
            var dataHistory = db.METERDATA_HIST
                .ToList()
                .Where(x => x.TIMESTAMP.CompareTo(fromTimestamp) >= 0 && x.TIMESTAMP.CompareTo(toTimestamp) <= 0)
                .Where((x, i) => (i % timeStep == 0))
                .ToList();

            if (dataHistory.Count() == 0)
                return null;

            return CalcEnergyDiffHist(dataHistory);
        }

        /// <summary>
        /// energy (Zählerstand) should be substracted from the prev energy (Zählerstand) 
        /// </summary>
        /// <param name="dataHistory">filtered history list</param>
        private List<METERDATA_HIST> CalcEnergyDiffHist(List<METERDATA_HIST> dataHistory)
        {
            // clone list
            var clonedHistory = dataHistory
                .Select(x => x.CopyPropertiesTo(new METERDATA_HIST()))
                .ToList();
            // calc differences
            METERDATA_HIST cloned, data;
            for (int i = 1; i < dataHistory.Count(); i++)
            {
                cloned = clonedHistory[i];
                data = dataHistory[i - 1];
                cloned.ID = i; // ID ascending
                cloned.ACTIVE_ENERGY_PLUS -= data.ACTIVE_ENERGY_PLUS;
                cloned.ACTIVE_ENERGY_MINUS -= data.ACTIVE_ENERGY_MINUS;
                cloned.REACTIVE_ENERGY_PLUS -= data.REACTIVE_ENERGY_PLUS;
                cloned.REACTIVE_ENERGY_MINUS -= data.REACTIVE_ENERGY_MINUS;
            }
            clonedHistory.RemoveAt(0); // delete first entry --> needed for diferences
            return clonedHistory;
        }

        /// <summary>
        /// returns latest entry of real time table
        /// </summary>
        public METERDATA_RT GetLatestRealTime()
        {
            var first = db.METERDATA_RT
                     .OrderByDescending(x => x.ID) // SQL doesn't know LastOrDefault()
                     .FirstOrDefault();
            db.Entry(first).Reload(); // reload entry --> Entity Framework caches data
            return first;
        }

        /// <summary>
        /// returns AES Key
        /// </summary>
        public static String GetAESKey()
        {
            if (AESKey == null)
            {
                ReadAESKey();
            }
            return AESKey ?? "";
        }

        /// <summary>
        /// read AES Key from config.ini
        /// </summary>
        private static void ReadAESKey()
        {
            foreach (String line in File.ReadAllLines(CONFIG_PATH))
            {
                if (line.StartsWith("aeskey = \""))
                {
                    AESKey = line.Substring(10, 15 * 3 + 2).Replace(" ", "");
                    Console.WriteLine($"--------ReadAESKey()::without spaces {AESKey}--------");
                    break;
                }
            }

        }
    }
}
