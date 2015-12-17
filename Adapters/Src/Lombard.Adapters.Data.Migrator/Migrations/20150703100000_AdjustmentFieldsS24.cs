using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    /// <summary>
    /// This migration adds the fields required for adjustments as part of sprint 24
    /// </summary>
    [Migration(20150703100000)]
    public class AdjustmentFieldsS24 : Migration
    {
        public override void Up()
        {
            Alter.Table("NabChq")
                .AddColumn("listingPageNumber")
                    .AsFixedLengthAnsiString(3)
                    .Nullable()
                .AddColumn("captureBSB")
                    .AsFixedLengthAnsiString(6)
                    .Nullable();
      
        }

        public override void Down()
        {
            Delete.Column("listingPageNumber")
              .FromTable("NabChq");

            Delete.Column("captureBSB")
              .FromTable("NabChq");
        }
    }
}
