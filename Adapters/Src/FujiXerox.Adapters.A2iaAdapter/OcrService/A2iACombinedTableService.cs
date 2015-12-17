using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using FujiXerox.Adapters.A2iaAdapter.Configuration;
using FujiXerox.Adapters.A2iaAdapter.Model;
using Serilog;

namespace FujiXerox.Adapters.A2iaAdapter.OcrService
{
    public class A2iACombinedTableService : A2iAOcrService
    {
        public A2iACombinedTableService(IA2iaConfiguration configuration)
            : base(configuration)
        {            
        }

        private readonly Mutex _mutex = new Mutex();

        public override bool ProcessBatch(OcrBatch batch)
        {
            bool result;
            try
            {
                _mutex.WaitOne();
                PreProcessVouchers(this);
                foreach (VoucherType voucher in Enum.GetValues(typeof(VoucherType)))
                {
                    ProcessVouchers(batch.Vouchers, voucher);
                }

                CloseOcrChannel();
                result = true;
            }
            catch (Exception ex)
            {
                Log.Error(ex,"An exception has occurred processing batch {0}", batch.JobIdentifier);
                result = false;
                //throw;
            }
            finally
            {
                _mutex.ReleaseMutex();
                OnBatchComplete(this, batch);
            }
            return result;
        }

        protected virtual void PreProcessVouchers(A2iAOcrService a2IaOcrService)
        {
            a2IaOcrService.OpenOcrChannel();
        }

        private readonly ConcurrentQueue<OcrVoucher> _voucherQueue = new ConcurrentQueue<OcrVoucher>();
        //private readonly BlockingCollection<OcrVoucher> voucherQueue = new BlockingCollection<OcrVoucher>(10000);

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
            SelectDocumentTable(voucherTable[voucherType]);
            Log.Debug("Process {0} vouchers of type {1}", enumerable.Count, voucherType);
            var ocrVouchers = enumerable.ToList();
            foreach (var voucher in ocrVouchers)
            {
                ProcessVoucher(voucher);
                Log.Debug("Adding voucher id {0} with request id {1} to queue", voucher.Id, voucher.RequestId);
                _voucherQueue.Enqueue(voucher);
                //voucherQueue.Add(voucher);
            }

            while (!_voucherQueue.IsEmpty)
                //while (!voucherQueue.IsCompleted)
            {
                OcrVoucher voucher = null;

                try
                {
                    if (!_voucherQueue.TryDequeue(out voucher)) continue;
                    //voucher = voucherQueue.Take();
                }
                catch (InvalidOperationException)
                {
                }
                if (voucher == null) continue;
                Log.Debug("Retrieving voucher id {0} with request id {1} to queue", voucher.Id, voucher.RequestId);
                GetIcrChannelResult(voucher);
            }
        }
    }
}
