using Microsoft.AspNetCore.SignalR;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartMeterAPI.Hubs
{
    /// <summary>
    /// entrypoint for listeners
    /// The SignalR Hubs API enables you to call methods on connected clients from the server
    /// </summary>
    public class SmartMeterHub : Hub
    {
        private static List<string> Connections = new List<string>(); // connected clients

        public override Task OnConnectedAsync()
        {
            Connections.Add(Context.ConnectionId);
            Console.WriteLine($"--------SignalR::client connected {Context.ConnectionId} [count: {GetConnectionCount()}]--------");
            return base.OnConnectedAsync();
        }

        public override Task OnDisconnectedAsync(Exception exception)
        {
            Connections.Remove(Context.ConnectionId);
            Console.WriteLine($"--------SignalR::client disconnected {Context.ConnectionId} [count: {GetConnectionCount()}]--------");
            return base.OnDisconnectedAsync(exception);
        }

        /// <summary>
        /// amount of connected clients
        /// </summary>
        public static int GetConnectionCount()
        {
            return Connections.Count();
        }
    }
}
