namespace CoinsServer.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class InitialCreate : DbMigration
    {
        public override void Up()
        {
            CreateTable(
                "dbo.Alerts",
                c => new
                    {
                        AlertId = c.Int(nullable: false, identity: true),
                        Token = c.String(nullable: false),
                        CoinId = c.String(nullable: false),
                        LowLimit = c.Decimal(nullable: false, precision: 24, scale: 12),
                        HighLimit = c.Decimal(nullable: false, precision: 24, scale: 12),
                    })
                .PrimaryKey(t => t.AlertId);
            
            CreateTable(
                "dbo.Currencies",
                c => new
                    {
                        CurrencyId = c.String(nullable: false, maxLength: 128),
                        Name = c.String(),
                        Rate = c.Decimal(precision: 24, scale: 12),
                    })
                .PrimaryKey(t => t.CurrencyId);
            
        }
        
        public override void Down()
        {
            DropTable("dbo.Currencies");
            DropTable("dbo.Alerts");
        }
    }
}
