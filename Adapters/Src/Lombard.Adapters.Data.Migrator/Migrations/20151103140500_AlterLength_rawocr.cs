using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    [Migration(20151103140500)]
    public class _20151103140500_AlterLength_rawocr:Migration
    {
        public override void Up()
        {
            Alter.Table("NabChq")
                .AlterColumn("raw_ocr")
                    .AsFixedLengthAnsiString(128)
                    .Nullable();
        }

        public override void Down()
        {

            Alter.Table("NabChq")
                .AlterColumn("raw_ocr")
                    .AsFixedLengthAnsiString(64)
                    .Nullable();

        }
         
    }
}
