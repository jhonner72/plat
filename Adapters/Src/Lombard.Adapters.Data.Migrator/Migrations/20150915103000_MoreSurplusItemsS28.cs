using FluentMigrator;
using System;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    /// <summary>
    /// This migration adds more columns required for surplus items as part of sprint 28
    /// </summary>
    [Migration(20150915103000)]
    public class MoreSurplusItemsS28 : Migration
    {
        public override void Up()
        {
            Alter.Table("request")
                .AddColumn("RequestCompleted")
                    .AsBoolean()
                    .Nullable();
        }

        public override void Down()
        {
            Delete.Column("RequestCompleted")
              .FromTable("request");
        }
    }
}
