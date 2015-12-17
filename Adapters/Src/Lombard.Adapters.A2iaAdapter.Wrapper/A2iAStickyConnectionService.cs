using System;
using System.Threading;
using System.Threading.Tasks;
using A2iACheckReaderLib;
using Lombard.Adapters.A2iaAdapter.Wrapper.Configuration;
using Lombard.Adapters.A2iaAdapter.Wrapper.Domain;

namespace Lombard.Adapters.A2iaAdapter.Wrapper
{
    public class A2iAStickyConnectionService : A2iACombinedTableService
    {
        private readonly int stickyChannelTimeout;
        private bool batchProcessing;
        private CancellationTokenSource cancellationTokenSource;

        public A2iAStickyConnectionService(API a2IaEngine, IA2iaConfiguration configuration)
            : base(a2IaEngine, configuration)
        {
            stickyChannelTimeout = configuration.StickyChannelTimeout;
        }

        public override void ProcessBatch(OcrBatch batch)
        {
            if (cancellationTokenSource != null) cancellationTokenSource.Cancel(false); //cancel the timeout
            cancellationTokenSource = new CancellationTokenSource();
            batchProcessing = true;
            base.ProcessBatch(batch);
        }

        protected override void PreProcessVouchers(A2iAOcrService a2IaOcrService)
        {
            base.PreProcessVouchers(a2IaOcrService);
            if (stickyChannelTimeout == 0) return;
            var timeSpan = (int)TimeSpan.FromMinutes(stickyChannelTimeout).TotalMilliseconds;
            StartStickyConnection(CloseChannel, a2IaOcrService, timeSpan, cancellationTokenSource.Token);
        }

        private void CloseChannel(A2iAOcrService service)
        {
            var timeSpan = (int)TimeSpan.FromMinutes(stickyChannelTimeout).TotalMilliseconds;
            if (batchProcessing) StartStickyConnection(CloseChannel, service, timeSpan, cancellationTokenSource.Token);
            else service.CloseOcrChannel();
        }

        protected override void OnBatchComplete(object sender, string batchId)
        {
            batchProcessing = false;
            base.OnBatchComplete(sender, batchId);
        }

        private async void StartStickyConnection(Action<A2iAOcrService> action, A2iAOcrService service, int timeout, CancellationToken token)
        {
            if (timeout == 0) return;
            try
            {
                await Task.Delay(timeout, token);
            }
            catch (TaskCanceledException)
            {
            }
            if (!token.IsCancellationRequested) action(service);
        }

        public override void Shutdown()
        {
            cancellationTokenSource.Cancel(false);
            Thread.Sleep(250);
            cancellationTokenSource.Dispose();
            base.Shutdown();
        }
    }
}
