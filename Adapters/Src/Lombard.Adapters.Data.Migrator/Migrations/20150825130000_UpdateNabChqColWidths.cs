using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    /// <summary>
    /// This migration modifies field lengths for ead, ser_num & acc_num as part of sprint 27
    /// </summary>
    [Migration(20150825130000)]
    public class UpdateNabChqColWidths : Migration
    {
        public override void Up()
        {
            Alter.Table("NabChq")
                .AlterColumn("ead")
                .AsFixedLengthAnsiString(16)
                .Nullable()
                .AlterColumn("ser_num")
                .AsFixedLengthAnsiString(14)
                .Nullable()
                .AlterColumn("acc_num")
                .AsFixedLengthAnsiString(12)
                .Nullable();
        }

        public override void Down()
        {
            Alter.Table("NabChq")
                .AlterColumn("ead")
                .AsFixedLengthAnsiString(15)
                .Nullable()
                .AlterColumn("ser_num")
                .AsFixedLengthAnsiString(9)
                .Nullable()
                .AlterColumn("acc_num")
                .AsFixedLengthAnsiString(10)
                .Nullable();
        }
    }
}
