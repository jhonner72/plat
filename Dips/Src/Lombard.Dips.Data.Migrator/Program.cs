﻿using System;
using Serilog;
using Config = System.Configuration.ConfigurationManager;

namespace Lombard.Dips.Data.Migrator
{
    class Program
    {
        public static void Main(string[] args)
        {
            var log = new LoggerConfiguration()
                .WriteTo.Console()
                .MinimumLevel.Debug()
                .CreateLogger();

            var options = new Options();
            if (CommandLine.Parser.Default.ParseArguments(args, options) == false)
            {
                log.Fatal("Problem parsing options!");
                Environment.Exit(-1);
            }

            log.Information("Processing migrations");
            var connectionStringVal = Config.ConnectionStrings[options.ConnectionStringName];
            if (connectionStringVal == null)
            {
                log.Fatal("ERROR: Unable to get connection string from configuration");
                Environment.Exit(-2);
            }

            var connectionString = connectionStringVal.ConnectionString;
            log.Debug("Connection string is {connectionString}", connectionString);

            var runner = new FluentRunner(connectionString, typeof(DipsContext).Assembly);

            try
            {
                runner.MigrateToLatest();
            }
            catch (Exception e)
            {
                log.Fatal(e, "ERROR: problem while running migrations!");
                Environment.Exit(-3);
            }

            log.Information("Migrations run successfully");
        }
    }
}
