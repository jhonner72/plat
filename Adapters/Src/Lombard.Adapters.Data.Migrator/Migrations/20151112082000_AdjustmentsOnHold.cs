using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    [Migration(20151112082000)]
    public class _20151112082000_AdjustmentsOnHold : Migration
    {
        public override void Up()
        {
            Delete.Column("adjustmentsOnHold")
             .FromTable("NabChq");
        }

        public override void Down()
        {
            Alter.Table("NabChq")
                .AddColumn("adjustmentsOnHold")
                    .AsFixedLengthAnsiString(1)
                    .Nullable();
        }
    }
}
