using System;

namespace e_community_local.Dtos
{
    public class StatusDto
    {
        public Guid? MemberId { get; set; }
        public bool IsConnectedToInternet { get; set; }
        public bool IsWiredConnected { get;set; }
        public string WifiSSID { get; set; }
        public SmartMeterDto SmartMeter { get; set; }
    }
}
