using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    [Migration(20151103110500)]
    public class _20151103110500_insertedCreditTypeS32:Migration
    {
        public override void Up()
        {
            Alter.Table("NabChq")
               .AddColumn("insertedCreditType")
                   .AsFixedLengthAnsiString(1)
                   .Nullable();
        }

        public override void Down()
        {
            Delete.Column("insertedCreditType")
             .FromTable("NabChq");
        }
    }
}
