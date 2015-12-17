using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    /// <summary>
    /// This migration adds the routing key column required as part of sprint 28
    /// </summary>
    [Migration(20150910120000)]
    public class AddRoutingKeyS28 : Migration
    {
        public override void Up()
        {
            Alter.Table("queue")
                .AddColumn("RoutingKey")
                    .AsFixedLengthAnsiString(100)
                    .Nullable();

            Alter.Table("NabChq")
                .AddColumn("isRetrievedVoucher")
                    .AsFixedLengthAnsiString(1)
                    .Nullable();

        }

        public override void Down()
        {
            Delete.Column("RoutingKey")
                .FromTable("queue");

            Delete.Column("isRetrievedVoucher")
                .FromTable("NabChq");
        }
    }
}
