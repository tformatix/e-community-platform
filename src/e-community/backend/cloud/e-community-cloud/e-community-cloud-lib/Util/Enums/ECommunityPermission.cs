namespace e_community_cloud_lib.Util.Enums
{
    /// <summary>
    /// every member of an eCommunity has a role (like admin, normal member, pending, ...)
    /// </summary>
    public enum ECommunityPermission
    {
        Admin,
        Member,
        Pending,
        Denied,
        Former
    }
}
