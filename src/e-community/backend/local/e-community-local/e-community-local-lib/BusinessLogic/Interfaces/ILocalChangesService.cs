using e_community_local_lib.CloudData.Local;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_local_lib.BusinessLogic.Interfaces
{
    public interface ILocalChangesService
    {
        Task SmartMeterChanged(CloudSmartMeterDto _cloudSmartMeterDto);
    }
}
