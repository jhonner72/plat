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
    public interface IImageExchange
    {
        [DisplayName("Image Exchange")]
        void ProcessTask();
    }

    public class ImageExchangeService : IImageExchange
    {
        private IExchangePublisher<Job> publisher;
        private IComponentContext iContainer = null;
        private IQueueConfiguration queueConfiguration;

        public ImageExchangeService(IComponentContext container)
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
                Log.Information("ImageExchange: recurring task was started.");

                var scope = iContainer.Resolve<ILifetimeScope>();
                using (var tmpScope = scope.BeginLifetimeScope())
                {
                    // Update recurring interval
                    var schedulerHelper = tmpScope.Resolve<ISchedulerHelper>();
                    schedulerHelper.ScheduleIEProcess(schedulerHelper.GetSchedulerReference());

                    var job = TaskHelper.GenerateJob("NIEO", Subject.ImageExchange, Predicate.ImageExchange);
                    Log.Information("ImageExchange: Job object created successfully as per the schema.");

                    publisher.PublishAsync(job, Guid.NewGuid().ToString());
                    Log.Information("ImageExchange: Message published successfully to RabbitMQ.");
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex, "ImageExchange: There was unexpected error occured in ImageExchange.ProcessTask()");
                throw ex;
            }
        }
    }
}