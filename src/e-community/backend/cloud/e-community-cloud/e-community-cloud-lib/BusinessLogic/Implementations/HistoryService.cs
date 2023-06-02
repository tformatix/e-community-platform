using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.Database;

namespace e_community_cloud_lib.BusinessLogic.Implementations;

public class HistoryService : IHistoryService
{
    private readonly ECommunityCloudContext mDb;

    public HistoryService(ECommunityCloudContext _db)
    {
        mDb = _db;
    }
}