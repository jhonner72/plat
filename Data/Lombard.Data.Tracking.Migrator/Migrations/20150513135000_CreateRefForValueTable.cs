using FluentMigrator;

namespace Lombard.Data.Tracking.Migrator.Migrations
{
    [Migration(20150513135001)]
    public class CreateRefForValueTable : Migration
    {
        public override void Up()
        {
            if (Schema.Table("ref_for_value").Exists())
            {
                Delete.Table("ref_for_value");
            }
        }

        public override void Down()
        {
        }
    }
}
