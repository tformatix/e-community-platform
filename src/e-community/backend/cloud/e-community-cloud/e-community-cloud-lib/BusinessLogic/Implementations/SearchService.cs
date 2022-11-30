using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.BusinessLogic.Interfaces.SignalR;
using e_community_cloud_lib.Database;
using e_community_cloud_lib.Database.Local;
using e_community_cloud_lib.Models;
using e_community_cloud_lib.Util.Extensions;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using e_community_cloud_lib.Database.Community;
using e_community_cloud_lib.Database.General;

namespace e_community_cloud_lib.BusinessLogic.Implementations
{
    public class SearchService : ISearchService
    {
        private readonly ECommunityCloudContext mDb;

        public SearchService(ECommunityCloudContext _db)
        {
            mDb = _db;
        }

        public List<Member> SearchForUsers(string _query)
        {
            var foundMembers = mDb.Member.Where(
                x => x.UserName.ToLower().Contains(_query.ToLower())
            );

            return foundMembers.ToList();
        }

        public List<ECommunity> SearchForEComms(string _query)
        {
            var foundMembers = mDb.ECommunity.Where(
                x => x.Name.ToLower().Contains(_query.ToLower())
            );

            return foundMembers.ToList();
        }
    }
}
