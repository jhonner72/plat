using System.Threading.Tasks;
using Autofac;

namespace Lombard.Common.Data.Query
{
    public abstract class QueryExecutor
        : IQueryExecutor
    {
        private readonly ILifetimeScope scope;

// ReSharper disable once PublicConstructorInAbstractClass
        public QueryExecutor(ILifetimeScope scope)
        {
            this.scope = scope;
        }

        public abstract ITransactionScope CreateTransaction();

        public Task<TResult> QueryAsync<TResult>(IQuery<TResult> query)
        {
            var handlerType = typeof(QueryAdapter<,>).MakeGenericType(query.GetType(), typeof(TResult));
            var handler = (QueryAdapter<TResult>) scope.Resolve(handlerType);

            return handler.QueryAsync(query);
        }
    }
}