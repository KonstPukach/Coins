namespace CoinsServer.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class Coin : DbMigration
    {
        public override void Up()
        {
            CreateTable(
                "dbo.Coins",
                c => new
                    {
                        Id = c.String(nullable: false, maxLength: 128),
                        Name = c.String(nullable: false),
                        IconUrl = c.String(nullable: false),
                        Symbol = c.String(nullable: false),
                        Rank = c.Int(nullable: false),
                        PriceUsd = c.Decimal(nullable: false, precision: 18, scale: 2),
                        PriceBtc = c.Decimal(nullable: false, precision: 18, scale: 2),
                        VolumeUsd24H = c.Decimal(precision: 18, scale: 2),
                        MarketCapUsd = c.Decimal(precision: 18, scale: 2),
                        AvailableSupply = c.Decimal(precision: 18, scale: 2),
                        TotalSupply = c.Decimal(precision: 18, scale: 2),
                        MaxSupply = c.Decimal(precision: 18, scale: 2),
                        PercentChange1H = c.Double(),
                        PercentChange24H = c.Double(),
                        PercentChange7D = c.Double(),
                        LastUpdated = c.Int(),
                    })
                .PrimaryKey(t => t.Id);
            
            AlterColumn("dbo.Alerts", "CoinId", c => c.String(nullable: false, maxLength: 128));
            CreateIndex("dbo.Alerts", "CoinId");
            AddForeignKey("dbo.Alerts", "CoinId", "dbo.Coins", "Id", cascadeDelete: true);
        }
        
        public override void Down()
        {
            DropForeignKey("dbo.Alerts", "CoinId", "dbo.Coins");
            DropIndex("dbo.Alerts", new[] { "CoinId" });
            AlterColumn("dbo.Alerts", "CoinId", c => c.String(nullable: false));
            DropTable("dbo.Coins");
        }
    }
}
