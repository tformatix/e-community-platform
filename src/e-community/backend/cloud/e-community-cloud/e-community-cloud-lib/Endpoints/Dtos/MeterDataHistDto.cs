using System.Collections.Generic;
using e_community_cloud_lib.Models.History;

namespace e_community_cloud_lib.Endpoints.Dtos
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
