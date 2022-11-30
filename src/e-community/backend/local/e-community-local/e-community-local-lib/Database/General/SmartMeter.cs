using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_local_lib.Database.General
{
    /// <summary>
    /// representation of the local (raspberry) device
    /// </summary>
    public class SmartMeter
    {
        public Guid Id { get; set; }

        /// <summary>
        /// AES Key of Smart Meter
        /// </summary>
        public string AESKey { get; set; }

        /// <summary>
        /// API Key of Smart Meter
        /// </summary>
        public string APIKey { get; set; }

        /// <summary>
        /// name of the device
        /// </summary>
        public string Name { get; set; }

        /// <summary>
        /// short description of the device
        /// </summary>
        public string Description { get; set; }

        /// <summary>
        /// marks the main raspberry (only one weather calculation per household)
        /// </summary>
        public bool IsMain { get; set; }

        /// <summary>
        /// device measures energy consumption
        /// </summary>
        public bool MeasuresConsumption { get; set; }

        /// <summary>
        /// device measures energy feed in
        /// </summary>
        public bool MeasuresFeedIn { get; set; }

        /// <summary>
        /// everything of the energy feed in goes directly back to the power grid
        /// </summary>
        public bool IsDirectFeedIn { get; set; }

        /// <summary>
        /// just the overflow of the energy feed in goes back to the power grid
        /// </summary>
        public bool IsOverflowFeedIn { get; set; }

        /// <summary>
        /// each change of data relevant for local devices (Raspberry) gets its own id
        /// </summary>
        public Guid LocalStorageId { get; set; }
    }
}
