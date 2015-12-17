﻿using System.Configuration;
using FluentMigrator;
using System.IO;

namespace Lombard.Data.Tracking.Migrator.Migrations
{
    [Migration(20150521140001)]
    public class StateOrdinalsSeedData : Migration
    {
        public override void Up()
        {
            using (StreamReader sr = new StreamReader("Migrations\\20150521140001.json"))
            {
                Insert.IntoTable("ref_metadata").Row(new { ref_name = "StateOrdinals", ref_value = sr.ReadToEnd() });
            }

        }

        public override void Down()
        {
            Delete.FromTable("ref_metadata").Row(new { ref_name = "StateOrdinals"});
        }
    }
}
