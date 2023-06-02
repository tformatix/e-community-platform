using e_community_local_lib.Util.BusinessLogic;
using Serilog;
using Serilog.Events;
using System;

namespace e_community_local_lib.Util;

public class LoggerUtils {

    private const string OUTPUT_TEMPLATE = "[{Timestamp:dd.MM.yyyy, HH:mm:ss} {Level:u3}] {Message:lj}{NewLine}{Exception}";

    public static void ConfigureLogger() {
        Log.Logger = new LoggerConfiguration()
            .MinimumLevel.Debug()
            .MinimumLevel.Override("Microsoft", LogEventLevel.Error)
            .Filter.ByExcluding(logEvent =>
                logEvent.Exception != null && logEvent.Exception.GetType().IsAssignableTo(typeof(ServiceException)))
            .Enrich.FromLogContext()
            .WriteTo.Console(
                outputTemplate: OUTPUT_TEMPLATE
             )
            .WriteTo.File(
                "log.txt",
                fileSizeLimitBytes: 1_000_000,
                rollOnFileSizeLimit: true,
                shared: true,
                flushToDiskInterval: TimeSpan.FromSeconds(1),
                outputTemplate: OUTPUT_TEMPLATE
            )
            .CreateLogger();
    }

}