using FluentMigrator;
using System;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    /// <summary>
    /// This migration adds additional columns required for surplus items as part of sprint 28
    /// </summary>
    [Migration(20150916103000)]
    public class AdditionalSurplusItemsS28 : Migration
    {
        public override void Up()
        {
            Alter.Table("NabChq")
                .AddColumn("isInsertedCredit")
                    .AsFixedLengthAnsiString(1)
                    .Nullable();
        }

        public override void Down()
        {
            Delete.Column("isInsertedCredit")
              .FromTable("NabChq");
        }
    }
}
