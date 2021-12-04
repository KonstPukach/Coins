namespace CoinsServer.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class AlertStructureUpdate : DbMigration
    {
        public override void Up()
        {
            AddColumn("dbo.Alerts", "Name", c => c.String(nullable: false));
            AddColumn("dbo.Alerts", "CreatedAt", c => c.DateTime(nullable: false));
        }
        
        public override void Down()
        {
            DropColumn("dbo.Alerts", "CreatedAt");
            DropColumn("dbo.Alerts", "Name");
        }
    }
}
