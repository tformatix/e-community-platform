using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using e_community_cloud_lib.Database.General;
using e_community_cloud_lib.Database.Community;

namespace e_community_cloud_lib.BusinessLogic.Interfaces
{
    public interface ISearchService
    {
        /// <summary>
        /// searches for users which match the search pattern
        /// </summary>
        List<Member> SearchForUsers(string _query);

        /// <summary>
        /// searches for eCommunities which match the search pattern
        /// </summary>
        List<ECommunity> SearchForEComms(string _query);
    }
}
