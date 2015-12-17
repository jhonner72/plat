using System.Configuration;
using FluentMigrator;
using System.IO;
using System;

namespace Lombard.Data.Tracking.Migrator.Migrations
{
    [Migration(20150521140005)]
    public class SequenceNumberSeedData : Migration
    {
        public override void Up()
        {
            Insert.IntoTable("ref_sequence").Row(new { sequence_name = "ValueInstructionFile", sequence_number = 8000, reset_point = DateTime.Now, reset_sequence_number = 8000 });
            Insert.IntoTable("ref_sequence").Row(new { sequence_name = "TierOneBanksImageExchange", sequence_number = 1, reset_point = DateTime.Now, reset_sequence_number = 1 });
            Insert.IntoTable("ref_sequence").Row(new { sequence_name = "AgencyBanksImageExchange", sequence_number = 1, reset_point = DateTime.Now, reset_sequence_number = 1 });
        }

        public override void Down()
        {
            Delete.FromTable("ref_sequence").Row(new { sequence_name = "ValueInstructionFile"});
            Delete.FromTable("ref_sequence").Row(new { sequence_name = "TierOneBanksImageExchange"});
            Delete.FromTable("ref_sequence").Row(new { sequence_name = "AgencyBanksImageExchange"});
        }
    }
}
