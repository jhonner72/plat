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
    public interface IAgencyBanks
    {
        [DisplayName("Agency Bank")]
        void ProcessTask();
    }

    public class AgencyBanks : IAgencyBanks
    {
        private IComponentContext iContainer = null;
        private IExchangePublisher<Job> publisher;
        private IQueueConfiguration queueConfiguration;

        public AgencyBanks(IComponentContext container)
        {
                this.iContainer = container;
                queueConfiguration = iContainer.Resolve<IQueueConfiguration>();
                publisher = iContainer.Resolve<IExchangePublisher<Job>>();
                publisher.Declare(queueConfiguration.ResponseExchangeName);
        }

        public void ProcessTask()
        {
            try
            {
                Log.Information("NIET: Agency Bank Image Exchange was started.");

                //Send RabbitMQ message to trigger Agency Bank BPM process
                var job = TaskHelper.GenerateJob("NIET", Subject.AgencyBanks, Predicate.AgencyBanks);
                Log.Information("NIET: Job object created successfully as per the schema.");

                publisher.PublishAsync(job, Guid.NewGuid().ToString());
                Log.Information("NIET: Message published successfully to RabbitMQ.");
            }
            catch(Exception ex)
            {
                Log.Error("NIET: There was unexpected error occured in AgencyBanks.ProcessTask() with the stack trace information as {stackTrace}", ex.ToString());
                 throw new InvalidOperationException();
            }
        }
    }
}