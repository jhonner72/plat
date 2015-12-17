using FluentMigrator;
using System;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    /// <summary>
    /// This migration adds additional columns required as part of sprint 29
    /// </summary>
    [Migration(20150928160000)]
    public class CreditNoteFlagS29 : Migration
    {
        public override void Up()
        {
            Alter.Table("NabChq")
                .AddColumn("creditNoteFlag")
                    .AsFixedLengthAnsiString(1)
                    .Nullable();
        }

        public override void Down()
        {
            Delete.Column("creditNoteFlag")
              .FromTable("NabChq");
        }
    }
}
