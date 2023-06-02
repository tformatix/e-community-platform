using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi.Models;
using System;
using System.Collections.Generic;
using System.Text;
using e_community_cloud_lib.Database.General;
using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.BusinessLogic.Implementations;
using e_community_cloud_lib.Endpoints;
using e_community_cloud_lib.Database;
using e_community_cloud_lib.BusinessLogic.Interfaces.SignalR;
using e_community_cloud_lib.BusinessLogic.Implementations.SignalR;
using FirebaseAdmin;
using Google.Apis.Auth.OAuth2;
using System.IO;
using Newtonsoft.Json;
using e_community_cloud.BackgroundServices;

namespace e_community_cloud
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
            AddIdentity(_services);
            AddAuthentication(_services);
            AddFirebaseAdmin();
            AddServices(_services);

            _services.AddSignalR();
            _services.AddControllers(); 
            _services.AddHostedService<DistributionBackgroundService>();
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
                    Title = "e-community-cloud",
                    Version = "v1",
                });
                _setup.CustomSchemaIds(x => x.Name);
                _setup.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme
                {
                    Name = "Authorization",
                    Type = SecuritySchemeType.ApiKey,
                    Scheme = "Bearer",
                    BearerFormat = "JWT",
                    In = ParameterLocation.Header,
                    Description = "JWT Authorization header using the Bearer scheme."
                });
                _setup.AddSecurityRequirement(new OpenApiSecurityRequirement()
                    {
                        {
                            new OpenApiSecurityScheme {
                                Reference = new OpenApiReference {
                                    Type = ReferenceType.SecurityScheme,
                                    Id = "Bearer"
                                },
                                Scheme = "oauth2",
                                Name = "Bearer",
                                In = ParameterLocation.Header,

                            },
                        new List<string>()
                        }
                });
            });
        }

        private void AddServices(IServiceCollection _services)
        {
            _services.AddSingleton<IRTListenerSingleton, RTListenerSingleton>();
            _services.AddScoped<ISeedService, SeedService>();
            _services.AddScoped<IAuthService, AuthService>();
            _services.AddScoped<IEmailService, EmailService>();
            _services.AddScoped<IPairingService, PairingService>();
            _services.AddScoped<ISmartMeterService, SmartMeterService>();
            _services.AddScoped<ILocalSignalRSenderService, LocalSignalRSenderService>();
            _services.AddScoped<ILocalSignalRReceiverService, LocalSignalRReceiverService>();
            _services.AddScoped<IRTListenerSignalRSenderService, RTListenerSignalRSenderService>();
            _services.AddScoped<IMemberService, MemberService>();
            _services.AddScoped<ISearchService, SearchService>();
            _services.AddScoped<IFCMService, FCMService>();
            _services.AddScoped<IDistributionService, DistributionService>();
            _services.AddScoped<IMonitoringService, MonitoringService>();
            _services.AddScoped<IReplacementValueService, ReplacementValueService>();
            _services.AddScoped<IECommunityService, ECommunityService>();
            _services.AddScoped<IBlockchainService, BlockchainService>();
            _services.AddScoped<IHistoryService, HistoryService>();
        }

        private void AddIdentity(IServiceCollection _services)
        {
            var jwtConfig = Configuration.GetSection("Jwt");

            _services.AddIdentity<Member, IdentityRole<Guid>>(_options =>
            {
                // Password settings
                _options.Password.RequireDigit = true;
                _options.Password.RequireLowercase = true;
                _options.Password.RequireUppercase = true;
                _options.Password.RequireNonAlphanumeric = true;
                _options.Password.RequiredLength = 6;

                // Lockout settings
                _options.Lockout.DefaultLockoutTimeSpan = TimeSpan.FromMinutes(jwtConfig.GetValue<int>("DefaultLockoutTimeSpanMinutes"));
                _options.Lockout.MaxFailedAccessAttempts = jwtConfig.GetValue<int>("MaxFailedAccessAttempts");
                _options.Lockout.AllowedForNewUsers = false;

                // User settings
                _options.User.RequireUniqueEmail = true;
                _options.User.AllowedUserNameCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-._@ ";
            })
                .AddEntityFrameworkStores<ECommunityCloudContext>()
                .AddDefaultTokenProviders();
        }

        private void AddAuthentication(IServiceCollection _services)
        {
            var jwtConfig = Configuration.GetSection("Jwt");

            _services
                .AddAuthentication(_options =>
                {
                    _options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
                    _options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
                    _options.DefaultScheme = JwtBearerDefaults.AuthenticationScheme;
                })
                .AddJwtBearer(_options =>
                {
                    _options.SaveToken = true;
                    _options.TokenValidationParameters = new TokenValidationParameters()
                    {
                        ValidateIssuer = true,
                        ValidateAudience = true,
                        IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(jwtConfig.GetValue<string>("AccessKey"))),
                        ValidIssuer = jwtConfig.GetValue<string>("Issuer"),
                        ValidAudience = jwtConfig.GetValue<string>("Audience")
                    };
                });
        }

        private void AddFirebaseAdmin()
        {
            FirebaseApp.Create(new AppOptions
            {
                Credential = GoogleCredential.FromJson(
                        JsonConvert.SerializeObject(Configuration.GetSection("Firebase").Get<Dictionary<string, string>>())
                    )
            });
        }

        private void AddDatabase(IServiceCollection _services)
        {
            _services.AddDbContext<ECommunityCloudContext>(options =>
                options.UseSqlServer(Configuration.GetConnectionString("e-community-cloud-db")));

        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(IApplicationBuilder _app, IWebHostEnvironment _env)
        {
            _app.UseHttpsRedirection();
            _app.UseHsts();

            _app.UseSwagger();
            _app.UseSwaggerUI(c => c.SwaggerEndpoint("/swagger/v1/swagger.json", "e-community-cloud v1"));

            _app.UseExceptionHandler("/Error");

            _app.UseRouting();
            _app.Use(async (_context, _next) =>
            {
                _context.Response.Headers.Add("X-Powered-By", $"e-community-cloud v{Program.Version}");
                await _next.Invoke();
            });
            _app.UseAuthentication();
            _app.UseAuthorization();

            _app.UseCors();

            _app.UseEndpoints(_endpoints =>
            {
                _endpoints.MapControllers();
                // SignalR
                _endpoints.MapHub<LocalHub>("Endpoint/Local"); // Smart Meter <-> Cloud
                _endpoints.MapHub<EndDeviceHub>("Endpoint/EndDevice"); // End Device <-> Cloud
            });
        }
    }
}
