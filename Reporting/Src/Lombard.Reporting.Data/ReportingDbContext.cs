using System.Configuration;
using System.Data.Common;
using System.Data.Entity;
using System.Data.Entity.Infrastructure;

namespace Lombard.Reporting.Data
{
    public class ReportingDbContext : DbContext
    {
        public ReportingDbContext(DbConnection dbConnection) :
            base(dbConnection, true)
        {
            int commandTimeout;
            if (int.TryParse(ConfigurationManager.AppSettings["reporting:CommandTimeout"], out commandTimeout))
            {
                var objectContext = (this as IObjectContextAdapter).ObjectContext;
                objectContext.CommandTimeout = commandTimeout;
            }
        }
    }
}
