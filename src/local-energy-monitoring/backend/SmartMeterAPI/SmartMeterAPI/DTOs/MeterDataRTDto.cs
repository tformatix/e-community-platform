using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartMeterAPI.DTOs
{
    public class MeterDataRTDto
    {
        public MeterDataDto<string> Unit { get; set; }
        public MeterDataDto<double> MeterDataValues { get; set; }
    }
}
