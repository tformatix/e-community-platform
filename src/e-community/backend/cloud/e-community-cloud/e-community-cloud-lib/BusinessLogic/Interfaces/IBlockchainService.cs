using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using e_community_cloud_lib.Database.General;
using e_community_cloud_lib.Database.Community;

namespace e_community_cloud_lib.BusinessLogic.Interfaces
{
    public interface IBlockchainService
    {
        /// <summary>
        /// returns the ethereum balance of member's account
        /// </summary>
        /// <param name="_memberId"></param>
        string GetAccountBalance(Guid _memberId);
    }
}