namespace CoinsServer.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class CoinsPrecision : DbMigration
    {
        public override void Up()
        {
            AlterColumn("dbo.Coins", "PriceUsd", c => c.Decimal(nullable: false, precision: 32, scale: 12));
            AlterColumn("dbo.Coins", "PriceBtc", c => c.Decimal(nullable: false, precision: 32, scale: 12));
            AlterColumn("dbo.Coins", "VolumeUsd24H", c => c.Decimal(precision: 32, scale: 12));
            AlterColumn("dbo.Coins", "MarketCapUsd", c => c.Decimal(precision: 32, scale: 12));
            AlterColumn("dbo.Coins", "AvailableSupply", c => c.Decimal(precision: 32, scale: 12));
            AlterColumn("dbo.Coins", "TotalSupply", c => c.Decimal(precision: 32, scale: 12));
            AlterColumn("dbo.Coins", "MaxSupply", c => c.Decimal(precision: 32, scale: 12));
        }
        
        public override void Down()
        {
            AlterColumn("dbo.Coins", "MaxSupply", c => c.Decimal(precision: 18, scale: 2));
            AlterColumn("dbo.Coins", "TotalSupply", c => c.Decimal(precision: 18, scale: 2));
            AlterColumn("dbo.Coins", "AvailableSupply", c => c.Decimal(precision: 18, scale: 2));
            AlterColumn("dbo.Coins", "MarketCapUsd", c => c.Decimal(precision: 18, scale: 2));
            AlterColumn("dbo.Coins", "VolumeUsd24H", c => c.Decimal(precision: 18, scale: 2));
            AlterColumn("dbo.Coins", "PriceBtc", c => c.Decimal(nullable: false, precision: 18, scale: 2));
            AlterColumn("dbo.Coins", "PriceUsd", c => c.Decimal(nullable: false, precision: 18, scale: 2));
        }
    }
}
