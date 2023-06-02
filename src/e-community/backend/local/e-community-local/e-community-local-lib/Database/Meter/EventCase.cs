using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_local_lib.Database.Meter
{
    /// <summary>
    /// used for checking if everything is ok. We check if we find something strange with energy data (compared to the MeterDataProfile)
    /// </summary>
    public class EventCase
    {
        public Guid Id { get; set; }

        /// <summary>
        /// name of event case
        /// </summary>
        public string Name { get; set; }

        /// <summary>
        /// priority of event case
        /// </summary>
        public int Priority { get; set; }

        /// <summary>
        /// list of meter data where this event case appears
        /// </summary>
        public IList<MeterDataHistory> MeterDataHistories { get; set; }
    }
}
