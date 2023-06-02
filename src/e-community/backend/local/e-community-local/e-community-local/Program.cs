using e_community_local_lib.Util;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Hosting;
using Serilog;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace e_community_local
{
    public class Program
    {
        public const string Version = "0.1";

        public static void Main(string[] _args)
        {
            LoggerUtils.ConfigureLogger();
            Log.Information("Program::Starting local backend...");
            CreateHostBuilder(_args).Build().Run();
            Log.CloseAndFlush();
        }

        public static IHostBuilder CreateHostBuilder(string[] args) =>
        Host.CreateDefaultBuilder(args)
            .ConfigureWebHostDefaults(webBuilder =>
            {
                webBuilder.UseStartup<Startup>();
            });
    }
}
