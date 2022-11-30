using e_community_local_lib.Models;
using e_community_local_lib.NonEntities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_local_lib.BusinessLogic.Interfaces
{
    public interface IPairingService
    {
        Task<Status> CurrentStatus();

        /// <summary>
        /// calls a python script which add a wifi network to the wpa_supplicant config file
        /// restart wpa_supplicant and check if connection is a success
        /// </summary>
        Task NetworkAdd(NetworkConnectModel _networkConnectModel);

        /// <summary>
        /// checks which wifi network are available for the loca device (raspberry)
        /// </summary>
        List<NetworkDiscoveryModel> NetworkDiscovery();

        Task CloudConnect(CloudConnectModel _cloudConnectModel);
    }
}
