using System.Data.Entity;

namespace CoinsServer.Models
{
    public class CoinsContext: DbContext
    {
        public DbSet<Alert> Alerts { get; set; }
        public DbSet<Currency> MonetaryCurrencies { get; set; }
        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Alert>().Property(alert => alert.LowLimit).HasPrecision(24, 12);
            modelBuilder.Entity<Alert>().Property(alert => alert.HighLimit).HasPrecision(24, 12);

            modelBuilder.Entity<Currency>().Property(currency => currency.Rate).HasPrecision(24, 12);

            base.OnModelCreating(modelBuilder);
        }
    }
}