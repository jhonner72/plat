using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    [Migration(20151028135200)]
    public class _20151028135200_AdjustmentProcessS31: Migration
    {
        public override void Up()
        {
            Alter.Table("NabChq")
                .AddColumn("batchAuxDom")
                    .AsFixedLengthAnsiString(14)
                    .Nullable();
        }

        public override void Down()
        {
            Delete.Column("batchAuxDom")
              .FromTable("NabChq");
        }
    }
}
