using Autofac;
using Lombard.Common.Queues;
using Lombard.Scheduler.Configuration;
using Lombard.Scheduler.Constants;
using Lombard.Scheduler.Utils;
using Lombard.Vif.Service.Messages.XsdImports;
using Serilog;
using System;
using System.ComponentModel;

namespace Lombard.Scheduler.Domain
{
    public interface ILockedBoxCorp
    {
        [DisplayName("{2} for {1}")]
        void ProcessTask(string processName, string batchType, string workType);
    }

    public class LockedBoxCorp : ILockedBoxCorp
    {
        private IExchangePublisher<Job> publisher;
        private IComponentContext iContainer = null;
        private IQueueConfiguration queueConfiguration;

        public LockedBoxCorp(IComponentContext container)
        {
            this.iContainer = container;

            queueConfiguration = iContainer.Resolve<IQueueConfiguration>();
            publisher = iContainer.Resolve<IExchangePublisher<Job>>();
            publisher.Declare(queueConfiguration.ResponseExchangeName);
        }

        public void ProcessTask(string processName, string batchType, string workType)
        {
            try
            {
                Log.Information("LockedBoxCorp: background task is started.");

                var job = TaskHelper.GenerateJob("NLBC", Subject.LockedBox, Predicate.LockedBox, new Parameter[] {
                    new Parameter() { name = "batchType", value = batchType },
                    new Parameter() { name = "workType", value = workType }
                });
                Log.Information("LockedBoxCorp: Job object created successfully as per the schema.");

                publisher.PublishAsync(job, Guid.NewGuid().ToString());
                Log.Information("LockedBoxCorp: Message published successfully to RabbitMQ.");
            }
            catch (Exception ex)
            {
                Log.Error(ex, "LockedBoxCorp: There was unexpected error occured in LockedBox.ProcessTask()");
                throw ex;
            }
        }
    }
}