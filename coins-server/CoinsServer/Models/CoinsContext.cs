using System.Data.Entity;

namespace CoinsServer.Models
{
    public class CoinsContext: DbContext
    {
        public DbSet<User> Users { get; set; }
        public DbSet<Alert> Alerts { get; set; }
        public DbSet<Coin> Coins { get; set; }
        public DbSet<Currency> MonetaryCurrencies { get; set; }
        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Alert>().Property(alert => alert.LowLimit).HasPrecision(24, 12);
            modelBuilder.Entity<Alert>().Property(alert => alert.HighLimit).HasPrecision(24, 12);

            modelBuilder.Entity<Coin>().Property(coin => coin.AvailableSupply).HasPrecision(32, 12);
            modelBuilder.Entity<Coin>().Property(coin => coin.MarketCapUsd).HasPrecision(32, 12);
            modelBuilder.Entity<Coin>().Property(coin => coin.MaxSupply).HasPrecision(32, 12);
            modelBuilder.Entity<Coin>().Property(coin => coin.PriceBtc).HasPrecision(32, 12);
            modelBuilder.Entity<Coin>().Property(coin => coin.PriceUsd).HasPrecision(32, 12);
            modelBuilder.Entity<Coin>().Property(coin => coin.TotalSupply).HasPrecision(32, 12);
            modelBuilder.Entity<Coin>().Property(coin => coin.VolumeUsd24H).HasPrecision(32, 12);

            modelBuilder.Entity<Currency>().Property(currency => currency.Rate).HasPrecision(24, 12);

            base.OnModelCreating(modelBuilder);
        }
    }
}