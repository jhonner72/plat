using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    [Migration(20151023111000)]
    public class _20151023111000_BulkCreditS30 : Migration
    {
        public override void Up()
        {
            Alter.Table("NabChq")
                .AddColumn("maxVouchers")
                    .AsFixedLengthAnsiString(6)
                    .Nullable()
                .AddColumn("customerLinkNumber")
                    .AsFixedLengthAnsiString(10)
                    .Nullable()
                .AddColumn("isGeneratedBulkCredit")
                    .AsFixedLengthAnsiString(1)
                    .Nullable();
        }
        public override void Down()
        {
            Delete.Column("maxVouchers")
                .FromTable("NabChq");

            Delete.Column("customerLinkNumber")
                .FromTable("NabChq");

            Delete.Column("isGeneratedBulkCredit")
                .FromTable("NabChq");
        }
    }
}
