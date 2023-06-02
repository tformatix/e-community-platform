using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.Models.Distribution
{
    public class PortionAckModel
    {
        public Guid SmartMeterId { get; set; }

        /// <summary>
        /// acknowledged flexibility
        /// </summary>
        public int Flexibility { get; set; }
    }
}
