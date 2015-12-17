using System.Configuration;
using FluentMigrator;
using System.IO;

namespace Lombard.Data.Tracking.Migrator.Migrations
{
    [Migration(20150521140002)]
    public class FinancialEntitiesSeedData : Migration
    {
        public override void Up()
        {
            using (StreamReader sr = new StreamReader("Migrations\\20150521140002.json"))
            {
                Insert.IntoTable("ref_metadata").Row(new { ref_name = "FinancialEntities", ref_value = sr.ReadToEnd() });
            }

        }

        public override void Down()
        {
            Delete.FromTable("ref_metadata").Row(new { ref_name = "FinancialEntities"});
        }
    }
}
