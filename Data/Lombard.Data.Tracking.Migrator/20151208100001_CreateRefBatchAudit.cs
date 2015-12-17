using FluentMigrator;
using FluentMigrator.Runner.Extensions;

namespace Lombard.Data.Tracking.Migrator
{
    [Migration(20151208100003)]
    public class _20151208100001_CreateRefBatchAudit : Migration
    {
        public override void Up()
        {
            if (Schema.Table("ref_batch_audit").Exists())
            {
                Delete.Table("ref_batch_audit");
            }

            Create.Table("ref_batch_audit")
                .WithColumn("file_id").AsInt32().Identity(1, 1).NotNullable().PrimaryKey("[PK_ref_batch_audit]")
                .WithColumn("machine_number").AsString(3).NotNullable()
                .WithColumn("file_timestamp").AsDateTime().NotNullable()
                .WithColumn("file_name").AsString(255).NotNullable()
                .WithColumn("batch_number").AsString(40).NotNullable()
                .WithColumn("processing_date").AsDate().NotNullable()
                .WithColumn("work_type").AsString(50).NotNullable()
                .WithColumn("record_count").AsInt32().NotNullable()
                .WithColumn("first_DRN").AsString(16).NotNullable()
                .WithColumn("last_DRN").AsString(16).NotNullable();

        }

        public override void Down()
        {
            if (Schema.Table("ref_batch_audit").Exists())
            {
                Delete.Table("ref_batch_audit");
            }
        }

    }
}
