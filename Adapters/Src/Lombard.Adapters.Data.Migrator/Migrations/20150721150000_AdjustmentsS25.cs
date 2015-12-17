using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    /// <summary>
    /// This migration adds fields required for adjustments as part of sprint 25
    /// </summary>
    [Migration(20150721150000)]
    public class AdjustmentsS25 : Migration
    {
        public override void Up()
        {
            Alter.Table("NabChq")
                .AddColumn("isGeneratedVoucher")
                    .AsFixedLengthAnsiString(1)
                    .Nullable();
        }

        public override void Down()
        {
            Delete.Column("isGeneratedVoucher")
              .FromTable("NabChq");
        }
    }
}
