using e_community_cloud_lib.Database.Local;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.NonEntities {
    public class CurrentPortion {
        public int EstimatedActiveEnergyPlus { get; set; }
        public int Flexibility { get; set; }
        public int Deviation { get; set; }
        public int UnassignedActiveEnergyMinus { get; set; }
        public int MissingSmartMeterCount { get; set; }
        public bool IsRelevant { get; set; }
        public SmartMeter SmartMeter { get; set; }
    }
}
