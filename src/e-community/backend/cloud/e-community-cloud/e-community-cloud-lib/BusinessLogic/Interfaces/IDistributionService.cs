using e_community_cloud_lib.Database.Community;
using e_community_cloud_lib.Models.Distribution;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.BusinessLogic.Interfaces {
    public interface IDistributionService { 
        Task StartDistribution();

        Task ForecastArrived(ForecastModel _forecastModel);

        Task StartMonitorSession();

        Task Distribute();

        Task Distribute(ECommunityDistribution _eCommunityDistribution);

    }
}
