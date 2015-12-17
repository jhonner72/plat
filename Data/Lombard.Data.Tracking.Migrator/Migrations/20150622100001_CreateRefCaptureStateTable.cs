using FluentMigrator;


namespace Lombard.Data.Tracking.Migrator.Migrations
{
    [Migration(20150622100041)]
    public class _20150622100001_CreateRefCaptureStateTable : Migration
    {
        public override void Up()
        {
            Create.Table("ref_capture_state")
                .WithColumn("capture_state").AsString(6).NotNullable().PrimaryKey("PK_ref_capture_state")
                .WithColumn("capture_state_name").AsString(100);
        }

        public override void Down()
        {
            Delete.Table("ref_capture_state");
        }
    }
}
