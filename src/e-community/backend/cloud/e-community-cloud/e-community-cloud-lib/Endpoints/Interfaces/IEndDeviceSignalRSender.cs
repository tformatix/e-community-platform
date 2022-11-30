using e_community_cloud_lib.Endpoints.Dtos;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.Endpoints.Interfaces
{
    /// <summary>
    /// provided SignalR methods of End Device
    /// </summary>
    public interface IEndDeviceSignalRSender
    {
        /// <summary>
        /// cloud sends buffered real time data from smart meters to end device
        /// </summary>
        Task ReceiveRTData(BufferedMeterDataRTDto _meterData);
    }
}
