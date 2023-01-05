using e_community_cloud_lib.Database.Community;
using e_community_cloud_lib.Database.General;
using e_community_cloud_lib.Database.PriceRate;
using e_community_cloud_lib.Database.Local;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using System;
using System.Linq;
using e_community_cloud_lib.Util.Enums;
using System.Collections.Generic;
using e_community_cloud_lib.Util.Extensions;
using e_community_cloud_lib.Util;
using System.Threading.Tasks;

namespace e_community_cloud_lib.Database
{
    public class ECommunityCloudContext : DbContext
    {
        public ECommunityCloudContext()
        {
        }

        public ECommunityCloudContext(DbContextOptions<ECommunityCloudContext> _options)
            : base(_options)
        {
        }

        // Community
        public DbSet<ECommunity> ECommunity { get; set; }
        public DbSet<ECommunityMembership> ECommunityMembership { get; set; }
        public DbSet<ECommunityDistribution> ECommunityDistribution { get; set; }
        public DbSet<ECommunityType> ECommunityType { get; set; }


        // General
        public DbSet<Language> Language { get; set; }
        public DbSet<LegalForm> LegalForm { get; set; }
        public DbSet<Member> Member { get; set; }
        public DbSet<MemberFCMToken> MemberFCMToken { get; set; }
        public DbSet<Monitoring> Monitoring { get; set; }
        public DbSet<Translation> Translation { get; set; }

        // Local
        public DbSet<BatterySystem> BatterySystem { get; set; }
        public DbSet<EventCase> EventCase { get; set; }
        public DbSet<MeterDataProfile> MeterDataProfile { get; set; }
        public DbSet<PVSystem> PVSystem { get; set; }
        public DbSet<SmartMeter> SmartMeter { get; set; }
        public DbSet<SmartMeterPortion> SmartMeterPortion { get; set; }
        public DbSet<MeterDataMonitoring> MeterDataMonitoring { get; set; }

        // Price Rate
        public DbSet<Charge> Charge { get; set; }
        public DbSet<GridPriceRate> GridPriceRate { get; set; }
        public DbSet<GridPriceRateCharge> GridPriceRateCharge { get; set; }
        public DbSet<Provider> Provider { get; set; }
        public DbSet<SupplierPriceRate> SupplierPriceRate { get; set; }


        protected override void OnModelCreating(ModelBuilder _modelBuilder)
        {
            base.OnModelCreating(_modelBuilder);

            // composite keys
            _modelBuilder.Entity<Translation>().HasKey(o => new { o.EventCaseId, o.LanguageId });
            _modelBuilder.Entity<ECommunityMembership>().HasKey(o => new { o.ECommunityId, o.MemberId });
            _modelBuilder.Entity<SmartMeterPortion>().HasKey(o => new { o.ECommunityDistributionId, o.SmartMeterId });
            _modelBuilder.Entity<MeterDataMonitoring>().HasKey(o => new { o.MonitoringId, o.SmartMeterId });
            _modelBuilder.Entity<MemberFCMToken>().HasKey(o => new { o.MemberId, o.Token });
            _modelBuilder.Entity<GridPriceRateCharge>().HasKey(o => new { o.GridPriceRateId, o.ChargeId });

            OnDeleteBehaviour(_modelBuilder);

            _modelBuilder.Entity<IdentityRole<Guid>>().ToTable("Roles", "dbo");
            _modelBuilder.Entity<IdentityUserLogin<Guid>>()
                .ToTable("UserLogins", "dbo")
                .HasKey(o => o.UserId);
            _modelBuilder.Entity<IdentityUserToken<Guid>>()
                .ToTable("UserTokens", "dbo")
                .HasKey(o => o.UserId);
            _modelBuilder.Entity<IdentityUserRole<Guid>>()
                .ToTable("UserRoles", "dbo")
                .HasKey(o => new { o.UserId, o.RoleId });
            _modelBuilder.Entity<IdentityUserClaim<Guid>>().ToTable("UserClaims", "dbo");
            _modelBuilder.Entity<IdentityRoleClaim<Guid>>().ToTable("RoleClaims", "dbo");

            // resolve cycles & multiple cascade paths

        }

        private void OnDeleteBehaviour(ModelBuilder _modelBuilder)
        {
            // ON DELETE CASCADE
            _modelBuilder.Entity<MemberFCMToken>()
                .HasOne(x => x.Member)
                .WithMany(x => x.MemberFCMTokens)
                .OnDelete(DeleteBehavior.ClientCascade);

            // ON DELETE SET NULL
            _modelBuilder.Entity<SmartMeter>()
                .HasOne(x => x.GridPriceRate)
                .WithMany(x => x.SmartMeters)
                .OnDelete(DeleteBehavior.ClientSetNull);

            _modelBuilder.Entity<SmartMeter>()
                .HasOne(x => x.SupplierPriceRate)
                .WithMany(x => x.SmartMeters)
                .OnDelete(DeleteBehavior.ClientSetNull);

            _modelBuilder.Entity<ECommunity>()
                .HasOne(x => x.SupplierPriceRate)
                .WithMany(x => x.ECommunities)
                .OnDelete(DeleteBehavior.ClientSetNull);

            _modelBuilder.Entity<ECommunity>()
                .HasOne(x => x.LegalForm)
                .WithMany(x => x.ECommunities)
                .OnDelete(DeleteBehavior.ClientSetNull);

            _modelBuilder.Entity<Member>()
                .HasOne(x => x.LegalForm)
                .WithMany(x => x.Members)
                .OnDelete(DeleteBehavior.ClientSetNull);

            _modelBuilder.Entity<GridPriceRate>()
                .HasOne(x => x.Provider)
                .WithMany(x => x.GridPriceRates)
                .OnDelete(DeleteBehavior.ClientSetNull);

            _modelBuilder.Entity<SupplierPriceRate>()
                .HasOne(x => x.Provider)
                .WithMany(x => x.SupplierPriceRates)
                .OnDelete(DeleteBehavior.ClientSetNull);

            _modelBuilder.Entity<Member>()
                .HasOne(x => x.Language)
                .WithMany(x => x.Members)
                .OnDelete(DeleteBehavior.ClientSetNull);

            _modelBuilder.Entity<MeterDataProfile>()
                .HasOne(x => x.EventCase)
                .WithMany(x => x.MeterDataProfiles)
                .OnDelete(DeleteBehavior.ClientSetNull);
        }

        /// <param name="_memberId">member id</param>
        /// <returns>eCommunity id of a member (only when currently active)</returns>
        public Guid? GetECommunityId(Guid? _memberId)
        {
            return ECommunityMembership
                .FirstOrDefault(x => x.MemberId == _memberId && Constants.ACTIVE_MEMBER_PERMISSIONS.Contains(x.ECommunityPermission))?.ECommunityId;
        }
    }
}
