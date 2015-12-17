﻿using FluentMigrator.Runner.Announcers;
using FluentMigrator.Runner.Initialization;
using System;
using System.Diagnostics;
using System.IO;
using System.Reflection;

namespace Lombard.NABD2UserLoad.Migrator
{
    public class FluentRunner
    {
        private readonly string connectionString;
        private readonly string database;
        private readonly Assembly migrationAssembly;
        private string task;
        private long version;
        private string profile;

        public FluentRunner(string connectionString, Assembly migrationAssembly, string database = "SqlServer2012")
        {
            this.connectionString = connectionString;
            this.migrationAssembly = migrationAssembly;
            this.database = database;
        }

        public void MigrateTo(long versionTo)
        {
            version = versionTo;
            task = version == 0 ? "rollback:all" : "rollback:toversion";
            Execute();
        }

        public void MigrateToLatest()
        {
            task = "migrate:up";
            profile = "AlwaysRun";
            Execute();
        }

        private void Execute()
        {
            var runnerContext = new RunnerContext(GetAnnouncer())
            {
                Database = database,
                Task = task,
                Connection = connectionString,
                Targets = new[] { migrationAssembly.CodeBase.Replace("file:///", string.Empty) },
                Version = version,
                Profile = profile
            };

            Trace.TraceInformation("#\n# Executing migration task {0}...\n#\n", task);
            var localTask = new TaskExecutor(runnerContext);
            localTask.Execute();
            Trace.TraceInformation("\n#\n# Task {0} complete!\n#", task);
        }

        private Announcer GetAnnouncer()
        {
            //return new TextWriterAnnouncer(new StreamWriter(@"C:\Logs\SsrsMigrator.log")) { ShowElapsedTime = true, ShowSql = true };
            return new TextWriterAnnouncer(Console.Out) { ShowElapsedTime = true, ShowSql = true };
        }
    }
}