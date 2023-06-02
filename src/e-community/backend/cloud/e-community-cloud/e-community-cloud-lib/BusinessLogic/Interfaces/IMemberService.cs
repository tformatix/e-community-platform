using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using e_community_cloud_lib.Database.General;
using e_community_cloud_lib.Database.Local;

namespace e_community_cloud_lib.BusinessLogic.Interfaces
{
    public interface IMemberService
    {
        /// <param name="_memberId">member id</param>
        /// <returns>member which has according member id</returns>
        Task<Member> GetMember(Guid _memberId);
    }
}
