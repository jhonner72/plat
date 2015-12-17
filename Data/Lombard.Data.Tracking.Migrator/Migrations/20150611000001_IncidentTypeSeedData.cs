using System.Configuration;
using FluentMigrator;
using System.IO;

namespace Lombard.Data.Tracking.Migrator.Migrations
{
    [Migration(2015061100001)]
    public class IncidentTypesSeedData : Migration
    {
        public override void Up()
        {
            using (StreamReader sr = new StreamReader("Migrations\\2015061100001.json"))
            {
                Insert.IntoTable("ref_metadata").Row(new { ref_name = "IncidentTypes", ref_value = sr.ReadToEnd() });
            }

        }

        public override void Down()
        {
            Delete.FromTable("ref_metadata").Row(new { ref_name = "IncidentTypes"});
        }
    }
}
