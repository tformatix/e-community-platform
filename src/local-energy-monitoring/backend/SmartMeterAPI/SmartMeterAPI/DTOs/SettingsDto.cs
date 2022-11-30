using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartMeterAPI.DTOs
{
    public class SettingsDto
    {
        public String Name { get; set; }
        public String IPRaspberry { get; set; }
        public String AESKey { get; set; }
    }
}
