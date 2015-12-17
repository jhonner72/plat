using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    [Migration(20150408163001)]
    public class AddValidateCodelinesColumn : Migration
    {
        private const string DbName = "queue";

        public override void Up()
        {
            Alter.Table(DbName)
                .AddColumn("ValidateCodelineCompleted")
                    .AsBoolean()
                    .Nullable()
                .AddColumn("ConcurrencyToken")
                    .AsByte()
                    .Nullable()
                .AddColumn("ValidateCodelineCorrelationId")
                    .AsString(50)
                    .Nullable();

        }
        public override void Down()
        {
            Delete.Column("ValidateCodelineCompleted").FromTable(DbName);
            Delete.Column("ConcurrencyToken").FromTable(DbName);
            Delete.Column("ValidateCodelineCorrelationId").FromTable(DbName);
        }
    }
}
