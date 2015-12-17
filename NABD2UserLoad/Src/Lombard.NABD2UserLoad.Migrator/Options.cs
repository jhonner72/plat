using CommandLine;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lombard.NABD2UserLoad.Migrator
{
    public class Options
    {
        [Option('n', "name",
            HelpText = "Connection String name in configuration",
            DefaultValue = "SQLServer")]
        public string ConnectionStringName { get; set; }
    }
}
