using e_community_cloud_lib.Database.Local;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.BusinessLogic.Interfaces
{
    public interface IReplacementValueService {
        SmartMeterPortion GetReplacementValue(SmartMeterPortion _smartMeterPortion);
    }
}
