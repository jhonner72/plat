using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    [Migration(20150423132500)]
    public class AddTransactionColumns : Migration
    {
        public override void Up()
        {
            Alter.Table("NabChq")
                .AddColumn("transactionLinkNumber")
                    .AsFixedLengthAnsiString(60)
                    .Nullable();
        }

        public override void Down()
        {
            Delete.Column("transactionLinkNumber")
                .FromTable("NabChq");
        }
    }
}
