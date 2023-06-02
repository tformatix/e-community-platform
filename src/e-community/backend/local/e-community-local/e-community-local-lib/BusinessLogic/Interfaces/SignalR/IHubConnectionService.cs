using Microsoft.AspNetCore.SignalR.Client;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_local_lib.BusinessLogic.Interfaces.SignalR {
    public interface IHubConnectionService {
        /// <returns>the hub connection to the cloud</returns>
        Task<HubConnection> GetHubConnection();

        /// <returns>creates new hub connection</returns>
        Task<HubConnection> Reconnect();

        /// <summary>
        /// invokes a SignalR method on the cloud
        /// </summary>
        /// <param name="_method">method name</param>
        /// <returns></returns>
        Task InvokeSignalR(String _method);

        /// <summary>
        /// invokes a SignalR method on the cloud
        /// </summary>
        /// <param name="_method">method name</param>
        /// <param name="_object1">argument</param>
        /// <returns></returns>
        Task InvokeSignalR(String _method, object _object1);
    }
}
