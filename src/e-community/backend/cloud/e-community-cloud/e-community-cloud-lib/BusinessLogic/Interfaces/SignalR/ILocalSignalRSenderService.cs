using e_community_cloud_lib.Models.SmartMeter;
using e_community_cloud_lib.NonEntities;
using e_community_cloud_lib.Util;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;
using e_community_cloud_lib.Models.Blockchain;

namespace e_community_cloud_lib.BusinessLogic.Interfaces.SignalR
{
    /// <summary>
    /// SignalR sender to the Smart Meter (send something to a Smart Meter)
    /// </summary>
    public interface ILocalSignalRSenderService
    {
        /// <summary>
        /// request realtime data from smart meters (for 5 minutes)
        /// </summary>
        void RequestRTData(Guid? _memberId);

        /// <summary>
        /// request realtime data for member with missing smart meters
        /// </summary>
        void RequestRTDataFaulty(string _signalRGroupName);

        /// <summary>
        /// real time data connection should be extended (every 5 minutes)
        /// </summary>
        void ExtendRTData(Guid? _memberId);

        /// <summary>
        /// stop realtime data stream from smart meter
        /// </summary>
        void StopRTData(Guid? _memberId);

        /// <summary>
        /// updates smart meter in database
        /// </summary>
        /// <param name="_updateSmartMeterModel">changes to smart meter</param>
        void UpdateSmartMeter(UpdateSmartMeterModel _updateSmartMeterModel);

        /// <summary>
        /// cloud requests hourly forecast data (load profile and flexibility)
        /// </summary>
        void RequestHourlyForecast();

        /// <summary>
        /// cloud requests meter data for monitoring
        /// </summary>
        void RequestMeterDataMonitoring();

        /// <summary>
        /// cloud requests information about the local ethereum account (balances)
        /// </summary>
        void RequestBlockchainAccountBalance(Guid? _memberId);

        /// <summary>
        /// creates a new consent contract on the blockchain
        /// </summary>
        /// <param name="_memberId"></param>
        /// <param name="_consentContractModel"></param>
        void CreateConsentContract(Guid? _memberId, ConsentContractModel _consentContractModel);
    }
}
