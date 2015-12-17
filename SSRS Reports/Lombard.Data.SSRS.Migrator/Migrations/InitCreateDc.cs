using FluentMigrator;

namespace Lombard.Data.SSRS.Migrator.Migrations
{
    [Profile("DocumentumProfile")]
    public class InitCreateDc : Migration
    {
        public override void Up()
        {
            Execute.Script(@"Scripts\InitCreate_doc.sql");
        }

        public override void Down()
        {
        }
    }
}
