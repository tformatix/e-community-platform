using e_community_cloud_lib.Database.General;
using e_community_cloud_lib.Util.BaseClasses;
using System.Collections.Generic;

namespace e_community_cloud_lib.Database.Local
{
    /// <summary>
    /// used for checking if everything is ok. We check if we find something strange with energy data (compared to the MeterDataProfile)
    /// <see cref="EntityBase"/>
    /// </summary>
    public class EventCase: EntityBase
    {
        /// <summary>
        /// name of event case
        /// </summary>
        public string Name { get; set; }

        /// <summary>
        /// priority of event case
        /// </summary>
        public int Priority { get; set; }

        /// <summary>
        /// list of translations which are using this event case
        /// </summary>
        public IList<Translation> Translation { get; set; }

        /// <summary>
        /// list of meter data where this event case appears
        /// </summary>
        public IList<MeterDataProfile> MeterDataProfiles { get; set; }
    }
}
