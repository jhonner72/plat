using System.Configuration;
using FluentMigrator;
using System.IO;

namespace Lombard.Data.Tracking.Migrator.Migrations
{
    [Migration(20150521140004)]
    public class AgencyBanksImageExchangeSeedData : Migration
    {
        public override void Up()
        {
            using (StreamReader sr = new StreamReader("Migrations\\20150521140004.json"))
            {
                Insert.IntoTable("ref_metadata").Row(new { ref_name = "AgencyBanksImageExchange", ref_value = sr.ReadToEnd() });
            }

        }

        public override void Down()
        {
            Delete.FromTable("ref_metadata").Row(new { ref_name = "AgencyBanksImageExchange"});
        }
    }
}
