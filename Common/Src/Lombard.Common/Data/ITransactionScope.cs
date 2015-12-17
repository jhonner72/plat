using System;

namespace Lombard.Common.Data
{
    public interface ITransactionScope : IDisposable
    {
        void Commit();
        void Rollback();
    }
}
