using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartMeterAPI.DTOs
{
    public class MeterDataHistDto
    {
        public MeterDataDto<string> Unit { get; set; }
        public MeterDataDto<double> Min { get; set; }
        public MeterDataDto<double> Avg { get; set; }
        public MeterDataDto<double> Max { get; set; }
        public List<MeterDataDto<double>> MeterDataValues { get; set; }
    }
}
