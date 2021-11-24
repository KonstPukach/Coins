using System.Data.Entity;

namespace CoinsServer.Models
{
    public class CoinsContext: DbContext
    {
        public DbSet<Currency> MonetaryCurrencies { get; set; }
        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Currency>().Property(currency => currency.Rate).HasPrecision(24, 12);

            base.OnModelCreating(modelBuilder);
        }
    }
}