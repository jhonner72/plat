using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    /// <summary>
    /// This migration adds 3rd party checking fields required as part of sprint 25
    /// </summary>
    [Migration(20150715170000)]
    public class ThirdPartyCheckingS25 : Migration
    {
        public override void Up()
        {
            Alter.Table("NabChq")
                .AddColumn("fxaPtQAAmtFlag")
                    .AsFixedLengthAnsiString(1)
                    .Nullable()
                .AddColumn("fxaPtQACodelineFlag")
                    .AsFixedLengthAnsiString(1)
                    .Nullable();
        }

        public override void Down()
        {
            Delete.Column("fxaPtQAAmtFlag")
              .FromTable("NabChq");
            Delete.Column("fxaPtQACodelineFlag")
              .FromTable("NabChq");
        }
    }
}
