using CommandLine;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lombard.Data.SSRS.Migrator
{
    public class Options
    {
        [Option('n', "name",
            HelpText = "Connection String name in configuration",
            DefaultValue = "SQLServer")]
        public string ConnectionStringName { get; set; }

        [Option('p', "profile",
            HelpText = "Profile name is either TrackingProfile or DocumentumProfile",
            DefaultValue = "TrackingProfile")]
        public string Profile { get; set; }
    }
}
