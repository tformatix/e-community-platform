using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.LocalDtos
{
    public class LocalDto
    {
        public List<LocalBatterySystemDto> BatterySystems { get; set; }
        public List<LocalChargeDto> Charges { get; set; }
        public LocalECommunityDto ECommunity { get; set; }
        public LocalGridPriceRateDto GridPriceRate { get; set; }
        public LocalMemberDto Member { get; set; }
        public List<LocalPVSystemDto> PVSystems { get; set; }
        public LocalSmartMeterDto SmartMeter { get; set; }
        public LocalSupplierPriceRateDto SupplierPriceRate { get; set; }
    }
}
