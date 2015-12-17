using FluentMigrator;
using FluentMigrator.Runner.Extensions;
namespace Lombard.Data.Tracking.Migrator.Migrations

{
    [Migration(20151111100002)]
    public class _20151111100001_CreateRefFileTransmissionTable : Migration
    {
        public override void Up()
        {
            if (Schema.Table("ref_file_transmission").Exists())
            {
                Delete.Table("ref_file_transmission");
            }

            Create.Table("ref_file_transmission")
                .WithColumn("file_id").AsInt32().Identity(1, 1).NotNullable().PrimaryKey("[PK_ref_file_transmission]")
                .WithColumn("file_timestamp").AsDateTime().NotNullable()
                .WithColumn("file_name").AsString(255).NotNullable()
                .WithColumn("counter_party").AsString(50).NotNullable()
                .WithColumn("transmission").AsString(10).NotNullable()
                .WithColumn("remote_address").AsString(255).NotNullable()
                .WithColumn("jscape_host").AsString(50).NotNullable()
                .WithColumn("direction").AsString(1).NotNullable();

        }

        public override void Down()
        {
            if (Schema.Table("ref_file_transmission").Exists())
            {
                Delete.Table("ref_file_transmission");
            }
        }
    }
}