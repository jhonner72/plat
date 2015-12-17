using FluentMigrator;
using FluentMigrator.Expressions;
using FluentMigrator.Runner.Extensions;
using System.IO;
using System.Configuration;
using System;

namespace Lombard.Data.Tracking.Migrator.Migrations
{
    [Profile("SeedData")]
    public class UpdateMetadataTable : Migration
    {
        public override void Up()
        {
            SeedData();
        }

        private void SeedData()
        {
            this.DeleteRefMetadataData();

            this.AgencyBanksIE();
            this.AssetManagement();
            this.BusinessCalendar();
            this.FinancialEntities();
            this.ForValue();
            this.IncidentTypes();
            this.TierOneBankIE();
            this.ReportMetadata();
            this.StateOrdinal();
            this.ValueInstructionFile();
            this.EndOfDayWorkflow();
            this.ReportAgencyBanks();
            this.AdjustmentLetterDetails();
            this.CorporateDetails();
            this.SchedulerReferences();
            this.AusPostECLMatch();
            this.EncodedDummyImage();
        }

        public override void Down()
        {
            Delete.Table("ref_metadata");
        }

        private void DeleteRefMetadataData()
        {
            Delete.FromTable("ref_metadata").AllRows();
        }

        private void AgencyBanksIE()
        {
            using (StreamReader sr = new StreamReader("Migrations\\20150521140004.json"))
            {
                Insert.IntoTable("ref_metadata").Row(new { ref_id = 1, ref_name = "AgencyBanksImageExchange", ref_value = sr.ReadToEnd() }).WithIdentityInsert();
            }
        }

        private void AssetManagement()
        {
            using (StreamReader sr = new StreamReader("Migrations\\20150611000002.json"))
            {
                Insert.IntoTable("ref_metadata").Row(new { ref_id = 2, ref_name = "AssetManagement", ref_value = sr.ReadToEnd() }).WithIdentityInsert();
            }
        }

        private void BusinessCalendar()
        {
            using (StreamReader sr = new StreamReader("Migrations\\20150625000002_BusinessCalendar.json"))
            {
                string value = sr.ReadToEnd();

                value = value.Replace("###businessDay###", DateTime.Today.ToUniversalTime().ToString(@"yyyy-MM-ddTHH:mm:ss.fff""+0000"""));

                Insert.IntoTable("ref_metadata").Row(new { ref_id = 3, ref_name = "BusinessCalendar", ref_value = value }).WithIdentityInsert();
            }
        }

        private void FinancialEntities()
        {
            using (StreamReader sr = new StreamReader("Migrations\\20150521140002.json"))
            {
                Insert.IntoTable("ref_metadata").Row(new { ref_id = 4, ref_name = "FinancialEntities", ref_value = sr.ReadToEnd() }).WithIdentityInsert();
            }
        }

        private void ForValue()
        {
            using (StreamReader sr = new StreamReader("Migrations\\20150527000001.json"))
            {
                Insert.IntoTable("ref_metadata").Row(new { ref_id = 5, ref_name = "ForValueReference", ref_value = sr.ReadToEnd() }).WithIdentityInsert();
            }
        }

        private void IncidentTypes()
        {
            using (StreamReader sr = new StreamReader("Migrations\\20150611000001.json"))
            {
                Insert.IntoTable("ref_metadata").Row(new { ref_id = 6, ref_name = "IncidentTypes", ref_value = sr.ReadToEnd() }).WithIdentityInsert();
            }
        }

        private void TierOneBankIE()
        {
            var environment = ConfigurationManager.AppSettings["environment"];

            if (string.IsNullOrEmpty(environment))
            {
                throw new ConfigurationErrorsException("Cannot find a value for environment in the configuration file");
            }

            var contentName = string.Format(@"Migrations\20150521140003.{0}.json", environment);

            using (StreamReader sr = new StreamReader(contentName))
            {
                Insert.IntoTable("ref_metadata").Row(new { ref_id = 7, ref_name = "TierOneBanksImageExchange", ref_value = sr.ReadToEnd() }).WithIdentityInsert();
            }
        }

        private void ReportMetadata()
        {
            using (StreamReader sr = new StreamReader("Migrations\\20150610000001.json"))
            {
                Insert.IntoTable("ref_metadata").Row(new { ref_id = 8, ref_name = "ReportMetadata", ref_value = sr.ReadToEnd() }).WithIdentityInsert();
            }
        }

        private void StateOrdinal()
        {
            using (StreamReader sr = new StreamReader("Migrations\\20150521140001.json"))
            {
                Insert.IntoTable("ref_metadata").Row(new { ref_id = 9, ref_name = "StateOrdinals", ref_value = sr.ReadToEnd() }).WithIdentityInsert();
            }
        }

        private void ValueInstructionFile()
        {
            var environment = ConfigurationManager.AppSettings["environment"];

            if (string.IsNullOrEmpty(environment))
            {
                throw new ConfigurationErrorsException("Cannot find a value for environment in the configuration file");
            }

            var contentName = string.Format(@"Migrations\20150521140000.{0}.json", environment);

            using (StreamReader sr = new StreamReader(contentName))
            {
                Insert.IntoTable("ref_metadata").Row(new { ref_id = 10, ref_name = "ValueInstructionFile", ref_value = sr.ReadToEnd() }).WithIdentityInsert();
            }
        }

        private void EndOfDayWorkflow()
        {
            using (StreamReader sr = new StreamReader("Migrations\\20150703000001.json"))
            {
                Insert.IntoTable("ref_metadata").Row(new { ref_id = 11, ref_name = "EndOfDayWorkflow", ref_value = sr.ReadToEnd() }).WithIdentityInsert();
            }
        }

        private void ReportAgencyBanks()
        {
            using (StreamReader sr = new StreamReader("Migrations\\20150807000001.json"))
            {
                Insert.IntoTable("ref_metadata").Row(new { ref_id = 12, ref_name = "ReportAgencyBanksMetadata", ref_value = sr.ReadToEnd() }).WithIdentityInsert();
            }
        }

        private void AdjustmentLetterDetails()
        {
            using (StreamReader sr = new StreamReader("Migrations\\AdjustmentLettersMetadata.json"))
            {
                Insert.IntoTable("ref_metadata").Row(new { ref_id = 13, ref_name = "AdjustmentLettersMetadata", ref_value = sr.ReadToEnd() }).WithIdentityInsert();
            }
        }

        private void CorporateDetails()
        {
            using (StreamReader sr = new StreamReader("Migrations\\Corporate.json"))
            {
                Insert.IntoTable("ref_metadata").Row(new { ref_id = 14, ref_name = "CorporateMetadata", ref_value = sr.ReadToEnd() }).WithIdentityInsert();
            }
        }

        private void SchedulerReferences()
        {
            using (StreamReader sr = new StreamReader("Migrations\\SchedulerReferenceMetadata.json"))
            {
                Insert.IntoTable("ref_metadata").Row(new { ref_id = 15, ref_name = "SchedulerReference", ref_value = sr.ReadToEnd() }).WithIdentityInsert();
            }
        }

        private void AusPostECLMatch()
        {
            using (StreamReader sr = new StreamReader("Migrations\\AusPostECLMatchSeedData.json"))
            {
                Insert.IntoTable("ref_metadata").Row(new { ref_id = 16, ref_name = "AusPostECLMatch", ref_value = sr.ReadToEnd() }).WithIdentityInsert();
            }
        }

        private void EncodedDummyImage()
        {
            using (StreamReader sr = new StreamReader("Migrations\\EncodedDummyImageSeedData.json"))
            {
                Insert.IntoTable("ref_metadata").Row(new { ref_id = 17, ref_name = "EncodedDummyImage", ref_value = sr.ReadToEnd() }).WithIdentityInsert();
            }
        }
	}
}
