using e_community_local_lib.Database;
using e_community_local_lib.Endpoints;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.OpenApi.Models;
using e_community_local_lib.BusinessLogic.Interfaces;
using e_community_local_lib.BusinessLogic.Implementations;
using e_community_local_lib.BusinessLogic.Interfaces.SignalR;
using e_community_local_lib.BusinessLogic.Implementations.SignalR;
using e_community_local_lib.BusinessLogic.Interfaces.REST;
using e_community_local_lib.BusinessLogic.Implementations.REST;

namespace e_community_local
{
    public class Startup
    {
        public Startup(IConfiguration _configuration)
        {
            Configuration = _configuration;
        }

        public IConfiguration Configuration { get; }

        // This method gets called by the runtime. Use this method to add services to the container.
        public void ConfigureServices(IServiceCollection _services)
        {
            AddDatabase(_services);
            AddServices(_services);

            _services.AddSignalR();
            _services.AddControllers();
            _services.AddHostedService<CloudBackgroundService>();
            _services.AddHealthChecks();
            _services.AddHttpClient();

            _services.AddCors(_options =>
            {
                _options.AddDefaultPolicy(_builder =>
                {
                    _builder.AllowAnyOrigin().AllowAnyHeader().AllowAnyMethod();
                });
            });

            _services.AddSwaggerGen(_setup =>
            {
                _setup.SwaggerDoc("v1",
                new OpenApiInfo
                {
                    Title = "e-community-local",
                    Version = "v1",
                });
                _setup.CustomSchemaIds(x => x.Name);
            });
        }

        private void AddDatabase(IServiceCollection _services)
        {
            _services.AddDbContext<ECommunityLocalContext>(_options =>
                _options.UseSqlite(Configuration.GetConnectionString("e-community-local-db")));

        }

        private void AddServices(IServiceCollection _services)
        {
            _services.AddSingleton<IHubConnectionService, HubConnectionService>();
            _services.AddScoped<ICloudBackgroundService, CloudBackgroundService>();
            _services.AddScoped<ICloudSignalRSenderService, CloudSignalRSenderService>();
            _services.AddScoped<ICloudRESTService, CloudRESTService>();
            _services.AddScoped<IPairingService, PairingService>();
            _services.AddScoped<ILocalChangesService, LocalChangesService>();
            _services.AddScoped<IForecastService, ForecastService>();
            _services.AddScoped<IMeterDataService, MeterDataService>();
        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(IApplicationBuilder _app, IWebHostEnvironment _env)
        {
            _app.UseHttpsRedirection();
            _app.UseHsts();

            _app.UseSwagger();
            _app.UseSwaggerUI(_c => _c.SwaggerEndpoint("/swagger/v1/swagger.json", "e-community-local v1"));

            _app.UseExceptionHandler("/Error");

            _app.UseRouting();
            _app.Use(async (_context, _next) =>
            {
                _context.Response.Headers.Add("X-Powered-By", $"e-community-local v{Program.Version}");
                await _next.Invoke();
            });
            _app.UseAuthentication();
            _app.UseAuthorization();

            _app.UseCors();

            _app.UseEndpoints(_endpoints =>
            {
                _endpoints.MapControllers();
            });
        }
    }
}
