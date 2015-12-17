using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    /// <summary>
    /// This migration adds fields required for third party checking as part of sprint 26
    /// </summary>
    [Migration(20150727170000)]
    public class ThirdPartyCheckingS26 : Migration
    {
        public override void Up()
        {
            Alter.Table("NabChq")
                .AddColumn("tpcMixedDepRet")
                    .AsFixedLengthAnsiString(1)
                    .Nullable();
        }

        public override void Down()
        {
            Delete.Column("tpcMixedDepRet")
              .FromTable("NabChq");
        }
    }
}
