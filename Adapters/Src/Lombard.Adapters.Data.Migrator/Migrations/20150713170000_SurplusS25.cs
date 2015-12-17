using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    /// <summary>
    /// This migration adds surplus fields required as part of sprint 25
    /// </summary>
    [Migration(20150713170000)]
    public class SurplusS25 : Migration
    {
        public override void Up()
        {
            Alter.Table("NabChq")
                .AddColumn("surplusItemFlag")
                    .AsFixedLengthAnsiString(1)
                    .Nullable();
        }

        public override void Down()
        {
            Delete.Column("surplusItemFlag")
              .FromTable("NabChq");
        }
    }
}
