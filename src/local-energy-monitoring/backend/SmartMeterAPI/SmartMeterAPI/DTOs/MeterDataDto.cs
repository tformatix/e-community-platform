using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartMeterAPI.DTOs
{
    public class MeterDataDto<T>
    {
        public int Id { get; set; }
        public DateTime Timestamp { get; set; }
        public T ActiveEnergyPlus { get; set; }
        public T ActiveEnergyMinus { get; set; }
        public T ReactiveEnergyPlus { get; set; }
        public T ReactiveEnergyMinus { get; set; }
        public T ActivePowerPlus { get; set; }
        public T ActivePowerMinus { get; set; }
        public T ReactivePowerPlus { get; set; }
        public T ReactivePowerMinus { get; set; }
    }
}
