using FluentMigrator.Runner.Announcers;
using Serilog;

namespace Lombard.Dips.Data
{
    public class SerilogAnnouncer : Announcer
    {
        private readonly ILogger logger;

        public SerilogAnnouncer(ILogger logger)
        {
            this.logger = logger;
        }

        public override void Write(string message, bool escaped)
        {
            logger.Information(message);
        }

        public override void Error(string message)
        {
            logger.Error(message);
        }

        public override void Heading(string message)
        {
            logger.Information("Migration Heading: {heading}", message);
        }

        public override void Emphasize(string message)
        {
            logger.Warning(message);
        }

        public override void Sql(string sql)
        {
            logger.Information("SQL string: {migrationSQL}", sql);
        }
    }
}
