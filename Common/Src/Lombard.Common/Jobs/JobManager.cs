using Serilog;
using System;
using System.Collections.Concurrent;
using System.Threading;
using System.Threading.Tasks;

namespace Lombard.Common.Jobs
{
    public sealed class JobManager<TData> : IJobManager<TData>
    {
        private readonly BlockingCollection<TData> jobQueue = new BlockingCollection<TData>();
        private readonly CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
        private readonly ConcurrentBag<Task> tasks = new ConcurrentBag<Task>();

        private readonly IJobFactory<TData> jobFactory;

        private bool started;

        public JobManager(IJobFactory<TData> jobFactory)
        {
            this.jobFactory = jobFactory;
        }

        public void Start()
        {
            if (started)
                return;

            started = true;

            Log.Information("Start {0} job manager", typeof(TData).Name);

            //start listener on separate thread
            var task = Task.Run(() => Listen(jobQueue, cancellationTokenSource.Token, jobFactory, tasks), cancellationTokenSource.Token);
            
            tasks.Add(task);
        }

        public void QueueJob(TData data)
        {
            Log.Debug("Queuing {0} job : {@data}", typeof(TData).Name, data);

            if (jobQueue.IsAddingCompleted)
                throw new InvalidOperationException("The queue has been completed, and is not accepting any new job processing requests.");

            try
            {
                jobQueue.Add(data, cancellationTokenSource.Token);
            }
            catch (OperationCanceledException ocex)
            {
                Log.Error(ocex, "Job queering was forcibly stopped by the job manager");
                jobQueue.CompleteAdding();
            }
        }

        public async Task CompleteAsync(bool forceCancel = false)
        {
            if (!started)
                return;

            Log.Debug("Stopping the job queue, forceCancel :  {0}", forceCancel);

            jobQueue.CompleteAdding();

            if (!forceCancel)
            {
                await Task.WhenAll(tasks.ToArray());
            }

            cancellationTokenSource.Cancel();

            started = false;
        }

        private static void Listen(BlockingCollection<TData> jobQueue, CancellationToken ct, IJobFactory<TData> jobFactory, ConcurrentBag<Task> tasks)
        {
            Log.Debug("Listening for {0} jobs", typeof(IJob<TData>).Name);

            try
            {
                foreach (var tdata in jobQueue.GetConsumingEnumerable(ct))
                {
                    //start job on separate thread
                    var data = tdata;
                    var task = Task.Run(async () =>
                    {
                        try
                        {
                            var job = jobFactory.CreateJob(data);
                            await job.ProcessAsync(ct);
                        }
                        catch (Exception ex)
                        {
                            Log.Error(ex, "An unexpected exception occurred while processing the job.");
                        }
                    }, ct);
                    
                    //add task to running jobs
                    tasks.Add(task);
                }
            }
            catch (OperationCanceledException ocex)
            {
                Log.Error(ocex, "Job listening was forcibly cancelled by the job manager.");
            }

        }

        public void Dispose()
        {
            try
            {
                if (started)
                    Task.WaitAll(CompleteAsync());

                jobQueue.Dispose();
            }
            catch (AggregateException ex)
            {
                Log.Fatal(ex, "JobManager shut down failed");
            }
        }
    }
}
