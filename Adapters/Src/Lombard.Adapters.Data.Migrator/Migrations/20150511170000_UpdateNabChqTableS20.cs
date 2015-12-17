using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    [Migration(20150511170000)]
    public class UpdateNabChqTableS20 : Migration
    {
        public override void Up()
        {
            Alter.Table("NabChq")
                .AlterColumn("ead")
                    .AsFixedLengthAnsiString(15)
                    .Nullable()
                .AlterColumn("acc_num")
                    .AsFixedLengthAnsiString(10)
                    .Nullable()
                .AlterColumn("amount")
                    .AsFixedLengthAnsiString(12)
                    .Nullable()
                .AddColumn("ie_endPoint")
                    .AsFixedLengthAnsiString(10)
                    .Nullable()
                .AddColumn("export_exclude_flag")
                    .AsFixedLengthAnsiString(1)
                    .Nullable()
                .AddColumn("orig_ead")
                    .AsFixedLengthAnsiString(15)
                    .Nullable()
                .AddColumn("orig_ser_num")
                    .AsFixedLengthAnsiString(9)
                    .Nullable()
                .AddColumn("orig_bsb_num")
                    .AsFixedLengthAnsiString(6)
                    .Nullable()
                .AddColumn("orig_acc_num")
                    .AsFixedLengthAnsiString(10)
                    .Nullable()
                .AddColumn("orig_trancode")
                    .AsFixedLengthAnsiString(3)
                    .Nullable()
                .AddColumn("orig_amount")
                    .AsFixedLengthAnsiString(12)
                    .Nullable();
        }

        public override void Down()
        {

            Alter.Table("NabChq")
                .AlterColumn("ead")
                    .AsFixedLengthAnsiString(11)
                    .Nullable()
                 .AlterColumn("acc_num")
                    .AsFixedLengthAnsiString(21)
                    .Nullable()
                .AlterColumn("amount")
                    .AsFixedLengthAnsiString(15)
                    .Nullable();
            
            Delete.Column("ie_endPoint")
                .FromTable("NabChq");
            Delete.Column("export_exclude_flag")
                .FromTable("NabChq");
            Delete.Column("orig_ead")
                .FromTable("NabChq");
            Delete.Column("orig_ser_num")
                .FromTable("NabChq");
            Delete.Column("orig_bsb_num")
                .FromTable("NabChq");
            Delete.Column("orig_acc_num")
                .FromTable("NabChq");
            Delete.Column("orig_trancode")
                .FromTable("NabChq");
            Delete.Column("orig_amount")
                .FromTable("NabChq");
        }
    }
}
