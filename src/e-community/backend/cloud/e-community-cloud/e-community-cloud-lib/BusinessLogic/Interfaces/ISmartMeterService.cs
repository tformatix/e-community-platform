using e_community_cloud_lib.Database.Local;
using e_community_cloud_lib.Models.SmartMeter;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.BusinessLogic.Interfaces
{
    public interface ISmartMeterService
    {
        /// <summary>
        /// fetchs all smart meters belonging to the member
        /// </summary>
        /// <returns>list of smart meters</returns>
        Task<List<SmartMeter>> GetSmartMeters(Guid _memberId);

        /// <summary>
        /// updates the smart meter object
        /// </summary>
        /// <param name="_updateSmartMeterModel">update model</param>
        /// <returns>async operation</returns>
        Task Update(UpdateSmartMeterModel _updateSmartMeterModel);
    }
}
