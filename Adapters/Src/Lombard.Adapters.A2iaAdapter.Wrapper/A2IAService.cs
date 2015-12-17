using A2iACheckReaderLib;
using Polly;
using Serilog;
using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Threading.Tasks;
using Lombard.Adapters.A2iaAdapter.Wrapper.Enums;
using System.Diagnostics;
using System.Linq;
using System.Threading;
using Lombard.Adapters.A2iaAdapter.Wrapper.Configuration;
using Lombard.Adapters.A2iaAdapter.Wrapper.Domain;

namespace Lombard.Adapters.A2iaAdapter.Wrapper
{
    public class A2iAOcrProcessingService : IOcrProcessingService
    {
        private readonly API a2IaEngine;
        private readonly string parameterDirectory;
        private readonly object processingLock = new object();
        private readonly LoadMethod loadMethod;
        private readonly string fileType;
        private int maxProcessorCount;

        private IA2iaConfiguration configuration;

        public A2iAOcrProcessingService(API a2IaEngine, IA2iaConfiguration configuration)
        {
            this.a2IaEngine = a2IaEngine;
            parameterDirectory = configuration.ParameterPath;
            loadMethod = configuration.LoadMethod;
            fileType = configuration.FileType;
            maxProcessorCount = configuration.MaxProcessorCount;

            this.configuration = configuration;
        }

        protected int GetMaxThreadCount()
        {
            var processorCount = Environment.ProcessorCount - 1;
            return Math.Min(processorCount, maxProcessorCount);
        }

        private int OpenChannel(int timeout = 10000)
        {
            int channelId = 0;

            try
            {
                var channelParmId = (int)a2IaEngine.ScrCreateChannelParam();
                var machineName = Environment.MachineName;

                for (var i = 0; i < GetMaxThreadCount(); i++)
                {
                    // Set CPU properties
                    a2IaEngine.SetProperty(channelParmId, string.Format("CPU[{0}].cpuServer", i + 1), machineName);
                    // Set parms properties, find on the main page of the configuration tools
                    a2IaEngine.SetProperty(channelParmId, string.Format("CPU[{0}].paramDir", i + 1), parameterDirectory);
                }

                // ReSharper disable once RedundantAssignment
                var errorId = 0;
                var sleepPeriods = new List<TimeSpan> 
                { 
                    TimeSpan.FromMilliseconds(200), 
                    TimeSpan.FromMilliseconds(1000),
                    TimeSpan.FromMilliseconds(7000),
                    TimeSpan.FromMilliseconds(12000)
                };
                Policy.Handle<Exception>()
                    .WaitAndRetry(sleepPeriods)
                    .Execute(() => errorId = (int)a2IaEngine.ScrOpenChannelExt2(ref channelId, channelParmId, timeout));
                if (errorId != 0) throw new Exception(a2IaEngine.ScrGetErrorMessage(errorId));
                Debug.WriteLine("Channel Id {0}", channelId);
                return channelId;
            }
            catch (Exception ex)
            {
                var error = a2IaEngine.ScrGetLastError();
                var status = a2IaEngine.ScrGetChannelStatus(channelId);// CheckChannelStatus()
                Log.Error(ex, "Open Channel Error {0}, Status {1}", error, status);
                a2IaEngine.ScrCloseChannel(channelId);
                throw new Exception(error);
            }
        }

        private int LoadDocumentTables(string filename)
        {
            return (int)a2IaEngine.ScrOpenDocumentTable(filename);
        }

        private int GetDefaultDocument(int tableId)
        {
            return (int)a2IaEngine.ScrGetDefaultDocument(tableId);
        }

        private void PrepareRequestForCheckReader(int documentId)
        {
            const string fieldName = Constants.EngineFields.Amount;
            const string value = Constants.Enabled;
            // ReSharper disable once UseIndexedProperty
            a2IaEngine.set_ObjectProperty(documentId, fieldName, value);
        }

        private int OpenIcrChannel(int channelId, int documentId)
        {
            return (int)a2IaEngine.ScrOpenRequest(channelId, documentId);
        }

        private int GetIcrChannelResult(int channelId, int requestId, int timeout)
        {
            // ReSharper disable once RedundantAssignment
            var result = 0;
            try
            {
                result = (int)a2IaEngine.ScrGetResult(channelId, 0, timeout);
            }
            catch (Exception)
            {
                a2IaEngine.ScrCloseRequest(requestId);
                throw;
            }
            return result;
        }
        
        private void ReleaseChannelResources(int requestId, int channelId)
        {
            if (requestId != 0) a2IaEngine.ScrCloseRequest(requestId);
            if (channelId != 0) a2IaEngine.ScrCloseChannel(channelId);
        }

        private void ReleaseDocumentResources(int documentId, int tableId)
        {
            if (documentId != 0) a2IaEngine.ScrCloseDocument(documentId);
            if (tableId != 0) a2IaEngine.ScrCloseDocumentTable(tableId);
        }

        public void Initialise()
        {
            a2IaEngine.ScrInit(parameterDirectory);
        }

        private string SetVoucherConfiguration(OcrVoucher voucher)
        {
            string documentTableFilename;

            switch (voucher.VoucherType)
            {
                case VoucherType.Credit:
                    documentTableFilename = configuration.CreditTablePath;
                    break;
                default:
                    documentTableFilename = configuration.DebitTablePath;
                    break;
            }

            return documentTableFilename;
        }

        [SuppressMessage("ReSharper", "RedundantAssignment")]
        public async Task ProcessVoucherAsync(IList<OcrVoucher> vouchers)
        {
            var channelId = 0;

            try
            {
                channelId = OpenChannel();

                foreach (var voucher in vouchers)
                {
                    var tableId = 0;
                    var documentId = 0;

                    tableId = LoadDocumentTables(SetVoucherConfiguration(voucher));
                    documentId = GetDefaultDocument(tableId);
                    PrepareRequestForCheckReader(documentId);

                    try
                    {
                        if (loadMethod == LoadMethod.File) a2IaEngine.ScrDefineImage(documentId, fileType, "FILE", voucher.ImagePath);
                        if (loadMethod == LoadMethod.Mem)
                        {
                            using (var imageFile = System.IO.File.OpenRead(voucher.ImagePath))
                            {
                                var closureVoucher = voucher;

                                //closureVoucher.ImageFormat = Functions.GetImageFormat(fileSystem, closureVoucher.ImagePath);
                                closureVoucher.ImageBuffer = new byte[imageFile.Length];

                                try
                                {
                                    //read the image file into memory
                                    await imageFile
                                        .ReadAsync(closureVoucher.ImageBuffer, 0, (int)imageFile.Length)
                                        .ContinueWith(t =>
                                        {
                                            //initialize processing in A2IA
                                            if (t.IsCompleted && !t.IsFaulted)
                                            {
                                                //closureVoucher.ImageBuffer = LoadFromMemory(documentId, closureVoucher.ImageBuffer);
                                                a2IaEngine.ScrDefineImage(documentId, fileType, "MEM", closureVoucher.ImageBuffer);
                                                closureVoucher.RequestId = (int)a2IaEngine.ScrOpenRequest(channelId, documentId);
                                            }
                                            else
                                            {
                                                throw t.Exception ?? new Exception("Could not process the image.");
                                            }
                                        });
                                }
                                catch (Exception ex)
                                {
                                    Log.Error(ex, "An error has ocurred while processing the image for voucher {@voucherId}", closureVoucher.Id);
                                    if (closureVoucher.RequestId > 0)
                                    {
                                        a2IaEngine.ScrCloseRequest(closureVoucher.RequestId);
                                    }
                                }
                            }
                        }
                        voucher.RequestId = OpenIcrChannel(channelId, documentId);

                        //Return result and release document resources
                        await GetResultAsync(voucher.RequestId, documentId, tableId, channelId, voucher);
                        ReleaseDocumentResources(documentId, tableId);
                    }
                    catch (Exception ex)
                    {
                        Log.Error(ex, "An error has ocurred while processing the image for voucher {@voucherId}", voucher.Id);
                        if (voucher.RequestId > 0)
                        {
                            var requestId = voucher.RequestId;
                            Policy.Handle<Exception>()
                                .CircuitBreaker(3, TimeSpan.FromMilliseconds(200))
                                .Execute(() => a2IaEngine.ScrCloseRequest(requestId));
                        }
                        throw;
                    }
                }

                //Release document resources
                ReleaseChannelResources(0, channelId);

            }
            catch (Exception ex)
            {
                var error = a2IaEngine.ScrGetLastError();
                Shutdown();
                throw new Exception(error, ex);
            }
        }

        public Task GetResultAsync(int requestId, int documentId, int tableId, int channelId, OcrVoucher voucher)
        {
            return Task.Run(() =>
            {
                int resultId;
                lock (processingLock)
                {
                    resultId = GetIcrChannelResult(channelId, requestId, 3000);
                }
                if (resultId == requestId) Functions.GetResult(a2IaEngine, resultId, voucher);
                //ReleaseResources(requestId, documentId, tableId, channelId);
                a2IaEngine.ScrCloseRequest(requestId);
            });
        }

        public void ProcessVoucherThreaded(IList<OcrVoucher> vouchers)
        {
            int channelId;

            try
            {
                channelId = OpenChannel();
                foreach (var voucher in vouchers)
                {
                    var tableId = LoadDocumentTables(SetVoucherConfiguration(voucher));
                    var documentId = GetDefaultDocument(tableId);
                    PrepareRequestForCheckReader(documentId);

                    try
                    {
                        if (loadMethod == LoadMethod.File) a2IaEngine.ScrDefineImage(documentId, "TIFF", "FILE", voucher.ImagePath);
                        voucher.RequestId = OpenIcrChannel(channelId, documentId);

                        //Release document resources
                        ReleaseDocumentResources(documentId, tableId);
                    }
                    catch (Exception ex)
                    {
                        Log.Error(ex, "An error has ocurred while processing the image for voucher {@voucherId}", voucher.Id);
                        if (voucher.RequestId > 0)
                        {
                            var requestId = voucher.RequestId;
                            Policy.Handle<Exception>()
                                .CircuitBreaker(3, TimeSpan.FromMilliseconds(200))
                                .Execute(() => a2IaEngine.ScrCloseRequest(requestId));
                        }
                        throw;
                    }
                }
            }
            catch (Exception ex)
            {
                var error = a2IaEngine.ScrGetLastError();
                Shutdown();
                throw new Exception(error, ex);
            }
            const int processorCount = 40; // configuration for production system
            var maxThreadCount = Math.Min(Environment.ProcessorCount - 1, processorCount);
            var documentCount = vouchers.Count;

            ThreadPool.QueueUserWorkItem(state =>
                {
                    // Queue up to maxThreadCout of threads at a time
                    const int sourceFileIndex = 0;
                    var documentLeft = documentCount;
                    while (documentLeft > 0)
                    {
                        int batchCount = Math.Min(maxThreadCount, documentLeft);
                        using (batchFinishedEvent = new AutoResetEvent(false))
                        {
                            for (int i = 0; i < batchCount; i++)
                            {
                                var data = new ProcessData
                                {
                                    Voucher = vouchers[i + sourceFileIndex],
                                    ChannelId = channelId
                                };
                                ThreadPool.QueueUserWorkItem(ThreadProc, data);
                            }
                            batchFinishedEvent.WaitOne();
                        }
                    }
                });

            //Release channel resources
            ReleaseDocumentResources(0, channelId);
        }

        private AutoResetEvent batchFinishedEvent;
        private struct ProcessData
        {
            public int ChannelId { get; set; }
            public OcrVoucher Voucher { get; set; }
        }

        private void ThreadProc(object stateInfo)
        {
            var data = (ProcessData)stateInfo;
            var channelId = data.ChannelId;
            var requestId = data.Voucher.RequestId;
            var voucher = data.Voucher;
            int resultId;
            lock (processingLock)
            {
                resultId = GetIcrChannelResult(channelId, requestId, 3000);
            }
            if (resultId == requestId) Functions.GetResult(a2IaEngine, resultId, voucher);
            //ReleaseResources(requestId, documentId, tableId, channelId);
            a2IaEngine.ScrCloseRequest(requestId);
        }

        public void Shutdown()
        {
            a2IaEngine.ScrClose();
        }
        
        public void ProcessBatch(OcrBatch batch)
        {
            ProcessVoucherAsync(batch.Vouchers.Where(v => v.VoucherType == VoucherType.Debit).ToList());
            ProcessVoucherAsync(batch.Vouchers.Where(v => v.VoucherType == VoucherType.Credit).ToList());
        }

        public event BatchCompleteDelegate BatchComplete;
    }
}
