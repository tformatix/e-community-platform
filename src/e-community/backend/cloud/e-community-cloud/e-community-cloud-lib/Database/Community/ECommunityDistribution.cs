using e_community_cloud_lib.Util.BaseClasses;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.Database.Community {
    public class ECommunityDistribution {
        public int Id { get; set; }

        /// <summary>
        /// eCommunity (part of composite primary key)
        /// </summary>
        public Guid ECommunityId { get; set; }
        public ECommunity ECommunity { get; set; }

        /// <summary>
        /// date and time of the distribution
        /// </summary>
        public DateTime Timestamp { get; set; }

        /// <summary>
        /// list of estimated energy portions for smart meters
        /// </summary>
        public IList<SmartMeterPortion> SmartMeterPortions { get; set; }
    }
}
