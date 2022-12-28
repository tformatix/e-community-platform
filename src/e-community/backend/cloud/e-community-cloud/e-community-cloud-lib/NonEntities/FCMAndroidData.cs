using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.NonEntities {
    public class FCMAndroidData {
        public string TitleKey { get; set; }
        public List<string> TitleArgs { get; set; }
        public string BodyKey { get; set; }
        public List<string> BodyArgs { get; set; }
    }
}
