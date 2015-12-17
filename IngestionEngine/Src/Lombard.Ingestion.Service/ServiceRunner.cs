using System;
using System.Timers;
using Autofac.Features.OwnedInstances;
using Lombard.Ingestion.Service.Configurations;
using Lombard.Ingestion.Service.Workers;
using Serilog;

namespace Lombard.Ingestion.Service
{
    public class ServiceRunner
    {
        private Timer eclIngestionTimer;
        private Timer batchAuditIngestionTimer;
        private Func<Owned<EclIngestionWorker>> eclWorkerFactory;
        private Func<Owned<BatchAuditIngestionWorker>> batchAuditWorkerFactory;
        private IIngestionServiceConfiguration configuration;

        public ServiceRunner(Func<Owned<EclIngestionWorker>> eclWorkerFactory, 
            Func<Owned<BatchAuditIngestionWorker>> batchAuditWorkerFactory, IIngestionServiceConfiguration configuration)
        {
            this.eclWorkerFactory = eclWorkerFactory;
            this.batchAuditWorkerFactory = batchAuditWorkerFactory;
            this.configuration = configuration;
        }

        public void Start()
        {
            RegisterEclIngestion();

            RegisterBatchAuditIngestion();

            Log.Information("Ingestion Service Started");
        }

        public void Stop()
        {
            eclIngestionTimer.Stop();
            batchAuditIngestionTimer.Stop();

            Log.Information("Ingestion Service Stopped");
        }

        private void RegisterEclIngestion()
        {
            if (configuration.EclIngestionPollingInSeconds > 0)
            {
                eclIngestionTimer = new Timer()
                {
                    Interval = configuration.EclIngestionPollingInSeconds * 1000,
                };
                eclIngestionTimer.Elapsed += (EclIngestionTimer_Triggered);
                eclIngestionTimer.Start();

                Log.Information("Ecl Ingestion Timer Started - Polling every {0} seconds", configuration.EclIngestionPollingInSeconds);
            }
            else
            {
                Log.Information("Ecl Ingestion is off due to polling less than 1 second");
            }
        }

        private void EclIngestionTimer_Triggered(object sender, ElapsedEventArgs e)
        {
            try
            {
                eclIngestionTimer.Stop();

                using (var factory = eclWorkerFactory())
                {
                    var eclIngestionWorker = factory.Value;

                    eclIngestionWorker.Process();
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex, "An unexpected error occurred during ecl ingestion worker process execution.");
            }
            finally
            {
                eclIngestionTimer.Start();
            }
        }

        private void RegisterBatchAuditIngestion()
        {
            if (configuration.BatchAuditIngestionPollingInSeconds > 0)
            {
                batchAuditIngestionTimer = new Timer()
                {
                    Interval = configuration.BatchAuditIngestionPollingInSeconds * 1000,
                };
                batchAuditIngestionTimer.Elapsed += (BatchAuditIngestionTimer_Triggered);
                batchAuditIngestionTimer.Start();

                Log.Information("Batch Audit Ingestion Timer Started - Polling every {0} seconds", configuration.BatchAuditIngestionPollingInSeconds);
            }
            else
            {
                Log.Information("Batch Audit Ingestion is off due to polling less than 1 second");
            }
        }

        private void BatchAuditIngestionTimer_Triggered(object sender, ElapsedEventArgs e)
        {
            try
            {
                batchAuditIngestionTimer.Stop();

                using (var factory = batchAuditWorkerFactory())
                {
                    var batchAuditIngestionWorker = factory.Value;

                    batchAuditIngestionWorker.Process();
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex, "An unexpected error occurred during batch audit ingestion worker process execution.");
            }
            finally
            {
                batchAuditIngestionTimer.Start();
            }
        }
    }
}
