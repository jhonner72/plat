using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading;
using A2iACheckReaderLib;
using Lombard.Adapters.A2iaAdapter.Wrapper.Configuration;
using Lombard.Adapters.A2iaAdapter.Wrapper.Domain;
using Serilog;

namespace Lombard.Adapters.A2iaAdapter.Wrapper
{
    public class A2iACombinedTableService : IOcrProcessingService
    {
        private readonly A2iAOcrService a2iAOcrService;
        private bool disposed = false;
        //private readonly IA2iaConfiguration configuration;

        public A2iACombinedTableService(API a2IaEngine, IA2iaConfiguration configuration)
        {
            a2iAOcrService = new A2iAOcrService(a2IaEngine, configuration);
        }

        public void Initialise()
        {
            a2iAOcrService.Initialise();
        }

        private readonly Mutex mutex = new Mutex();

        public virtual void ProcessBatch(OcrBatch batch)
        {
            try
            {
                mutex.WaitOne();
                PreProcessVouchers(a2iAOcrService);
                foreach (VoucherType voucher in Enum.GetValues(typeof(VoucherType)))
                {
                    ProcessVouchers(batch.Vouchers, voucher);
                }

                a2iAOcrService.CloseOcrChannel();
            }
            catch (Exception)
            {
                {
                }
                throw;
            }
            finally
            {
                mutex.ReleaseMutex();
                OnBatchComplete(this, batch.JobIdentifier);
            }
        }

        protected virtual void PreProcessVouchers(A2iAOcrService a2IaOcrService)
        {
            a2IaOcrService.OpenOcrChannel();
        }

        public virtual void Shutdown()
        {
            a2iAOcrService.Shutdown();
        }

        protected virtual void OnBatchComplete(object sender, string batchId)
        {
            var batchComplete = BatchComplete;
            if (batchComplete != null) batchComplete(sender, batchId);
        }

        public event BatchCompleteDelegate BatchComplete;

        private readonly BlockingCollection<OcrVoucher> voucherQueue = new BlockingCollection<OcrVoucher>(10000);

        private void ProcessVouchers(IEnumerable<OcrVoucher> vouchers, VoucherType voucherType)
        {
            var voucherTable = new Dictionary<VoucherType, string>
            {
                {VoucherType.Credit, "Credit"},
                {VoucherType.Debit, "Debit"}
            };

            var voucherSubset = vouchers.Where(v => v.VoucherType == voucherType);
            var enumerable = voucherSubset as IList<OcrVoucher> ?? voucherSubset.ToList();
            if (!enumerable.Any()) return;
            a2iAOcrService.SelectDocumentTable(voucherTable[voucherType]);
            Debug.WriteLine("Process {0} vouchers of type {1}", enumerable.Count, voucherType);
            var ocrVouchers = enumerable.ToList();
            foreach (var voucher in ocrVouchers)
            {
                a2iAOcrService.ProcessVoucher(voucher);
                Log.Debug("Adding voucher id {0} with request id {1} to queue", voucher.Id, voucher.RequestId);
                voucherQueue.Add(voucher);
            }

            while (!voucherQueue.IsCompleted)
            {
                OcrVoucher voucher = null;

                try
                {
                    voucher = voucherQueue.Take();
                }
                catch (InvalidOperationException) { }
                if (voucher == null) continue;
                Log.Debug("Retrieving voucher id {0} with request id {1} to queue", voucher.Id, voucher.RequestId);
                a2iAOcrService.GetIcrChannelResult(voucher);
            }
        }

        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        protected virtual void Dispose(bool disposing)
        {
            if (disposed) return;
            if (disposing)
            {
                // Free managed objects here
                a2iAOcrService.Dispose();
            }
            // free unmanaged objects here

            disposed = true;
        }
    }
}
