using FluentMigrator;
using FluentMigrator.Expressions;
using System.IO;
using System.Configuration;
using System;

namespace Lombard.Data.Tracking.Migrator.Migrations
{
    [Migration(20150514000020)]
    public class CreateMetadataTable : Migration
    {
        public override void Up()
        {
            Create.Table("ref_metadata")
                .WithColumn("ref_id").AsInt32().NotNullable().PrimaryKey("pk_ref_metadata").Identity()
                .WithColumn("ref_name").AsString(50).NotNullable()
                .WithColumn("ref_value").AsMaxString().NotNullable()
                .WithColumn("modified_date").AsDateTime().NotNullable().WithDefault(SystemMethods.CurrentDateTime)
                .WithColumn("modified_by").AsString(50).NotNullable().WithDefault(SystemMethods.CurrentUser);
        }

        public override void Down()
        {
            Delete.Table("ref_metadata");
        }
    }
}
