using e_community_local_lib.Endpoints.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace e_community_local_lib.Endpoints
{
    public interface ICloudBackgroundService : ICloudSignalRReceiver {
        Task ExecuteAsync(CancellationToken stoppingToken = default);
    }
}
