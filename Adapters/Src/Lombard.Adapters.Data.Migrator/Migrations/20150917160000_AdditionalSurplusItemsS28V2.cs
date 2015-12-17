using FluentMigrator;
using System;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    /// <summary>
    /// This migration adds additional columns required for surplus items as part of sprint 28
    /// </summary>
    [Migration(20150917160000)]
    public class AdditionalSurplusItemsS28V2 : Migration
    {
        public override void Up()
        {
            Alter.Table("response_done")
                .AddColumn("number_of_results")
                    .AsInt32()
                    .Nullable();
                    
        }

        public override void Down()
        {
            Delete.Column("number_of_results")
              .FromTable("response_done");
        }
    }
}
