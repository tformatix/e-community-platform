using e_community_cloud_lib.Database.Community;
using e_community_cloud_lib.Database.Local;
using e_community_cloud_lib.Models.Distribution;
using e_community_cloud_lib.NonEntities;
using System;
using System.Threading.Tasks;

namespace e_community_cloud_lib.BusinessLogic.Interfaces {

    /// <summary>
    /// handles dynamic energy distribution in eCommunities
    /// </summary>
    public interface IDistributionService { 
        /// <summary>
        /// start distribution: prepare database for next hour's distribution (10 minutes before full hour)
        /// </summary>
        /// <param name="_timestamp">current timestamp</param>
        Task StartDistribution(DateTime _timestamp);

        /// <summary>
        /// hourly forecast from local device (smart meter) arrived
        /// </summary>
        /// <param name="_forecastModel">hourly forecast</param>
        Task ForecastArrived(ForecastModel _forecastModel);

        /// <summary>
        /// distribute energy of every eCommunity between their members (5 minutes before full hour)
        /// </summary>
        Task Distribute();

        /// <summary>
        /// distribute energy of specific eCommunity between its members
        /// </summary>
        /// <param name="_eCommunityDistribution"></param>
        Task Distribute(ECommunityDistribution _eCommunityDistribution);

        /// <summary>
        /// member acknowledged its flexibility for a smart meter
        /// </summary>
        /// <param name="_portionAckModel">acknowledged flexibility</param>
        Task PortionAck(PortionAckModel _portionAckModel);

        /// <summary>
        /// finalizes distribution (full hour)
        /// </summary>
        Task FinalizeDistribution();

        /// <param name="_smartMeterId">id of smart meter</param>
        /// <param name="_includeSmartMeter">also join smart meter from db</param>
        /// <returns>current portion for a smart meter</returns>
        Task<CurrentPortion> GetCurrentPortion(Guid _smartMeterId, bool _includeSmartMeter = false);

        /// <param name="_memberId">id of member</param>
        /// <returns>new distribution (possible between five minutes before full hour and full hour: e.g. 11:55-12:00)</returns>
        Task<NewDistribution> GetNewDistribution(Guid _memberId);

    }
}
