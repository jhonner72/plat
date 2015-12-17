using FluentMigrator;
using System;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    /// <summary>
    /// This migration adds additional columns required for surplus items as part of sprint 28
    /// </summary>
    [Migration(20150925153000)]
    public class AdditionalSurplusItemsS28V3 : Migration
    {
        public override void Up()
        {
            Alter.Table("NabChq")
                .AddColumn("isBlankCredit")
                    .AsFixedLengthAnsiString(1)
                    .Nullable();
        }

        public override void Down()
        {
            Delete.Column("isBlankCredit")
              .FromTable("NabChq");
        }
    }
}
