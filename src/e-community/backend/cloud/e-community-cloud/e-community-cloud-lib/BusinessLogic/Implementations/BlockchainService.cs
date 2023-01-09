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
using e_community_cloud_lib.Models.Blockchain;

namespace e_community_cloud_lib.BusinessLogic.Implementations
{
    public class BlockchainService : IBlockchainService
    {
        private readonly ECommunityCloudContext mDb;

        public BlockchainService(ECommunityCloudContext _db)
        {
            mDb = _db;
        }
    }
}