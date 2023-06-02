using e_community_cloud_lib.Util.Enums;
using System;
using System.Collections.Generic;

namespace e_community_cloud_lib.LocalDtos
{
    /// <summary>
    /// table for storing the "users"
    /// a member can be a private person, organization, club, GmbH, ...
    /// </summary>
    public class LocalMemberDto
    {
        public Guid Id { get; set; }

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
        /// highest grid level of member
        /// </summary>
        public int GridLevel { get; set; }

        /// <summary>
        /// supply mode of member (how much energy to eCommunity pool)
        /// </summary>
        public SupplyMode SupplyMode { get; set; }

    }

}
