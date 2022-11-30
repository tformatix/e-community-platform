using System;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata;

namespace SmartMeterAPIDb
{
    public partial class SmartMeterAPIContext : DbContext
    {
        public SmartMeterAPIContext()
        {
        }

        public SmartMeterAPIContext(DbContextOptions<SmartMeterAPIContext> options)
            : base(options)
        {
        }

        public virtual DbSet<METERDATA_RT> METERDATA_RT { get; set; }
        public virtual DbSet<METERDATA_HIST> METERDATA_HIST { get; set; }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            
        }
    }
}
