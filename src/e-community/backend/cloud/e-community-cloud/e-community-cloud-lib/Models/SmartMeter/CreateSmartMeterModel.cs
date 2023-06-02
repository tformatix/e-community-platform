using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.Models.SmartMeter
{
    public class CreateSmartMeterModel
    {
        public string RefreshToken { get; set; }
        public string Name { get; set; }
        public string Description { get; set; }
        public bool IsMain { get; set; }
        public bool MeasuresConsumption { get; set; }
        public bool MeasuresFeedIn { get; set; }
        public bool IsDirectFeedIn { get; set; }
        public bool IsOverflowFeedIn { get; set; }
    }
}
