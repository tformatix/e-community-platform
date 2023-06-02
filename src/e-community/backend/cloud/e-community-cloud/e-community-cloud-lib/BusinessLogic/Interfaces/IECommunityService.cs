using e_community_cloud_lib.Database.Community;
using e_community_cloud_lib.Database.General;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.BusinessLogic.Interfaces {
    public interface IECommunityService {
        /// <param name="_memberId">member id</param>
        /// <returns>eComminty according to member id (null if not available)</returns>
        Task<ECommunity> GetECommunity(Guid _memberId);
    }
}
