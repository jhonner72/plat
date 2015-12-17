using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace Lombard.Common.Jobs
{
    /// <summary>
    /// A job that can be processed on a seperate thread
    /// </summary>
    /// <typeparam name="TData"></typeparam>
    public interface IJob<TData>
    {
        /// <summary>
        /// The data to pass to the job.
        /// </summary>
        TData Data { get; set; }

        /// <summary>
        /// Process the job.
        /// </summary>
        /// <remarks>It is the jobs responsibility to handle the cancellation token requests e.g. cancellationToken.ThrowIfCancellationRequested</remarks>
        /// <param name="cancellationToken">The cancellation token to indicate that the (thread) processing should be forceably terminated.</param>
        Task ProcessAsync(CancellationToken cancellationToken);
    }
}
