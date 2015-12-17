using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    /// <summary>
    /// This migration adds more fields required as part of sprint 25
    /// </summary>
    [Migration(20150717170000)]
    public class MoreFieldsS25 : Migration
    {
        public override void Up()
        {
            Alter.Table("NabChq")
                .AddColumn("repostFromDRN")
                    .AsFixedLengthAnsiString(9)
                    .Nullable()
                .AddColumn("repostFromProcessingDate")
                    .AsFixedLengthAnsiString(8)
                    .Nullable()
                .AddColumn("tpcRequired")
                    .AsFixedLengthAnsiString(1)
                    .Nullable()
                .AddColumn("tpcResult")
                    .AsFixedLengthAnsiString(1)
                    .Nullable()
                .AddColumn("fxa_tpc_suspense_pool_flag")
                    .AsFixedLengthAnsiString(1)
                    .Nullable()
                .AddColumn("fxa_unencoded_ECD_return")
                    .AsFixedLengthAnsiString(1)
                    .Nullable();
        }


        public override void Down()
        {
            Delete.Column("repostFromDRN")
              .FromTable("NabChq");
            Delete.Column("repostFromProcessingDate")
              .FromTable("NabChq");
            Delete.Column("tpcRequired")
              .FromTable("NabChq");
            Delete.Column("tpcResult")
              .FromTable("NabChq");
            Delete.Column("fxa_tpc_suspense_pool_flag")
              .FromTable("NabChq");
            Delete.Column("fxa_unencoded_ECD_return")
              .FromTable("NabChq");
        }
    }
}
