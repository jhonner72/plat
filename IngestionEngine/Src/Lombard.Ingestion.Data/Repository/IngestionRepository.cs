using Lombard.Ingestion.Data.Domain;

namespace Lombard.Ingestion.Data.Repository
{
    public class IngestionRepository : IIngestionRepository
    {
        private TrackingDbContext context;

        public IngestionRepository(TrackingDbContext context)
        {
            this.context = context;
        }

        public void Add(RefBatchAudit refBatchAudit)
        {
            this.context.BatchAudits.Add(refBatchAudit);
        }

        public int SaveChanges()
        {
            return this.context.SaveChanges();
        }
    }
}
