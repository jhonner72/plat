using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    [Migration(21050424110500)]
    public class UpdateNabChqTable : Migration
    {
        public override void Up()
        {
            Alter.Table("NabChq")
                .AddColumn("unproc_flag")
                .AsFixedLengthAnsiString(1)
                .Nullable();
        }

        public override void Down()
        {
            Delete.Column("unproc_flag")
                .FromTable("NabChq");
        }
    }
}
