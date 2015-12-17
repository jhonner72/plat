using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    /// <summary>
    /// This migration adds fields required for high value as part of sprint 26
    /// </summary>
    [Migration(20150730120000)]
    public class HighValueS26 : Migration
    {
        public override void Up()
        {
            Alter.Table("NabChq")
                .AddColumn("highValueFlag")
                    .AsFixedLengthAnsiString(1)
                    .Nullable();
        }

        public override void Down()
        {
            Delete.Column("highValueFlag")
              .FromTable("NabChq");
        }
    }
}
