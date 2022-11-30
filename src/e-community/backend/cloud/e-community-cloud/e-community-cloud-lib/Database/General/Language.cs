using e_community_cloud_lib.Util.BaseClasses;
using System;
using System.Collections.Generic;

namespace e_community_cloud_lib.Database.General
{
    /// <summary>
    /// languages used for the text templates (current: german, english)
    /// <see cref="EntityBase"/>
    /// </summary>
    public class Language: EntityBase
    {
        /// <summary>
        /// short description of the language (German)
        /// </summary>
        public String Name { get; set; }

        /// <summary>
        /// list of members with this language
        /// </summary>
        public IList<Member> Members { get; set; }

        /// <summary>
        /// list of translations in this language
        /// </summary>
        public IList<Translation> Translations { get; set; }
    }
}
