namespace CoinsServer.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class Favorites : DbMigration
    {
        public override void Up()
        {
            CreateTable(
                "dbo.FavoriteCoins",
                c => new
                    {
                        Id = c.Int(nullable: false, identity: true),
                        CoinId = c.String(nullable: false, maxLength: 128),
                        UserId = c.Int(nullable: false),
                    })
                .PrimaryKey(t => t.Id)
                .ForeignKey("dbo.Coins", t => t.CoinId, cascadeDelete: true)
                .ForeignKey("dbo.Users", t => t.UserId, cascadeDelete: true)
                .Index(t => t.CoinId)
                .Index(t => t.UserId);
            
        }
        
        public override void Down()
        {
            DropForeignKey("dbo.FavoriteCoins", "UserId", "dbo.Users");
            DropForeignKey("dbo.FavoriteCoins", "CoinId", "dbo.Coins");
            DropIndex("dbo.FavoriteCoins", new[] { "UserId" });
            DropIndex("dbo.FavoriteCoins", new[] { "CoinId" });
            DropTable("dbo.FavoriteCoins");
        }
    }
}
