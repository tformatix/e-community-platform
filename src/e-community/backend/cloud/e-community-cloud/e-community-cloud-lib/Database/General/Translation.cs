using e_community_cloud_lib.Database.Local;
using System;

namespace e_community_cloud_lib.Database.General
{
    /// <summary>
    /// prepared text templates for the client (e.g.: Your energy consumption is lower than yesterday (5,5%))
    /// </summary>
    public class Translation
    {
        /// <summary>
        /// event case of translation (part of composite primary key)
        /// <seealso cref="EventCase"/>
        /// </summary>
        public Guid EventCaseId { get; set; }
        public EventCase EventCase { get; set; }

        /// <summary>
        /// language of translation (part of composite primary key)
        /// <seealso cref="Language"/>
        /// </summary>
        public Guid LanguageId { get; set; }
        public Language Language { get; set; }


        /// <summary>
        /// text of translation
        /// </summary>
        public string Text { get; set; }

    }
}
