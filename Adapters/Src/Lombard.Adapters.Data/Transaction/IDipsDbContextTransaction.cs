using System;

namespace Lombard.Adapters.Data.Transaction
{
    public interface IDipsDbContextTransaction : IDisposable
    {
        void Commit();
        void Rollback();
    }
}