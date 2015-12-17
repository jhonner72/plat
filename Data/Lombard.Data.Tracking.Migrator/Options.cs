using CommandLine;

namespace Lombard.Data.Tracking.Migrator
{
    public class Options
    {
        [Option('n', "name",
            HelpText = "Connection String name in configuration",
            DefaultValue = "SQLServer")]
        public string ConnectionStringName { get; set; }

        [Option('c', "create",
            HelpText = "Create database if not exists",
            DefaultValue = false)]
        public bool CreateDatabase { get; set; }
    }
}