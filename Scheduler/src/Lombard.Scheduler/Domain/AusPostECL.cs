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
    public interface IAusPostECL
    {
        [DisplayName("AusPost ECL")]
        void ProcessTask(string workType);
    }

    public class AusPostECL : IAusPostECL
    {
        private IComponentContext iContainer = null;
        private IExchangePublisher<Job> publisher;
        private IQueueConfiguration queueConfiguration;

        public AusPostECL(IComponentContext container)
        {
            this.iContainer = container;
            queueConfiguration = iContainer.Resolve<IQueueConfiguration>();
            publisher = iContainer.Resolve<IExchangePublisher<Job>>();
            publisher.Declare(queueConfiguration.ResponseExchangeName);
        }

        public void ProcessTask(string workType)
        {
            try
            {
                Log.Information("NECL: AusPost ECL was started.");

                //Send RabbitMQ message to trigger Agency Bank BPM process
                var job = TaskHelper.GenerateJob("NECL", Subject.AusPostECL, Predicate.AusPostECL, new Parameter[] {
                    new Parameter() { name = "workType", value = workType }
                });
                Log.Information("NECL: Job object created successfully as per the schema.");

                publisher.PublishAsync(job, Guid.NewGuid().ToString());
                Log.Information("NECL: Message published successfully to RabbitMQ.");
            }
            catch(Exception ex)
            {
                Log.Error("NECL: There was unexpected error occured in AusPostECL.ProcessTask() with the stack trace information as {stackTrace}", ex.ToString());
                 throw new InvalidOperationException();
            }
        }
    }
}