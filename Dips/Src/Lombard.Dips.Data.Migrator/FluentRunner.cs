using System;
using System.Diagnostics;
using System.Reflection;
using FluentMigrator.Runner.Announcers;
using FluentMigrator.Runner.Initialization;

namespace Lombard.Dips.Data.Migrator
{
    public class FluentRunner
    {
        private readonly string connectionString;
        private readonly string database;
        private readonly Assembly migrationAssembly;
        private string task;
        private long version;

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
            Execute();
        }

        private void Execute()
        {
            var runnerContext = new RunnerContext(GetAnnouncer())
            {
                Database = database,
                Task = task,
                Connection = connectionString,
                Target = migrationAssembly.CodeBase.Replace("file:///", string.Empty),
                Version = version
            };

            Trace.TraceInformation("#\n# Executing migration task {0}...\n#\n", task);
            var localTask = new TaskExecutor(runnerContext);
            localTask.Execute();
            Trace.TraceInformation("\n#\n# Task {0} complete!\n#", task);
        }

        private Announcer GetAnnouncer()
        {
            return new TextWriterAnnouncer(Console.Out) { ShowElapsedTime = true, ShowSql = true };
        }
    }
}
