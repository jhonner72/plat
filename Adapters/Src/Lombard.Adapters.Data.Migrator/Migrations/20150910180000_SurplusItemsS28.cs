using FluentMigrator;
using System;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    /// <summary>
    /// This migration adds tables required for surplus items as part of sprint 28
    /// </summary>
    [Migration(20150910180000)]
    public class SurplusItemsS28 : Migration
    {
        public override void Up()
        {
            Create.Table("request")
                .WithColumn("guid_name")
                    .AsFixedLengthAnsiString(50)
                    .NotNullable()
                .WithColumn("payload")
                    .AsAnsiString(2000)
                    .NotNullable()
                .WithColumn("request_time")
                    .AsDateTime()
                    .NotNullable();


            Create.Table("response_done")
               .WithColumn("guid_name")
                   .AsFixedLengthAnsiString(50)
                   .NotNullable()
               .WithColumn("response_time")
                    .AsDateTime()
                    .NotNullable();

            Create.Table("response_data")
                .WithColumn("guid_name")
                    .AsFixedLengthAnsiString(50)
                    .NotNullable()
                .WithColumn("doc_ref_number")
                    .AsFixedLengthAnsiString(60)
                    .NotNullable()
                .WithColumn("payload")
                    .AsAnsiString(Int32.MaxValue)
                    .NotNullable()
                .WithColumn("front_image")
                    .AsAnsiString(Int32.MaxValue)
                    .NotNullable()
                .WithColumn("rear_image")
                    .AsAnsiString(Int32.MaxValue)
                    .NotNullable();
                

        }

        public override void Down()
        {

            Delete.Table("request");
            Delete.Table("response_done");
            Delete.Table("response_data");

            
        }
    }
}
