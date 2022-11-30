using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.Database;
using e_community_cloud_lib.Database.General;
using e_community_cloud_lib.Database.Local;
using Microsoft.AspNetCore.Identity;

namespace e_community_cloud_lib.BusinessLogic.Implementations 
{
    /// <summary>
    /// <seealso cref="IMemberService"/>
    /// </summary>
    public class MemberService : IMemberService
    {
        private readonly ECommunityCloudContext mDb;
        private readonly UserManager<Member> mUserManager;

        public MemberService(UserManager<Member> _userManager, ECommunityCloudContext _db)
        {
            mDb = _db;
            mUserManager = _userManager;
        }

        public Member GetMinimalMember(Guid _memberId) {
            var member = mDb.Member.SingleOrDefault(x => x.Id == _memberId);
            return member;
        }
    }
}