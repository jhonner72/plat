using System;
using Lombard.Adapters.DipsAdapter.Configuration;
using Lombard.Adapters.DipsAdapter.Jobs;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Common.Configuration;
using Lombard.Common.Queues;
using Quartz;
using Quartz.Spi;
using Serilog;

namespace Lombard.Adapters.DipsAdapter
{
    public class ServiceRunner
    {
        private readonly IAdapterConfiguration adapterConfig;
        private readonly IQuartzConfiguration quartzConfiguration;
        private readonly IScheduler scheduler;
        private readonly IJobFactory jobFactory;
        private readonly IQueueConsumer<ValidateBatchCodelineRequest> validateCodelineRequestQueue;
        private readonly IExchangePublisher<ValidateBatchCodelineResponse> validateCodelineExchange;
        private readonly IQueueConsumer<CorrectBatchCodelineRequest> correctCodelineRequestQueue;
        private readonly IExchangePublisher<CorrectBatchCodelineResponse> correctCodelineExchange;
        private readonly IQueueConsumer<ValidateBatchTransactionRequest> validateTransactionRequestQueue;
        private readonly IExchangePublisher<ValidateBatchTransactionResponse> validateTransactionExchange;
        private readonly IQueueConsumer<CorrectBatchTransactionRequest> correctTransactionRequestQueue;
        private readonly IExchangePublisher<CorrectBatchTransactionResponse> correctTransactionExchange;
        private readonly IQueueConsumer<CheckThirdPartyBatchRequest> checkThirdPartyRequestQueue;
        private readonly IExchangePublisher<CheckThirdPartyBatchResponse> checkThirdPartyExchange;
        private readonly IQueueConsumer<GenerateCorrespondingVoucherRequest> generateCorrespondingVoucherRequestQueue;
        private readonly IExchangePublisher<GenerateCorrespondingVoucherResponse> generateCorrespondingVoucherExchange;
        private readonly IExchangePublisher<GetVouchersInformationRequest> getVoucherInformationRequestExchange;
        private readonly IQueueConsumer<GetVouchersInformationResponse> getVoucherInformationResponseQueue;
        private readonly IQueueConsumer<GenerateBatchBulkCreditRequest> generateBulkCreditRequestQueue;
        private readonly IExchangePublisher<GenerateBatchBulkCreditResponse> generateBulkCreditResponseExchange;

        public ServiceRunner(
            IAdapterConfiguration adapterConfig,
            IQueueConsumer<ValidateBatchCodelineRequest> validateCodelineRequestQueue,
            IExchangePublisher<ValidateBatchCodelineResponse> validateCodelineExchange,
            IQueueConsumer<CorrectBatchCodelineRequest> correctCodelineRequestQueue,
            IExchangePublisher<CorrectBatchCodelineResponse> correctCodelineExchange,
            IQueueConsumer<ValidateBatchTransactionRequest> validateTransactionRequestQueue,
            IExchangePublisher<ValidateBatchTransactionResponse> validateTransactionExchange,
            IQueueConsumer<CorrectBatchTransactionRequest> correctTransactionRequestQueue,
            IExchangePublisher<CorrectBatchTransactionResponse> correctTransactionExchange,
            IQueueConsumer<CheckThirdPartyBatchRequest> checkThirdPartyRequestQueue,
            IExchangePublisher<CheckThirdPartyBatchResponse> checkThirdPartyExchange,
            IQueueConsumer<GenerateCorrespondingVoucherRequest> generateCorrespondingVoucherRequestQueue,
            IExchangePublisher<GenerateCorrespondingVoucherResponse> generateCorrespondingVoucherExchange,
            IExchangePublisher<GetVouchersInformationRequest> getVoucherInformationRequestExchange,
            IQueueConsumer<GetVouchersInformationResponse> getVoucherInformationResponseQueue,
            IQueueConsumer<GenerateBatchBulkCreditRequest> generateBulkCreditRequestQueue,
            IExchangePublisher<GenerateBatchBulkCreditResponse> generateBulkCreditResponseExchange,
            IJobFactory jobFactory,
            IScheduler scheduler,
            IQuartzConfiguration quartzConfiguration)
        {
            this.adapterConfig = adapterConfig;
            this.validateCodelineRequestQueue = validateCodelineRequestQueue;
            this.validateCodelineExchange = validateCodelineExchange;
            this.correctCodelineRequestQueue = correctCodelineRequestQueue;
            this.correctCodelineExchange = correctCodelineExchange;
            this.validateTransactionRequestQueue = validateTransactionRequestQueue;
            this.validateTransactionExchange = validateTransactionExchange;
            this.correctTransactionRequestQueue = correctTransactionRequestQueue;
            this.correctTransactionExchange = correctTransactionExchange;
            this.checkThirdPartyRequestQueue = checkThirdPartyRequestQueue;
            this.checkThirdPartyExchange = checkThirdPartyExchange;
            this.generateCorrespondingVoucherRequestQueue = generateCorrespondingVoucherRequestQueue;
            this.generateCorrespondingVoucherExchange = generateCorrespondingVoucherExchange;
            this.getVoucherInformationRequestExchange = getVoucherInformationRequestExchange;
            this.getVoucherInformationResponseQueue = getVoucherInformationResponseQueue;
            this.generateBulkCreditRequestQueue = generateBulkCreditRequestQueue;
            this.generateBulkCreditResponseExchange = generateBulkCreditResponseExchange;
            this.jobFactory = jobFactory;
            this.scheduler = scheduler;
            this.quartzConfiguration = quartzConfiguration;
        }

        public void Start()
        {
            if (adapterConfig.HandleValidateCodelineRequest)
            {
                Log.Information("This service will handle validate codeline requests");
                validateCodelineRequestQueue.Subscribe(adapterConfig.ValidateCodelineQueueName);
            }

            if (adapterConfig.HandleValidateCodelineResponse)
            {
                Log.Information("This service will handle validate codeline responses");
                validateCodelineExchange.Declare(adapterConfig.ValidateCodelineExchangeName);
                StartValidateCodelineResponsePollingJob();
            }

            if (adapterConfig.HandleCorrectCodelineRequest)
            {
                Log.Information("This service will handle correct codeline requests");
                correctCodelineRequestQueue.Subscribe(adapterConfig.CorrectCodelineQueueName);
            }

            if (adapterConfig.HandleCorrectCodelineResponse)
            {
                Log.Information("This service will handle correct codeline responses");
                correctCodelineExchange.Declare(adapterConfig.CorrectCodelineExchangeName);
                StartCorrectCodelineResponsePollingJob();
            }

            if (adapterConfig.HandleValidateTransactionRequest)
            {
                Log.Information("This service will handle validate transaction requests");
                validateTransactionRequestQueue.Subscribe(adapterConfig.ValidateTransactionQueueName);
            }

            if (adapterConfig.HandleValidateTransactionResponse)
            {
                Log.Information("This service will handle validate transaction responses");
                validateTransactionExchange.Declare(adapterConfig.ValidateTransactionExchangeName);
                StartValidateTransactionResponsePollingJob();
            }

            if (adapterConfig.HandleCorrectTransactionRequest)
            {
                Log.Information("This service will handle correct transaction requests");
                correctTransactionRequestQueue.Subscribe(adapterConfig.CorrectTransactionQueueName);
            }

            if (adapterConfig.HandleCorrectTransactionResponse)
            {
                Log.Information("This service will handle correct transaction responses");
                correctTransactionExchange.Declare(adapterConfig.CorrectTransactionExchangeName);
                StartCorrectTransactionResponsePollingJob();
            }

            if (adapterConfig.HandleCheckThirdPartyRequest)
            {
                Log.Information("This service will handle check third party requests");
                checkThirdPartyRequestQueue.Subscribe(adapterConfig.CheckThirdPartyQueueName);
            }

            if (adapterConfig.HandleCheckThirdPartyResponse)
            {
                Log.Information("This service will handle check third party responses");
                checkThirdPartyExchange.Declare(adapterConfig.CheckThirdPartyExchangeName);
                StartCheckThirdPartyResponsePollingJob();
            }

            if (adapterConfig.HandleGenerateCorrespondingVoucherRequest)
            {
                Log.Information("This service will handle generate corresponding voucher requests");
                generateCorrespondingVoucherRequestQueue.Subscribe(adapterConfig.GenerateCorrespondingVoucherQueueName);
            }

            if (adapterConfig.HandleGenerateCorrespondingVoucherResponse)
            {
                Log.Information("This service will handle generate corresponding voucher responses");
                generateCorrespondingVoucherExchange.Declare(adapterConfig.GenerateCorrespondingVoucherExchangeName);
                StartGenerateCorrespondingVoucherResponsePollingJob();
            }

            if (adapterConfig.HandleGetVoucherInformationRequest)
            {
                Log.Information("This service will handle requests for Voucher Information");
                getVoucherInformationRequestExchange.Declare(adapterConfig.GetPoolVouchersExchangeName);
                StartGetVouchersInformationRequestPollingJob();
            }

            if (adapterConfig.HandleGetVoucherInformationResponse)
            {
                Log.Information("This service will handle response for Voucher Information");
                getVoucherInformationResponseQueue.Subscribe(adapterConfig.GetPoolVouchersQueueName);
            }

            if (adapterConfig.HandleGenerateBulkCreditRequest)
            {
                Log.Information("This service will handle generate bulk credit requests");
                generateBulkCreditRequestQueue.Subscribe(adapterConfig.GenerateBulkCreditQueueName);
            }

            if (adapterConfig.HandleGenerateBulkCreditResponse)
            {
                Log.Information("This service will handle generate bulk credit responses");
                generateBulkCreditResponseExchange.Declare(adapterConfig.GenerateBulkCreditExchangeName);
                StartGenerateBulkCreditResponsePollingJob();
            }

            Log.Information("Dips Adapter Service Started");
        }


        public void Stop()
        {
            validateCodelineRequestQueue.Dispose();
            correctCodelineRequestQueue.Dispose();
            validateTransactionRequestQueue.Dispose();
            correctTransactionRequestQueue.Dispose();
            checkThirdPartyRequestQueue.Dispose();
            getVoucherInformationResponseQueue.Dispose();
            generateBulkCreditRequestQueue.Dispose();
            scheduler.Shutdown();

            Log.Information("Dips Adapter Service Stopped");
        }

        private void StartValidateCodelineResponsePollingJob()
        {
            var trigger = TriggerBuilder.Create()
                .WithDescription("SimplePoll")
                .WithSimpleSchedule(x =>
                {
                    x.WithInterval(TimeSpan.FromSeconds(quartzConfiguration.PollingIntervalSecs));
                    x.RepeatForever();
                })
                .Build();

            scheduler.JobFactory = jobFactory;

            var job = JobBuilder
                .Create<ValidateCodelineResponsePollingJob>()
                .WithIdentity("ValidateCodelineResponsePollingJob")
                .Build();

            scheduler.ScheduleJob(job, trigger);
            scheduler.Start();
        }

        private void StartCorrectCodelineResponsePollingJob()
        {
            var trigger = TriggerBuilder.Create()
                .WithDescription("SimplePoll")
                .WithSimpleSchedule(x =>
                {
                    x.WithInterval(TimeSpan.FromSeconds(quartzConfiguration.PollingIntervalSecs));
                    x.RepeatForever();
                })
                .Build();

            scheduler.JobFactory = jobFactory;

            var job = JobBuilder
                .Create<CorrectCodelineResponsePollingJob>()
                .WithIdentity("CorrectCodelineResponsePollingJob")
                .Build();

            scheduler.ScheduleJob(job, trigger);
            scheduler.Start();
        }

        private void StartValidateTransactionResponsePollingJob()
        {
            var trigger = TriggerBuilder.Create()
                .WithDescription("SimplePoll")
                .WithSimpleSchedule(x =>
                {
                    x.WithInterval(TimeSpan.FromSeconds(quartzConfiguration.PollingIntervalSecs));
                    x.RepeatForever();
                })
                .Build();

            scheduler.JobFactory = jobFactory;

            var job = JobBuilder
                .Create<ValidateTransactionResponsePollingJob>()
                .WithIdentity("ValidateTransactionResponsePollingJob")
                .Build();

            scheduler.ScheduleJob(job, trigger);
            scheduler.Start();
        }

        private void StartCorrectTransactionResponsePollingJob()
        {
            var trigger = TriggerBuilder.Create()
                .WithDescription("SimplePoll")
                .WithSimpleSchedule(x =>
                {
                    x.WithInterval(TimeSpan.FromSeconds(quartzConfiguration.PollingIntervalSecs));
                    x.RepeatForever();
                })
                .Build();

            scheduler.JobFactory = jobFactory;

            var job = JobBuilder
                .Create<CorrectTransactionResponsePollingJob>()
                .WithIdentity("CorrectTransactionResponsePollingJob")
                .Build();

            scheduler.ScheduleJob(job, trigger);
            scheduler.Start();
        }

        private void StartCheckThirdPartyResponsePollingJob()
        {
            var trigger = TriggerBuilder.Create()
                .WithDescription("SimplePoll")
                .WithSimpleSchedule(x =>
                {
                    x.WithInterval(TimeSpan.FromSeconds(quartzConfiguration.PollingIntervalSecs));
                    x.RepeatForever();
                })
                .Build();

            scheduler.JobFactory = jobFactory;

            var job = JobBuilder
                .Create<CheckThirdPartyResponsePollingJob>()
                .WithIdentity("CheckThirdPartyResponsePollingJob")
                .Build();

            scheduler.ScheduleJob(job, trigger);
            scheduler.Start();
        }

        private void StartGenerateCorrespondingVoucherResponsePollingJob()
        {
            var trigger = TriggerBuilder.Create()
                .WithDescription("SimplePoll")
                .WithSimpleSchedule(x =>
                {
                    x.WithInterval(TimeSpan.FromSeconds(quartzConfiguration.PollingIntervalSecs));
                    x.RepeatForever();
                })
                .Build();

            scheduler.JobFactory = jobFactory;

            var job = JobBuilder
                .Create<GenerateCorrespondingVoucherResponsePollingJob>()
                .WithIdentity("GenerateCorrespondingVoucherResponsePollingJob")
                .Build();

            scheduler.ScheduleJob(job, trigger);
            scheduler.Start();
        }

        private void StartGetVouchersInformationRequestPollingJob()
        {
            var trigger = TriggerBuilder.Create()
                .WithDescription("SimplePoll")
                .WithSimpleSchedule(x =>
                {
                    x.WithInterval(TimeSpan.FromSeconds(quartzConfiguration.PollingIntervalSecs));
                    x.RepeatForever();
                })
                .Build();

            scheduler.JobFactory = jobFactory;

            var job = JobBuilder
                .Create<GetVouchersInformationRequestPollingJob>()
                .WithIdentity("GetVouchersInformationRequestPollingJob")
                .Build();

            scheduler.ScheduleJob(job, trigger);
            scheduler.Start();
        }


        private void StartGenerateBulkCreditResponsePollingJob()
        {
            var trigger = TriggerBuilder.Create()
                .WithDescription("SimplePoll")
                .WithSimpleSchedule(x =>
                {
                    x.WithInterval(TimeSpan.FromSeconds(quartzConfiguration.PollingIntervalSecs));
                    x.RepeatForever();
                })
                .Build();

            scheduler.JobFactory = jobFactory;

            var job = JobBuilder
                .Create<GenerateBulkCreditResponsePollingJob>()
                .WithIdentity("GenerateBulkCreditResponsePollingJob")
                .Build();

            scheduler.ScheduleJob(job, trigger);
            scheduler.Start();
        }
    }
}
