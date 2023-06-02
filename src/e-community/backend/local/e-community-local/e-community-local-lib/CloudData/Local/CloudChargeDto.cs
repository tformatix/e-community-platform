using System;
using System.Collections.Generic;

namespace e_community_local_lib.CloudData.Local
{
    /// <summary>
    /// some charges a member has to do (e.g. electricity charge, green electricity, ...)
    /// </summary>
    public class CloudChargeDto
    {
        public Guid Id { get; set; }

        /// <summary>
        /// base rate of the charge (in €)
        /// </summary>
        public double BaseRate { get; set; }

        /// <summary>
        /// working price for 1 Wh
        /// </summary>
        public double WorkingPricePlus { get; set; }

        /// <summary>
        /// tax rate for the charge (in %)
        /// </summary>
        public double TaxRate { get; set; }

        /// <summary>
        /// flag if it is necesarry for the eCommunity
        /// </summary>
        public bool ApplyToECommunity { get; set; }
    }
}
