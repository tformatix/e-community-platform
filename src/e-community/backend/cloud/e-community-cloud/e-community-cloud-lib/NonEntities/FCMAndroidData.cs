using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.NonEntities {

    /// <summary>
    /// FCM notification data for Android
    /// </summary>
    public class FCMAndroidData {
        /// <summary>
        /// stored in payload of notification 
        /// used in messaging service to find out the notification's context (e.g. reload eCommunity screen if Id = "e_community")
        /// </summary>
        public string Id { get; set; }

        /// <summary>
        /// Android's string ressources key of title
        /// </summary>
        public string TitleKey { get; set; }

        /// <summary>
        /// (optional) Android's string ressources arguments of title
        /// </summary>
        public List<string> TitleArgs { get; set; }

        /// <summary>
        /// Android's string ressources key of body
        /// </summary>
        public string BodyKey { get; set; }

        /// <summary>
        /// (optional)  Android's string ressources arguments of body
        /// </summary>
        public List<string> BodyArgs { get; set; }
    }
}
