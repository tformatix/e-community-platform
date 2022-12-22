using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.BusinessLogic.Interfaces {
    public interface IDistributionService { 
        Task Distribute(Guid _eCommunityId);
    }
}
