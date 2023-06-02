using e_community_local_lib.Database.General;
using e_community_local_lib.Database.Meter;
using e_community_local_lib.Database.PriceRate;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Text;

namespace e_community_local_lib.Database
{
    public class ECommunityLocalContext : DbContext
    {
        public ECommunityLocalContext() { }

        public ECommunityLocalContext(DbContextOptions<ECommunityLocalContext> _options) : base(_options) { }

        public DbSet<BatterySystem> BatterySystem { get; set; }
        public DbSet<Credential> Credential { get; set; }
        public DbSet<Member> Member { get; set; }
        public DbSet<ECommunity> ECommunity { get; set; }
        public DbSet<PVSystem> PVSystem { get; set; }
        public DbSet<SmartMeter> SmartMeter { get; set; }
        public DbSet<EventCase> EventCase { get; set; }
        public DbSet<MeterDataHistory> MeterDataHistory { get; set; }
        public DbSet<MeterDataProfile> MeterDataProfile { get; set; }
        public DbSet<MeterDataRealTime> MeterDataRealTime { get; set; }
        public DbSet<Charge> Charge { get; set; }
        public DbSet<SupplierPriceRate> SupplierPriceRate { get; set; }
        public DbSet<GridPriceRate> GridPriceRate { get; set; }
    }
}
