using System.Threading.Tasks;

namespace Lombard.Common.Data.Query
{
    public interface IQueryExecutor
    {
        Task<TResult> QueryAsync<TResult>(IQuery<TResult> query);

        ITransactionScope CreateTransaction();
    }
}