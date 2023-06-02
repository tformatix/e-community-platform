using System;

namespace e_community_local_lib.CloudData;

public class RequestHistoryModel
{
    public Guid RequestedMemberId { get; set; }
    public DateTime FromTimestamp { get; set; }
    public DateTime ToTimestamp { get; set; }
    public int TimeResolution { get; set; }
}