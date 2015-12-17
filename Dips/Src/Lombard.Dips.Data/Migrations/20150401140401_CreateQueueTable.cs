using FluentMigrator;

namespace Lombard.Dips.Data.Migrations
{
    [Migration(20150401140401)]
    public class CreateQueueTable : Migration
    {
        private string dbName = "queue";
        public override void Up()
        {
                Create.Table(dbName)
                    .WithColumn("S_LOCATION")
                        .AsString(33)
                        .Nullable()
                    .WithColumn("S_PINDEX")
                        .AsString(16)
                        .Nullable()
                    .WithColumn("S_LOCK")
                        .AsString(10)
                        .Nullable()
                    .WithColumn("S_CLIENT")
                        .AsString(80)
                        .Nullable()
                    .WithColumn("S_JOB_ID")
                        .AsString(128)
                        .Nullable()
                    .WithColumn("S_MODIFIED")
                        .AsString(5)
                        .Nullable()
                    .WithColumn("S_COMPLETE")
                        .AsString(5)
                        .Nullable()
                    .WithColumn("S_BATCH")
                        .AsString(8)
                        .Nullable()
                    .WithColumn("S_TRACE")
                        .AsString(9)
                        .Nullable()
                    .WithColumn("S_SDATE")
                        .AsString(8)
                        .Nullable()
                    .WithColumn("S_STIME")
                        .AsString(8)
                        .Nullable()
                    .WithColumn("S_UTIME")
                        .AsString(10)
                        .Nullable()
                    .WithColumn("S_PRIORITY")
                        .AsString(5)
                        .Nullable()
                    .WithColumn("S_IMG_PATH")
                       .AsString(80)
                       .Nullable()
                    .WithColumn("S_USERNAME")
                        .AsString(250)
                        .Nullable()
                    .WithColumn("S_SELNSTRING")
                        .AsString(128)
                        .Nullable()
                    .WithColumn("S_VERSION")
                        .AsString(32)
                        .Nullable()
                    .WithColumn("S_LOCKUSER")
                        .AsString(17)
                        .Nullable()
                    .WithColumn("S_LOCKMODULENAME")
                        .AsString(17)
                        .Nullable()
                    .WithColumn("S_LOCKUNITID")
                        .AsString(10)
                        .Nullable()
                    .WithColumn("S_LOCKMACHINENAME")
                        .AsString(17)
                        .Nullable()
                    .WithColumn("S_LOCKTIME")
                        .AsString(10)
                        .Nullable()
                    .WithColumn("S_PROCDATE")
                        .AsString(9)
                        .Nullable()
                    .WithColumn("S_REPORTED")
                        .AsString(5)
                        .Nullable();

                     
        }
        public override void Down()
        {
            Delete.Table(dbName);
        }
    }
}
