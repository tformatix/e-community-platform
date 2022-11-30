using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartMeterAPI.DTOs
{
    public class HistSetupDto
    {
        public DateTime LatestTimestamp { get; set; }
        public int MaxTimeResolution { get; set; }
        public DateTime InitTimestamp { get; set; } // one month before OR LatestTimestamp
    }
}
