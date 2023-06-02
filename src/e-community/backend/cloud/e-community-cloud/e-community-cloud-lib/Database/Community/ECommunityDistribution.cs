using e_community_cloud_lib.Database.Local;
using e_community_cloud_lib.Util.BaseClasses;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.Database.Community
{
    /// <summary>
    /// energy distribution at a specific hour for an eCommunity
    /// </summary>
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
        /// is this distribution currently calculated
        /// </summary>
        public bool IsCalculating { get; set; }

        /// <summary>
        /// was this distribution already distributed
        /// </summary>
        public bool WasDistributed { get; set; }

        /// <summary>
        /// is this distribution relevant (enough feed-in)
        /// </summary>
        public bool IsRelevant { get; set; }

        /// <summary>
        /// list of estimated energy portions for smart meters
        /// </summary>
        public IList<SmartMeterPortion> SmartMeterPortions { get; set; }
    }
}
