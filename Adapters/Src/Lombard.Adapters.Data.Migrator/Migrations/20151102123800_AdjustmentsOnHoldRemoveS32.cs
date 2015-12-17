using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    [Migration(20151102123800)]
    public class _20151102123800_AdjustmentsOnHoldRemoveS32 : Migration
    {
        public override void Up()
        {
            Alter.Table("NabChq")
               .AddColumn("adjustmentsOnHold")
                   .AsFixedLengthAnsiString(1)
                   .Nullable(); 
        }

        public override void Down()
        {
            Delete.Column("adjustmentsOnHold")
             .FromTable("NabChq");
        }
    }
}
