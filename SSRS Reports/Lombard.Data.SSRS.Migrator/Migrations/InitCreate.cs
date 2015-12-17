using FluentMigrator;

namespace Lombard.Data.SSRS.Migrator.Migrations
{
    [Profile("TrackingProfile")]
    public class InitCreate : Migration
    {
        public override void Up()
        {
            Execute.Script(@"Scripts\InitCreate.sql");
        }

        public override void Down()
        {
        }
    }
}
