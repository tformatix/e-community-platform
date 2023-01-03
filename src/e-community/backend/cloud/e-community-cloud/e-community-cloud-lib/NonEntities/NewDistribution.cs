using e_community_cloud_lib.Database.Local;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.NonEntities
{
    public class NewDistribution {
        public List<SmartMeterPortion> SmartMeterPortions { get; set; }
        public int UnassignedActiveEnergyMinus { get; set; }
        public int MissingSmartMeterCount { get; set; }
    }
}
