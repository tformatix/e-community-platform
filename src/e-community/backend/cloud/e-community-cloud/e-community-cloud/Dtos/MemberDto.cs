using e_community_cloud_lib.Util.Enums;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace e_community_cloud.Dtos
{
    public class MemberDto {
        public Guid Id { get; set; }

        public string UserName { get; set; }
        public string Email { get; set; }

        /// <summary>
        /// street number of residence
        /// </summary>
        public string StreetNr { get; set; }

        /// <summary>
        /// zip code of residence
        /// </summary>
        public string ZipCode { get; set; }

        /// <summary>
        /// city name of residence
        /// </summary>
        public string CityName { get; set; }

        /// <summary>
        /// country code (e.g. AT) of residence
        /// </summary>
        public string CountryCode { get; set; }

        /// <summary>
        /// is email address public to other users
        /// </summary>
        public bool IsEmailPublic { get; set; }

        /// <summary>
        /// id of the transformer (Trafo)
        /// </summary>
        public int TransformerId { get; set; }

        /// <summary>
        /// id of the substation (Umspannwerk)
        /// </summary>
        public int SubstationId { get; set; }

        /// <summary>
        /// highest grid level of member
        /// </summary>
        public int GridLevel { get; set; }

        /// <summary>
        /// supply mode of member (how much energy to eCommunity pool)
        /// </summary>
        public SupplyMode? SupplyMode { get; set; }
    }
}
