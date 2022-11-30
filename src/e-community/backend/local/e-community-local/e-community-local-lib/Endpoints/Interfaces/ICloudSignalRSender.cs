using e_community_local_lib.CloudDtos;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace e_community_local_lib.Endpoints.Interfaces
{
    /// <summary>
    /// provided SignalR methods of cloud
    /// </summary>
    public interface ICloudSignalRSender
    {
        /// <summary>
        /// add to eCommunity group
        /// </summary>
        /// <param name="_smartMeterId">id of smart meter</param>
        Task AssignSmartMeterId(Guid _smartMeterId);

        /// <summary>
        /// cloud receives realtime data from Smart Meter
        /// </summary>
        /// <param name="_meterData">near real time meter data</param>
        Task ReceiveRTData(MeterDataRTDto _meterData);

        /// <summary>
        /// real time timer elapsed (end device must extend real time sessions after 5 minutes)
        /// </summary>
        Task TimerElapsed();
    }
}
