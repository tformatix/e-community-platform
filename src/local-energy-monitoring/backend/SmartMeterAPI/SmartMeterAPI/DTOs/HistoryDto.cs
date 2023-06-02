using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartMeterAPI.DTOs
{
    public class HistoryDto
    {
        public String AesKey { get; set; }
        public DateTime FromTimestamp { get; set; }
        public DateTime ToTimestamp { get; set; }
        public int TimeResolution { get; set; }
    }
}
