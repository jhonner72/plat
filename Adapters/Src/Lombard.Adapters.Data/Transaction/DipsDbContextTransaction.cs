using System.Data.Entity;

namespace Lombard.Adapters.Data.Transaction
{
    public sealed class DipsDbContextTransaction : IDipsDbContextTransaction
    {
        private readonly DbContextTransaction transaction;

        public DipsDbContextTransaction(DbContextTransaction transaction)
        {
            this.transaction = transaction;
        }

        public void Commit()
        {
            transaction.Commit();
        }

        public void Rollback()
        {
            transaction.Rollback();
        }

        public void Dispose()
        {
            transaction.Dispose();
        }
    }
}
