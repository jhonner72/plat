using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    /// <summary>
    /// This migration reverts the DRN field to be 9 chars
    /// </summary>
    [Migration(20150622130000)]
    public class UpdateNabChqTableUndoDRN : Migration
    {
        public override void Up()
        {
            // index needs to recreate as it depends on S_TRACE
            Delete.Index("NabChq_TS")
                .OnTable("NabChq"); 
            Delete.Index("NabChq_DBTS")
                 .OnTable("NabChq");
            Delete.Index("DB_INDEX_H")
                .OnTable("DB_INDEX");

            Alter.Table("NabChq")
                .AlterColumn("doc_ref_num")
                    .AsFixedLengthAnsiString(9)
                    .Nullable()
                .AlterColumn("trace")
                    .AsFixedLengthAnsiString(9)
                    .Nullable()
                .AlterColumn("S_TRACE")
                    .AsFixedLengthAnsiString(9)
                    .Nullable()
                .AlterColumn("batch_type")
                   .AsFixedLengthAnsiString(20)
                   .Nullable()
               .AlterColumn("sub_batch_type")
                   .AsFixedLengthAnsiString(20)
                   .Nullable();

            Alter.Table("DB_INDEX")
                .AlterColumn("TRACE")
                    .AsFixedLengthAnsiString(9)
                    .Nullable();

            Alter.Table("queue")
                .AlterColumn("S_TRACE")
                    .AsFixedLengthAnsiString(9)
                    .Nullable();

            Create.Index("NabChq_TS")
                .OnTable("NabChq")
                    .OnColumn("S_TRACE")
                        .Ascending()
                    .OnColumn("S_SEQUENCE")
                        .Ascending()
                    .WithOptions()
                        .NonClustered();

            Create.Index("NabChq_DBTS")
                .OnTable("NabChq")
                    .OnColumn("sys_date")
                        .Ascending()
                    .OnColumn("S_BATCH")
                        .Ascending()
                    .OnColumn("S_TRACE")
                        .Ascending()
                    .OnColumn("S_SEQUENCE")
                        .Ascending()
                    .WithOptions()
                        .NonClustered()
                    .WithOptions()
                        .Unique();

            Create.Index("DB_INDEX_H")
                .OnTable("DB_INDEX")
                    .OnColumn("BATCH")
                        .Ascending()
                    .OnColumn("TRACE")
                        .Ascending()
                    .OnColumn("SEQUENCE")
                        .Ascending()
                    .WithOptions()
                        .NonClustered()
                    .WithOptions()
                        .Unique();
        }

        public override void Down()
        {
            // index needs to recreate as it depends on S_TRACE
            Delete.Index("NabChq_TS")
                .OnTable("NabChq");
            Delete.Index("NabChq_DBTS")
                .OnTable("NabChq");
            Delete.Index("DB_INDEX_H")
                .OnTable("DB_INDEX");

            Alter.Table("NabChq")
                .AlterColumn("doc_ref_num")
                    .AsFixedLengthAnsiString(12)
                    .Nullable()
               .AlterColumn("trace")
                   .AsFixedLengthAnsiString(12)
                   .Nullable()
               .AlterColumn("S_TRACE")
                   .AsFixedLengthAnsiString(12)
                   .Nullable()
                .AlterColumn("batch_type")
                   .AsFixedLengthAnsiString(6)
                   .Nullable()
               .AlterColumn("sub_batch_type")
                   .AsFixedLengthAnsiString(6)
                   .Nullable();

            Alter.Table("DB_INDEX")
                .AlterColumn("TRACE")
                    .AsFixedLengthAnsiString(12)
                    .Nullable();

            Alter.Table("queue")
                .AlterColumn("S_TRACE")
                    .AsFixedLengthAnsiString(12)
                    .Nullable();

            Create.Index("NabChq_TS")
               .OnTable("NabChq")
                   .OnColumn("S_TRACE")
                       .Ascending()
                   .OnColumn("S_SEQUENCE")
                       .Ascending()
                   .WithOptions()
                       .NonClustered();

            Create.Index("NabChq_DBTS")
                .OnTable("NabChq")
                    .OnColumn("sys_date")
                        .Ascending()
                    .OnColumn("S_BATCH")
                        .Ascending()
                    .OnColumn("S_TRACE")
                        .Ascending()
                    .OnColumn("S_SEQUENCE")
                        .Ascending()
                    .WithOptions()
                        .NonClustered()
                    .WithOptions()
                        .Unique();

            Create.Index("DB_INDEX_H")
                .OnTable("DB_INDEX")
                    .OnColumn("BATCH")
                        .Ascending()
                    .OnColumn("TRACE")
                        .Ascending()
                    .OnColumn("SEQUENCE")
                        .Ascending()
                    .WithOptions()
                        .NonClustered()
                    .WithOptions()
                        .Unique();
        }
    }
}
