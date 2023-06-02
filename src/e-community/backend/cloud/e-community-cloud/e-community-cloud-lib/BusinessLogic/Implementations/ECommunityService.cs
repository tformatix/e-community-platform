using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.Database;
using e_community_cloud_lib.Database.Community;
using e_community_cloud_lib.Database.General;
using e_community_cloud_lib.Util;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.BusinessLogic.Implementations {
    public class ECommunityService : IECommunityService {
        private readonly ECommunityCloudContext mDb;

        public ECommunityService( ECommunityCloudContext _db) {
            mDb = _db;
        }

        public async Task<ECommunity> GetECommunity(Guid _memberId) {
            var eCommunityId = mDb.GetECommunityId(_memberId);

            if(eCommunityId == null) return null;

            var memberships = await mDb.ECommunityMembership
                .Where(x => x.ECommunityId == eCommunityId && Constants.ACTIVE_MEMBER_PERMISSIONS.Contains(x.ECommunityPermission))
                .Include(x => x.ECommunity)
                .Include(x => x.Member)
                .ToListAsync();

            return memberships[0].ECommunity;
        }
    }
}
