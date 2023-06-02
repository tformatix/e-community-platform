using e_community_cloud_lib.Database.Local;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.BusinessLogic.Interfaces
{
    /// <summary>
    /// creates replacement values (if values are missing)
    /// </summary>
    public interface IReplacementValueService {

        /// <param name="_smartMeterPortion">portion for which replacement values are required</param>
        /// <returns>dummy replacement values (last available smart meter portion)</returns>
        SmartMeterPortion GetReplacementValue(SmartMeterPortion _smartMeterPortion);
    }
}
