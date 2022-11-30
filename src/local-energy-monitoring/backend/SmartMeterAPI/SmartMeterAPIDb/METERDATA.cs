using System;
using System.Collections.Generic;
using System.Text;

namespace SmartMeterAPIDb
{
    public abstract class METERDATA
    {
        public int ID { get; set; }
        public DateTime TIMESTAMP { get; set; }
        public double ACTIVE_ENERGY_PLUS { get; set; }
        public double ACTIVE_ENERGY_MINUS { get; set; }
        public double REACTIVE_ENERGY_PLUS { get; set; }
        public double REACTIVE_ENERGY_MINUS { get; set; }
        public double ACTIVE_POWER_PLUS { get; set; }
        public double ACTIVE_POWER_MINUS { get; set; }
        public double REACTIVE_POWER_PLUS { get; set; }
        public double REACTIVE_POWER_MINUS { get; set; }
        public double PREPAYMENT_COUNTER { get; set; }
    }
}
