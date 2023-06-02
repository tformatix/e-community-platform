using e_community_cloud_lib.Util;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Serilog;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace e_community_cloud
{
    public class Program
    {
        public const string Version = "0.1";

        public static void Main(string[] _args)
        {
            LoggerUtils.ConfigureLogger();
            Log.Information("Program::Starting cloud backend...");
            CreateHostBuilder(_args).Build().Run();
            Log.CloseAndFlush();
        }

        public static IHostBuilder CreateHostBuilder(string[] _args) =>
            Host.CreateDefaultBuilder(_args)
                .UseSerilog()
                .ConfigureWebHostDefaults(_webBuilder =>
                {
                    _webBuilder.UseStartup<Startup>();
                });
    }
}
