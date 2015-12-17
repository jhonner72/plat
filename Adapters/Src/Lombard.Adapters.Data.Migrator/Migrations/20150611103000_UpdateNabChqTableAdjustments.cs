using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    [Migration(20150611103000)]
    public class UpdateNabChqTableAdjustments : Migration
    {
        public override void Up()
        {
            Alter.Table("NabChq")
                .AddColumn("presentationMode")
                    .AsFixedLengthAnsiString(1)
                    .Nullable()
                .AddColumn("adjustmentReasonCode")
                    .AsFixedLengthAnsiString(2)
                    .Nullable()
                .AddColumn("adjustmentDescription")
                    .AsFixedLengthAnsiString(60)
                    .Nullable()
                .AddColumn("adjustedBy")
                    .AsFixedLengthAnsiString(15)
                    .Nullable()
                .AddColumn("adjustmentsOnHold")
                    .AsFixedLengthAnsiString(1)
                    .Nullable()
                .AddColumn("adjustedFlag")
                    .AsFixedLengthAnsiString(1)
                    .Nullable()
                .AddColumn("adjustmentLetterRequired")
                    .AsFixedLengthAnsiString(1)
                    .Nullable()
               .AddColumn("adjustmentType")
                    .AsFixedLengthAnsiString(9)
                    .Nullable();
        }

        public override void Down()
        {
            Delete.Column("presentationMode")
                .FromTable("NabChq");
            Delete.Column("adjustmentReasonCode")
                .FromTable("NabChq");
            Delete.Column("adjustmentDescription")
                .FromTable("NabChq");
            Delete.Column("adjustedBy")
                .FromTable("NabChq");
            Delete.Column("adjustmentsOnHold")
                .FromTable("NabChq");
            Delete.Column("adjustedFlag")
                .FromTable("NabChq");
            Delete.Column("adjustmentLetterRequired")
                .FromTable("NabChq");
            Delete.Column("adjustmentType")
                .FromTable("NabChq");
        }
    }
}
