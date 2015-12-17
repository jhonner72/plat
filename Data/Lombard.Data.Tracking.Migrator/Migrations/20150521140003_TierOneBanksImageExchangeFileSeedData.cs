using System.Configuration;
using FluentMigrator;
using System.IO;

namespace Lombard.Data.Tracking.Migrator.Migrations
{
    [Migration(20150521140003)]
    public class TierOneBanksImageExchangeFileSeedData : Migration
    {
        public override void Up()
        {
            var environment = ConfigurationManager.AppSettings["environment"];

            if (string.IsNullOrEmpty(environment))
            {
                throw new ConfigurationErrorsException("Cannot find a value for environment in the configuration file");
            }

            var contentName = string.Format(@"Migrations\20150521140003.{0}.json", environment);

            
            using (StreamReader sr = new StreamReader(contentName))
            {
                Insert.IntoTable("ref_metadata").Row(new { ref_name = "TierOneBanksImageExchange", ref_value = sr.ReadToEnd() });
            }

            
        }

        public override void Down()
        {
            Delete.FromTable("ref_metadata").Row(new { ref_name = "TierOneBanksImageExchange"});
        }
    }
}
