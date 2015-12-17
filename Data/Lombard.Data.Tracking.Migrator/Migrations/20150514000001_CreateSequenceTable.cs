using FluentMigrator;
using System;

namespace Lombard.Data.Tracking.Migrator.Migrations
{
    [Migration(20150514000005)]
    public class CreateSequenceTable : Migration
    {
        public override void Up()
        {
             var isInsert = true;
             if (!Schema.Table("ref_sequence").Exists())
             {
                 Create.Table("ref_sequence")
                     .WithColumn("sequence_name").AsString(100).NotNullable().PrimaryKey("pk_ref_sequence")
                     .WithColumn("sequence_number").AsInt32().NotNullable()
                     .WithColumn("reset_point").AsDateTime().NotNullable().WithDefault(SystemMethods.CurrentDateTime)
                     .WithColumn("reset_sequence_number").AsInt32().NotNullable();

                     isInsert = true;
             }
             else
                 isInsert = false;

             SeedData(isInsert);
        }

        public override void Down()
        {
            Delete.Table("ref_sequence");
        }

        private void SeedData(bool isInsert)
        {
            if (isInsert)
            {
                Insert.IntoTable("ref_sequence").Row(new { sequence_name = "ValueInstructionFile", sequence_number = 8000, reset_point = DateTime.Now, reset_sequence_number = 8000 });
                Insert.IntoTable("ref_sequence").Row(new { sequence_name = "TierOneBanksImageExchange", sequence_number = 1, reset_point = DateTime.Now, reset_sequence_number = 1 });
                Insert.IntoTable("ref_sequence").Row(new { sequence_name = "AgencyBanksImageExchange", sequence_number = 1, reset_point = DateTime.Now, reset_sequence_number = 1 });
            }
            else
            {
                Update.Table("ref_sequence").Set(new { sequence_number = 8000, reset_point = DateTime.Now, reset_sequence_number = 8000 }).Where(new { sequence_name = "ValueInstructionFile" });
                Update.Table("ref_sequence").Set(new { sequence_number = 1, reset_point = DateTime.Now, reset_sequence_number = 1 }).Where(new { sequence_name = "TierOneBanksImageExchange" });
                Update.Table("ref_sequence").Set(new { sequence_number = 1, reset_point = DateTime.Now, reset_sequence_number = 1 }).Where(new { sequence_name = "AgencyBanksImageExchange" });
            }
        }
    }
}
