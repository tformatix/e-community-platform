using e_community_cloud_lib.Database.Community;
using e_community_cloud_lib.Database.Local;
using e_community_cloud_lib.Models.Distribution;
using e_community_cloud_lib.NonEntities;
using System;
using System.Threading.Tasks;

namespace e_community_cloud_lib.BusinessLogic.Interfaces {
    public interface IDistributionService { 
        Task StartDistribution(DateTime _timestamp);

        Task ForecastArrived(ForecastModel _forecastModel);

        Task Distribute();

        Task Distribute(ECommunityDistribution _eCommunityDistribution);

        Task PortionAck(PortionAckModel _portionAckModel);

        Task FinalizeDistribution();

        Task<CurrentPortion> GetCurrentPortion(Guid _smartMeterId);

        Task<SmartMeterPortion> GetCurrentSmartMeterPortion(Guid _smartMeterId, bool IncludeSmartMeter);

        Task<NewDistribution> GetNewDistribution(Guid _memberId);

    }
}
