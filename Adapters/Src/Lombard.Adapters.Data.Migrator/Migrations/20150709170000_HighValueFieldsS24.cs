using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    /// <summary>
    /// This migration adds additional fields required as part of sprint 24
    /// </summary>
    [Migration(20150709170000)]
    public class HighValueFieldsS24 : Migration
    {
        public override void Up()
        {
            Alter.Table("NabChq")
                .AddColumn("voucherIndicatorField")
                    .AsFixedLengthAnsiString(1)
                    .Nullable()
                .AddColumn("batchAccountNumber")
                    .AsFixedLengthAnsiString(10)
                    .Nullable();
      
        }

        public override void Down()
        {
            Delete.Column("voucherIndicatorField")
              .FromTable("NabChq");

            Delete.Column("batchAccountNumber")
              .FromTable("NabChq");
        }
    }
}
