using System.Diagnostics.CodeAnalysis;
using System.Threading.Tasks;

namespace Lombard.Common.Data.Query
{
    public abstract class QueryAdapter<TResult>
    {
        public abstract Task<TResult> QueryAsync(object query);
    }

    [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1402:FileMayOnlyContainASingleClass", Justification = "Reviewed.")]
    public class QueryAdapter<TQuery, TResult>
        : QueryAdapter<TResult>
        where TQuery : IQuery<TResult>
    {
        private readonly IQueryHandler<TQuery, TResult> handler;

        public QueryAdapter(IQueryHandler<TQuery, TResult> handler)
        {
            this.handler = handler;
        }

        public override Task<TResult> QueryAsync(object query)
        {
            return handler.QueryAsync((TQuery)query);
        }
    }
}