using e_community_cloud_lib.LocalDtos;

namespace e_community_cloud.Dtos
{
    public class CreateSmartMeterDto
    {
        public string RefreshToken { get; set; }
        public LocalSmartMeterDto SmartMeter { get; set; }
    }
}
