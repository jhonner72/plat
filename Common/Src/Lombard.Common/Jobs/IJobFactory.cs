using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace Lombard.Common.Jobs
{
    /// <summary>
    /// A simple factory to spawn new job instances for threaded processing
    /// </summary>
    /// <typeparam name="TData">The type of data that will be consumed by the job</typeparam>
    public interface IJobFactory<TData>
    {
        /// <summary>
        /// Create a new instance of a job
        /// </summary>
        /// <param name="data"></param>
        /// <returns>A new instance of a job</returns>
        IJob<TData> CreateJob(TData data);
    }
}
