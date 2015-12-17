using FluentMigrator;


namespace Lombard.Data.Tracking.Migrator.Migrations
{
    [Migration(20150622100002)]
    public class _20150622100001_CreateRefBankCalendarTable : Migration
    {
        public override void Up()
        {
            if (Schema.Table("ref_bank_calendar").Exists())
            {
                Delete.Table("ref_bank_calendar");
            }
        }

        public override void Down()
        {
        }
    }
}
