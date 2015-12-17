using FluentMigrator;

namespace Lombard.Data.Tracking.Migrator
{
    [Migration(20151107100001)]
    public class _20151107100001_CreateRefAusPostECLData : Migration
    {
        public override void Up()
        {
            if (Schema.Table("ref_aus_post_ecl_data").Exists())
            {
                Delete.Table("ref_aus_post_ecl_data");
            }

            Create.Table("ref_aus_post_ecl_data")
                .WithColumn("file_state").AsString(3).NotNullable()
                .WithColumn("file_date").AsDateTime().NotNullable()
                .WithColumn("record_sequence").AsInt32().NotNullable()
                .WithColumn("record_content").AsString(112).NotNullable()
                .WithColumn("record_type").AsString(1).NotNullable();

        }

        public override void Down()
        {
            if (Schema.Table("ref_aus_post_ecl_data").Exists())
            {
                Delete.Table("ref_aus_post_ecl_data");
            }
        }
    }
}
