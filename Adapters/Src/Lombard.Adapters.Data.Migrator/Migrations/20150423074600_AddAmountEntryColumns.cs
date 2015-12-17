using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    [Migration(20150423074600)]
    public class AddAmountEntryColumns : Migration
    {
        public override void Up()
        {
            Alter.Table("NabChq")
                .AddColumn("amountConfidenceLevel")
                .AsFixedLengthAnsiString(3)
                .Nullable()
                .AddColumn("balanceReason")
                .AsFixedLengthAnsiString(20)
                .Nullable();

        }

        public override void Down()
        {
            Delete.Column("amountConfidenceLevel")
                .FromTable("NabChq");

            Delete.Column("balanceReason")
                .FromTable("NabChq");
        }
    }
}
