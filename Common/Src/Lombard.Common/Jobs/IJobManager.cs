using System;
using System.Threading.Tasks;

namespace Lombard.Common.Jobs
{
    /// <summary>
    /// A job manager to orchestrate processing in a multi-threaded environment.
    /// Listens for jobs requests on a seperate thread, and spawns new threads for processing each job.
    /// </summary>
    /// <typeparam name="TData">The type of data that will be consumed by the job</typeparam>
    public interface IJobManager<TData> : IDisposable
    {
        /// <summary>
        /// Start listenening for and processing jobs on the queue. 
        /// </summary>
        void Start();

        /// <summary>
        /// Queue a new job. Can be called before or after 'Start'
        /// </summary>
        /// <param name="data">The data to pass to the job</param>
        void QueueJob(TData data);

        /// <summary>
        /// Complete processing jobs. 
        /// </summary>
        /// <param name="forceCancel">Should jobs be forceably cancelled</param>
        /// <returns></returns>
        Task CompleteAsync(bool forceCancel = true);
    }
}
