using System.Threading.Tasks;

namespace Lombard.Common.Data.Query
{
    public interface IQueryHandler<in TQuery, TResult>
        where TQuery : IQuery<TResult>
    {
        Task<TResult> QueryAsync(TQuery query);
    }
}