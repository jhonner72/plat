using FluentMigrator;

namespace Lombard.Data.Job.Migrator.Migrations
{
    [Migration(20150513112500)]
    public class CreateJobTable : Migration
    {
        public override void Up()
        {
            Create.Table("JOB")
                .WithColumn("JOB_ID").AsString(41).NotNullable().PrimaryKey("PK_JOB")
                .WithColumn("JOB_OBJECT").AsMaxString().Nullable()
                .WithColumn("MODIFIED_DATE").AsDateTime().NotNullable().WithDefault(SystemMethods.CurrentDateTime);
        }

        public override void Down()
        {
            Delete.Table("JOB");
        }
    }
}
