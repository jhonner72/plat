using Lombard.Ingestion.Data.Domain;

namespace Lombard.Ingestion.Data.Repository
{
    public interface IIngestionRepository
    {
        void Add(RefBatchAudit refBatchAudit);
        int SaveChanges();
    }
}