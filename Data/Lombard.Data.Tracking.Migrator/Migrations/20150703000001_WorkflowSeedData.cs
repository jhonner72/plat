using System.Configuration;
using FluentMigrator;
using System.IO;

namespace Lombard.Data.Tracking.Migrator.Migrations
{
    [Migration(20150703000001)]
    public class WorkflowSeedData : Migration
    {
        public override void Up()
        {
            using (StreamReader sr = new StreamReader("Migrations\\20150703000001.json"))
            {
                Insert.IntoTable("ref_metadata").Row(new { ref_name = "EndOfDayWorkflow", ref_value = sr.ReadToEnd() });
            }

        }

        public override void Down()
        {
            Delete.FromTable("ref_metadata").Row(new { ref_name = "EndOfDayWorkflow"});
        }
    }
}
