using System.Data.Entity;
using Lombard.Ingestion.Data.Domain;

namespace Lombard.Ingestion.Data.Repository
{
    public class TrackingDbContext : DbContext
    {
        public TrackingDbContext()
            : base("name=TrackingDb")
        {
        }

        public virtual DbSet<RefBatchAudit> BatchAudits { get; set; }
    }
}
