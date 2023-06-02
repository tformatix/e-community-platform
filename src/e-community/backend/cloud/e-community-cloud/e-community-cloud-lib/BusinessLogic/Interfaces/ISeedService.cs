using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.BusinessLogic.Interfaces
{
    public interface ISeedService
    {
        /// <summary>
        /// add default values to the general tables (e_community_type, language, legal_form)
        /// </summary>
        void AddGeneralTables();

        /// <summary>
        /// delete values of the general tables (e_community_type, language, legal_form)
        /// </summary>
        void RemoveGeneralTables();
    }
}
