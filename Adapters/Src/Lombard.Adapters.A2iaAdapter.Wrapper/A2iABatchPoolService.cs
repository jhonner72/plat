using A2iACheckReaderLib;
using Polly;
using Serilog;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using System.Diagnostics;
using System.Threading;
using System.Collections.Concurrent;
using Lombard.Adapters.A2iaAdapter.Wrapper.Configuration;
using Lombard.Adapters.A2iaAdapter.Wrapper.Domain;

namespace Lombard.Adapters.A2iaAdapter.Wrapper
{
    public class A2iABatchPoolService : A2iAOcrService
    {
        private readonly ConcurrentDictionary<string, int> batchRegistration;
        public ConcurrentQueue<OcrVoucher> VoucherQueue;
        public ConcurrentBag<OcrVoucher> VoucherProcessingBag;
        private readonly object processingLock = new object();

        public bool Processing { get { return batchRegistration.Count > 0; } }
        public new event BatchCompleteDelegate BatchComplete;

        public A2iABatchPoolService(API a2IaEngine, IA2iaConfiguration configuration) :
            base(a2IaEngine, configuration)
        {
            const int capacity = 100;
            batchRegistration = new ConcurrentDictionary<string, int>(GetMaxThreadCount(3), capacity);
            VoucherQueue = new ConcurrentQueue<OcrVoucher>();
            VoucherProcessingBag = new ConcurrentBag<OcrVoucher>();
        }

        public Task RegisterBatchTask(Domain.OcrBatch batch)
        {
            return Task.Run(() => RegisterBatch(batch));
        }

        public void RegisterBatch(Domain.OcrBatch batch)
        {
            batchRegistration[batch.JobIdentifier] = batch.Vouchers.Count;
            foreach (OcrVoucher voucher in batch.Vouchers)
            {
                voucher.BatchId = batch.JobIdentifier;
                VoucherQueue.Enqueue(voucher);
            }
        }

        public async void ProcessBatch()
        {
            OpenOcrChannel(null, 2);
            // ReSharper disable once CollectionNeverQueried.Local
            var vouchers = new List<OcrVoucher>();
            while (VoucherQueue.Count > 0)
            {
                OcrVoucher voucher;
                if (VoucherQueue.TryDequeue(out voucher))
                {
                    vouchers.Add(voucher);
                    ProcessVoucher(voucher);
                }
            }
            //foreach (var voucher in vouchers)
            //{
                await GetResultAsyncAlt(DocumentId, TableId, ChannelId);
            //}
            if (batchRegistration.Count <= 0) CloseOcrChannel();
        }

        public Task ProcessVoucherBatchAsync()
        {
            return Task.Run(() => ProcessVoucherBatch());
        }

        public void ProcessVoucherBatch()
        {
            Debug.WriteLine("Before Open channel open {0}, channel Id {1}", ChannelOpen, ChannelId);
            OpenOcrChannel(null, 2);
            Debug.WriteLine("After Open channel open {0}, channel Id {1}", ChannelOpen, ChannelId);
            while (VoucherQueue.Count > 0)
            {
                OcrVoucher voucher;
                if (VoucherQueue.TryDequeue(out voucher))
                {
                    VoucherProcessingBag.Add(voucher);
                    ProcessVoucher(voucher);
                }
            }
        }

        private int waitResultCount;
        public async Task WaitResults(bool withClose = true)
        {
            waitResultCount++;
            Debug.WriteLine("WaitResultCount {0}", waitResultCount);
            while (VoucherProcessingBag.Count > 0)
            {
                await GetResultAsyncAlt(DocumentId, TableId, ChannelId);
            }
            Debug.WriteLine("Voucher Processing Bag Empty");
            if (waitResultCount == 1 && withClose)
                CloseOcrChannel();
            waitResultCount--;
        }

        //public void ReleaseResources()
        //{
        //    Debug.WriteLine("Before Close channel open {0}, channel Id {1}", ChannelOpen, channelId);
        //    ReleaseResources();
        //    Debug.WriteLine("After Close channel open {0}, channel Id {1}", ChannelOpen, channelId);
        //}

        //public Task GetResultAsync(int requestId, int documentId, int tableId, int channelId, Domain.OcrVoucher voucher)
        //{
        //    return Task.Run(() =>
        //    {
        //        try
        //        {
        //            int resultId;
        //            lock (processingLock)
        //            {
        //                resultId = GetIcrChannelResult(channelId, requestId, 3000);
        //                Debug.WriteLine(string.Format("GetResultAsync: resultId {0}, requestId {1}, channelId {2}", resultId, requestId, channelId));
        //            }
        //            if (resultId == requestId) GetResult(resultId, voucher);
        //            Debug.WriteLine("Result requestId {0}, resultId {4}, documentId {1}, tableId {2}, channelId{3}", requestId, documentId, tableId, channelId, resultId);
        //            //ReleaseResources(requestId, documentId, tableId, channelId);
        //            Debug.WriteLine("Close request {0}", requestId);
        //            a2IaEngine.ScrCloseRequest(requestId);
        //            var key = GetBatchId(voucher);
        //            batchRegistration[key]--;
        //            if (batchRegistration[key] == 0)
        //            {
        //                int value;
        //                batchRegistration.TryRemove(key, out value);
        //                OnBatchComplete(this, key);
        //            }

        //        }
        //        catch (Exception ex)
        //        {
        //            Log.Error(ex, "An error has ocurred while processing the image for voucher {@voucherId}", voucher.Id);
        //            Debug.WriteLine("Exception for request {0}", voucher.RequestId);
        //            if (voucher.RequestId > 0)
        //            {
        //                a2IaEngine.ScrCloseRequest(voucher.RequestId);
        //            }
        //            ReleaseResources();
        //            throw;
        //        }
        //    });
        //}
        public int Count { get; set; }
        public Task GetResultAsyncAlt(int documentId, int tableId, int channelId)
        {
            return Task.Run(() =>
            {
                var resultId = 0;
                try
                {
                    OcrVoucher voucher;
                    lock (processingLock)
                    {
                        if (VoucherProcessingBag.TryTake(out voucher))
                        {
                            //TODO: need to rethink how to process the voucher
                            // as the channel does not process them in order
                            GetIcrChannelResult(voucher);
                            resultId = voucher.ResultId;
                        }
                        else Debug.WriteLine("Voucher not found for resultId {0}", resultId);
                    }

                    Debug.WriteLine("Result resultId {0}, documentId {1}, tableId {2}, channelId{3}", resultId, documentId, tableId, channelId);
                    string key = GetBatchId(voucher);
                    batchRegistration[key]--;
                    if (batchRegistration[key] == 0)
                    {
                        int value;
                        batchRegistration.TryRemove(key, out value);
                        OnBatchComplete(this, key);
                    }

                }
                catch (Exception ex)
                {
                    Log.Error(ex, "An error has ocurred while processing the image for voucher");
                    Debug.WriteLine("GetResultAsyncAlt has thrown an exception for request {0}", resultId);
                    //Debug.WriteLine("Exception for request {0}", resultId);
                    //if (resultId > 0)
                    //{
                    //    a2IaEngine.ScrCloseRequest(resultId);
                    //}
                    //ReleaseResources();
                    Policy.Handle<Exception>()
                        .CircuitBreaker(3, TimeSpan.FromMilliseconds(200))
                        .Execute(() => ExceptionClose());
                    throw;
                }
            });
        }

        private void ExceptionClose()
        {
            Debug.WriteLine("This is as a result of an exception");
            CloseOcrChannel();
        }

        protected void OnBatchComplete(object sender, string batchId)
        {
            BatchCompleteDelegate batchComplete = BatchComplete;
            if (batchComplete != null) batchComplete(sender, batchId);
        }

        private string GetBatchId(OcrVoucher voucher)
        {
            return voucher.BatchId;
        }

        private AutoResetEvent batchFinishedEvent;

        private struct ProcessData
        {
            public int ChannelId { get; set; }
            public Domain.OcrVoucher Voucher { get; set; }
        }
    }
}
