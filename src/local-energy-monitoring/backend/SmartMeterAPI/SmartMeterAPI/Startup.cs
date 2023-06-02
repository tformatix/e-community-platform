using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;
using Microsoft.OpenApi.Models;
using SmartMeterAPI.Hubs;
using SmartMeterAPI.Services;
using SmartMeterAPIDb;

namespace SmartMeterAPI
{
    public class Startup
    {
        private readonly string myAllowSpecificOrigins = "_myAllowSpecificOrigins";

        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
        }

        public IConfiguration Configuration { get; }

        // This method gets called by the runtime. Use this method to add services to the container.
        public void ConfigureServices(IServiceCollection services)
        {
            services.AddDbContext<SmartMeterAPIContext>(options => options.UseSqlite(Configuration.GetConnectionString("SmartMeterAPI")));
            services.AddScoped<SmartMeterService>();
            services.AddSignalR();
            services.AddHostedService<SmartMeterBackgroundService>();
            services.AddSwaggerGen(c =>
            {
                c.SwaggerDoc("v1", new OpenApiInfo { 
                    Title = "Smart Meter API", 
                    Description = "<b>SignalR:</b> <i>/SmartMeter/RealTime</i> -- Entry Method = \"AES{AES-Key with no spaces}END\" Returning MeterDataRTDto -- Get real time data every second.",
                    Version = "v1" });
                var xmlFile = $"{Assembly.GetExecutingAssembly().GetName().Name}.xml";
                var xmlPath = Path.Combine(AppContext.BaseDirectory, xmlFile);
                c.IncludeXmlComments(xmlPath);
            });
            services.AddCors(options =>
            {
                options.AddPolicy(myAllowSpecificOrigins,
                builder =>
                {
                    builder.AllowAnyOrigin();
                    builder.AllowAnyHeader();
                    builder.AllowAnyMethod();
                });
            });
            services.AddMvc(options => options.EnableEndpointRouting = false);
        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(IApplicationBuilder app, IHostingEnvironment env)
        {
            if (env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
            }

            app.UseSwagger();
            app.UseSwaggerUI(c =>
            {
                c.SwaggerEndpoint("/swagger/v1/swagger.json", "Smart Meter API v1");
                c.DefaultModelsExpandDepth(-1);
            });

            app.UseCors(myAllowSpecificOrigins);

            app.UseMvc();

            //app.UseEndpoints(endpoints =>
            //{
            //    endpoints.MapHub<SmartMeterHub>("/SmartMeter/RealTime");
            //});
            app.UseSignalR(routes =>
            {
                routes.MapHub<SmartMeterHub>("/SmartMeter/RealTime");
            });
        }
    }
}
