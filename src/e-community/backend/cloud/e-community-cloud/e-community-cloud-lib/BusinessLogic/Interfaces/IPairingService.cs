using e_community_cloud_lib.Database.Local;
using e_community_cloud_lib.Models.SmartMeter;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.BusinessLogic.Interfaces
{
    public interface IPairingService
    {
        /// <summary>
        /// creates a new smart meter object
        /// </summary>
        /// <param name="_createSmartMeterModel">contains attributes for smart meter</param>
        /// <param name="_memberId">id of member</param>
        /// <returns>database object of smart meter</returns>
        Task<SmartMeter> CreateSmartMeter(CreateSmartMeterModel _createSmartMeterModel, Guid _memberId);

        /// <param name="_smartMeterId">id of smart meter</param>
        /// <returns>all data which is relevant for local usage (on raspberry)</returns>
        Task<SmartMeter> GetLocalData(Guid _smartMeterId);
    }
}
