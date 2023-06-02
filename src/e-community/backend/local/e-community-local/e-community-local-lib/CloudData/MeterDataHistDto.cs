using System.Collections.Generic;
using e_community_local_lib.Database.Meter;

namespace e_community_local_lib.CloudData
{
    public class MeterDataHistDto
    {
        public MeterDataBase Unit { get; set; }
        public MeterDataBase Min { get; set; }
        public MeterDataBase Avg { get; set; }
        public MeterDataBase Max { get; set; }
        public List<MeterDataBase> MeterDataValues { get; set; }
    }
}
