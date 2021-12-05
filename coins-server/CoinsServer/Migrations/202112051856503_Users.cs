namespace CoinsServer.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class Users : DbMigration
    {
        public override void Up()
        {
            CreateTable(
                "dbo.Users",
                c => new
                    {
                        Id = c.Int(nullable: false, identity: true),
                        Token = c.String(nullable: false),
                    })
                .PrimaryKey(t => t.Id);
            
            AddColumn("dbo.Alerts", "UserId", c => c.Int(nullable: false));
            CreateIndex("dbo.Alerts", "UserId");
            AddForeignKey("dbo.Alerts", "UserId", "dbo.Users", "Id", cascadeDelete: true);
            DropColumn("dbo.Alerts", "Token");
        }
        
        public override void Down()
        {
            AddColumn("dbo.Alerts", "Token", c => c.String(nullable: false));
            DropForeignKey("dbo.Alerts", "UserId", "dbo.Users");
            DropIndex("dbo.Alerts", new[] { "UserId" });
            DropColumn("dbo.Alerts", "UserId");
            DropTable("dbo.Users");
        }
    }
}
