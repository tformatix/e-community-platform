using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_local_lib.CloudData.Local
{
    public class CloudLocalDto
    {
        public List<CloudBatterySystemDto> BatterySystems { get; set; }
        public List<CloudChargeDto> Charges { get; set; }
        public CloudECommunityDto ECommunity { get; set; }
        public CloudGridPriceRateDto GridPriceRate { get; set; }
        public CloudMemberDto Member { get; set; }
        public List<CloudPVSystemDto> PVSystems { get; set; }
        public CloudSmartMeterDto SmartMeter { get; set; }
        public CloudSupplierPriceRateDto SupplierPriceRate { get; set; }
    }
}
