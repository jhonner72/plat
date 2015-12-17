using FluentMigrator;

namespace Lombard.Data.Tracking.Migrator.Migrations
{
    [Migration(20150527000001)]
    public class CreateFileReceiptTable : Migration
    {
        public override void Up()
        {
            if (Schema.Table("fxa_file_receipt").Exists())
            {
                Delete.Table("fxa_file_receipt");
            }
        }

        public override void Down()
        {
        }
    }
}
