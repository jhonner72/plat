using System.Configuration;
using FluentMigrator;
using System.IO;

namespace Lombard.Data.Tracking.Migrator.Migrations
{
    [Migration(20150807000001)]
    public class ReportAgencyBanks : Migration
    {
        public override void Up()
        {
            using (StreamReader sr = new StreamReader("Migrations\\20150807000001.json"))
            {
                Insert.IntoTable("ref_metadata").Row(new { ref_name = "ReportAgencyBanksMetadata", ref_value = sr.ReadToEnd() });
            }

        }

        public override void Down()
        {
            Delete.FromTable("ref_metadata").Row(new { ref_name = "ReportAgencyBanksMetadata"});
        }
    }
}
