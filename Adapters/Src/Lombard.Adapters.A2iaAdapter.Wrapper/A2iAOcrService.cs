using A2iACheckReaderLib;
using Polly;
using Serilog;
using System;
using System.Diagnostics;
using System.Threading.Tasks;
using Lombard.Adapters.A2iaAdapter.Wrapper.Configuration;
using Lombard.Adapters.A2iaAdapter.Wrapper.Domain;
#pragma warning disable 1998

namespace Lombard.Adapters.A2iaAdapter.Wrapper
{

    public class A2iAOcrService : IOcrProcessingService
    {
        private readonly API a2IaEngine;
        // ReSharper disable once NotAccessedField.Local
        private readonly IA2iaConfiguration configuration;
        private bool disposed = false;

        protected int ChannelId;
        protected int TableId;
        protected int DocumentId;

        public A2iAOcrService(API a2IaEngine, IA2iaConfiguration configuration)
        {
            this.a2IaEngine = a2IaEngine;
            this.configuration = configuration;
        }

        protected bool ChannelOpen { get; private set; }

        private readonly Policy waitRetryPolicy = Policy.Handle<Exception>().WaitAndRetry(new[]
        {
            TimeSpan.FromSeconds(1),
            TimeSpan.FromSeconds(4),
            TimeSpan.FromSeconds(10)
        }, (exception, timespan) =>
        {
            object methodThatRaisedException = timespan;
            Log.Error(exception, "The timespan {0} has raised an exception caught by the Retry Policy", methodThatRaisedException);
            Debug.WriteLine("The timespan {0} has raised an exception caught by the Retry Policy, Exception: {1}", methodThatRaisedException, exception.Message);
        });

        private readonly Policy openChannelPolicy = Policy.Handle<Exception>().WaitAndRetry(new[]
        {
            TimeSpan.FromMilliseconds(200), 
            TimeSpan.FromMilliseconds(1000),
            TimeSpan.FromMilliseconds(7000),
            TimeSpan.FromMilliseconds(12000)
        }, (exception, timespan) =>
        {
            object methodThatRaisedException = timespan;
            Log.Error(exception, "The timespan {0} has raised an exception caught by the Open Channel Policy", methodThatRaisedException);
            Debug.WriteLine("The timespan {0} has raised an exception caught by the Open Channel Policy, Exception: {1}", methodThatRaisedException, exception.Message);
        });

        private readonly Policy circuitBreakerPolicy = Policy.Handle<Exception>().CircuitBreaker(5, TimeSpan.FromSeconds(5));

        public void Initialise()
        {
            a2IaEngine.ScrInit(configuration.ParameterPath);
        }

        public void OpenOcrChannel()
        {
            OpenOcrChannel(configuration.TablePath, configuration.MaxProcessorCount);
        }

        public void OpenOcrChannel(string documentTableFilename, int maxProcessorCount = 0)
        {
            if (ChannelOpen) return;

            try
            {
                ChannelId = maxProcessorCount <= 0 ? OpenChannel(1) : OpenChannel(maxProcessorCount);
                TableId = LoadDocumentTables(documentTableFilename);
            }
            catch (Exception ex)
            {
                var error = a2IaEngine.ScrGetLastError();
                CloseOcrChannel();
                throw new Exception(error, ex);
            }
        }

        public void SelectDocumentTable(string documentName)
        {
            DocumentId = GetDocument(TableId, documentName);
            Debug.WriteLine("Selected Document Id is {0} for requested document {1}", DocumentId, documentName);
            PrepareRequestForCheckReader(DocumentId);
        }

        public void CloseOcrChannel()
        {
            CloseDocument(DocumentId);
            CloseDocumentTables(TableId);
            CloseChannel(ChannelId);
        }

        public void ProcessVoucher(OcrVoucher voucher)
        {
            try
            {
                LoadImage(DocumentId, voucher.ImagePath);
                voucher.RequestId = OpenIcrChannel(ChannelId, DocumentId);
            }
            catch (Exception ex)
            {
                Log.Error(ex, "An error has ocurred while processing the image for voucher {@voucherId}", voucher.Id);
                CloseChannel(ChannelId);
                throw;
            }
        }

        // ReSharper disable once UnusedMember.Local
        private async Task LoadMem(int channelId, int documentId, OcrVoucher voucher)
        {
            using (var imageFile = System.IO.File.OpenRead(voucher.ImagePath))
            {
                var closureVoucher = voucher;
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
                                a2IaEngine.ScrDefineImage(documentId, configuration.FileType, "MEM", closureVoucher.ImageBuffer);
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
                    Log.Error(ex, "An error has ocurred while processing the image for voucher {@voucherId}",
                        closureVoucher.Id);
                    if (closureVoucher.RequestId > 0)
                    {
                        a2IaEngine.ScrCloseRequest(closureVoucher.RequestId);
                    }
                }
            }
        }

        public virtual void Shutdown()
        {
            ReleaseResources();
            a2IaEngine.ScrClose();
        }

        public event BatchCompleteDelegate BatchComplete;

        #region A2iA specific methods
        /// <summary>
        /// Open A2iA channel
        /// </summary>
        /// <returns>Channel Id</returns>
        private int OpenChannel(int maxProcessorCount)
        {
            try
            {
                var channelParmId = (int)a2IaEngine.ScrCreateChannelParam();
                var machineName = Environment.MachineName;
                for (var i = 0; i < GetMaxThreadCount(maxProcessorCount); i++)
                {
                    // Set CPU properties
                    a2IaEngine.SetProperty(channelParmId, string.Format("CPU[{0}].cpuServer", i + 1), machineName);
                    // Set parms properties, find on the main page of the configuration tools
                    a2IaEngine.SetProperty(channelParmId, string.Format("CPU[{0}].paramDir", i + 1), configuration.ParameterPath);
                }

                // ReSharper disable once RedundantAssignment
                var errorId = 0;
                openChannelPolicy.Execute(() => errorId = (int)a2IaEngine.ScrOpenChannelExt2(ref ChannelId, channelParmId, configuration.ChannelTimeout));
                if (errorId != 0) throw new Exception(a2IaEngine.ScrGetErrorMessage(errorId));
                Debug.WriteLine("Channel Id {0}", ChannelId);
                ChannelOpen = true;
                return ChannelId;
            }
            catch (Exception ex)
            {
                var error = a2IaEngine.ScrGetLastError();
                var status = a2IaEngine.ScrGetChannelStatus(ChannelId);

                //Get CPU error description
                var faultCpu = a2IaEngine.ObjectProperty[(int)status, "A2iARC_Unsigned A2iARC_ChannelStatus.faultCpu"];
                var cTab = a2IaEngine.ObjectProperty[(int)status, "A2iARC_Unsigned A2iARC_ChannelStatus.Cpu.cTab"];
                var pTab = a2IaEngine.ObjectProperty[(int)status, "A2iARC_CPUStatus A2iARC_ChannelStatus.Cpu.pTab"];

                var faultCpuStatus = "";
                if (int.Parse(faultCpu.ToString()) != 0) faultCpuStatus = a2IaEngine.ObjectProperty[(int)status, string.Format("A2iARC_ChannelStatus.Cpu.pTab[{0}].status", faultCpu)].ToString();

                Log.Error(ex, "Open Channel Error {0}, status {1}, faultCpu {2}, cTab {3}, pTab {4}, faultCpuStatus {5}", error, status, faultCpu, cTab, pTab, faultCpuStatus);
                a2IaEngine.ScrCloseChannel(ChannelId);

                throw new Exception(error);
            }
        }

        /// <summary>
        /// Close A2iA channel with the aid of a Circuit Breaker Policy
        /// </summary>
        /// <param name="channelId">The channel being closed</param>
        private void CloseChannel(int channelId)
        {
            if (channelId == 0) return;
            circuitBreakerPolicy.Execute(() =>
            {
                a2IaEngine.ScrCloseChannel(channelId);
                ChannelOpen = false;
            });
            ChannelId = 0;
        }

        /// <summary>
        /// Open A2iA document table
        /// </summary>
        /// <param name="filename">Filename where the document table resides</param>
        /// <returns>Document Id</returns>
        private int LoadDocumentTables(string filename)
        {
            var result = 0;
            try
            {
                openChannelPolicy.Execute(() => result = (int)a2IaEngine.ScrOpenDocumentTable(filename));
            }
            catch (Exception)
            {
                if (result != 0) CloseDocumentTables(result);
                throw;
            }
            return result;
        }

        /// <summary>
        /// Close A2iA document table with the aid of a Circuit Breaker Policy
        /// </summary>
        /// <param name="tableId">Document table being closed</param>
        private void CloseDocumentTables(int tableId)
        {
            if (tableId == 0) return;
            circuitBreakerPolicy.Execute(() => a2IaEngine.ScrCloseDocumentTable(tableId));
            TableId = 0;
        }

        /// <summary>
        /// Open A2iA default document within the document table
        /// </summary>
        /// <param name="tableId">Table where the default document resides</param>
        /// <returns>Document Id</returns>
        // ReSharper disable once UnusedMember.Local
        private int GetDefaultDocument(int tableId)
        {
            var result = 0;
            try
            {
                openChannelPolicy.Execute(() => result = (int)a2IaEngine.ScrGetDefaultDocument(tableId));
            }
            catch (Exception)
            {
                if (result != 0) CloseDocument(result);
                throw;
            }
            return result;
        }

        private int GetDocument(int tableId, string key)
        {
            var result = 0;
            try
            {
                openChannelPolicy.Execute(() => result = (int)a2IaEngine.ScrGetStringDocument(tableId, key));
            }
            catch (Exception)
            {
                if (result != 0) CloseDocument(result);
                throw;
            }
            return result;
        }

        /// <summary>
        /// Close A2iA document with the aid of Circuit Breaker Policy
        /// </summary>
        /// <param name="documentId">The document being closed</param>
        private void CloseDocument(int documentId)
        {
            if (documentId == 0) return;
            circuitBreakerPolicy.Execute(() => a2IaEngine.ScrCloseDocument(documentId));
            DocumentId = 0;
        }

        /// <summary>
        /// Prepares the A2iA engine to return the Amount value on a cheque
        /// </summary>
        /// <param name="documentId">The document being referenced by the OCR engine
        /// to identify where the amount is expected to be found</param>
        private void PrepareRequestForCheckReader(int documentId)
        {
            const string fieldName = Constants.EngineFields.Amount;
            const string value = Constants.Enabled;
            // ReSharper disable once UseIndexedProperty
            openChannelPolicy.Execute(() => a2IaEngine.set_ObjectProperty(documentId, fieldName, value));
        }

        /// <summary>
        /// Open A2iA request
        /// </summary>
        /// <param name="channelId">OCR Channel</param>
        /// <param name="documentId">OCR document definition</param>
        /// <returns>Request Id</returns>
        private int OpenIcrChannel(int channelId, int documentId)
        {
            var result = 0;
            openChannelPolicy.Execute(() => result = (int)a2IaEngine.ScrOpenRequest(channelId, documentId));
            return result;
        }

        /// <summary>
        /// Get the A2iA Result
        /// </summary>
        /// <param name="channelId"></param>
        /// <param name="requestId"></param>
        /// <param name="timeout"></param>
        /// <returns>Result Id</returns>
        private int GetIcrChannelResult(int channelId, int requestId, int timeout)
        {
            // ReSharper disable once RedundantAssignment
            var result = 0;
            try
            {
                waitRetryPolicy.Execute(() =>
                {
                    result = GetIcrChannelResult(requestId, timeout, true);
                });
            }
            catch (Exception)
            {
                Debug.WriteLine("GetIcrChannelResult Exception channelId {0}, requestId {1}", channelId, requestId);
                if (requestId != 0) CloseRequest(requestId);
                throw;
            }
            return result;
        }

        private int GetIcrChannelResult(int requestId, int timeout, bool useExt = false)
        {
            //var requestId = 0;
            if (useExt)
            {
                try
                {
                    var errorCode = (int) a2IaEngine.ScrGetResultExt(ChannelId, requestId, timeout);
                    if (errorCode != 0)
                    {
                        var errorMessage = a2IaEngine.ScrGetErrorMessage(errorCode);
                        Log.Error("GetIcrChannelResult has thrown error id {0} for request {1} with message {2}", errorCode,
                            requestId, errorMessage);
                    }
                }
                catch (Exception ex)
                {
                    var error = a2IaEngine.ScrGetLastError();
                    Log.Error(ex, "GetIcrChannelResult caught unhandled A2iA exception {0} request id {1} using timeout {2}", error, requestId, timeout);
                    throw;
                }
            }
            else
            {
                requestId = (int)a2IaEngine.ScrGetResult(ChannelId, 0, timeout);
            }
            return requestId;
        }

        /// <summary>
        /// Close A2iA request
        /// </summary>
        /// <param name="requestId">Request Id to be closed</param>
        private void CloseRequest(int requestId)
        {
            if (requestId == 0) return;
            circuitBreakerPolicy.Execute(() => a2IaEngine.ScrCloseRequest(requestId));
        }

        /// <summary>
        /// Defines an image for A2iA to OCR
        /// </summary>
        /// <param name="documentId">Document template used for OCR</param>
        /// <param name="imagePath">Path to image source</param>
        private void LoadImage(int documentId, string imagePath)
        {
            openChannelPolicy.Execute(() => a2IaEngine.ScrDefineImage(documentId, configuration.FileType, "FILE", imagePath));
        }

        /// <summary>
        /// Release the A2iA resources
        /// </summary>
        private void ReleaseResources()
        {
            const string format = "ReleaseResources close {0} {1}";
            try
            {
                if (DocumentId != 0)
                {
                    Debug.WriteLine(format, "document", DocumentId);
                    a2IaEngine.ScrCloseDocument(DocumentId);
                    DocumentId = 0;
                }
                if (TableId != 0)
                {
                    Debug.WriteLine(format, "table", TableId);
                    a2IaEngine.ScrCloseDocumentTable(TableId);
                    TableId = 0;
                }
                if (ChannelId == 0) return;
                Debug.WriteLine(format, "channel", ChannelId);
                a2IaEngine.ScrCloseChannel(ChannelId);
                ChannelId = 0;
                ChannelOpen = false;
            }
            catch (Exception ex)
            {
                Debug.WriteLine("An exception has occurred in ReleaseResources documentId {0}, tableId {1}, channelId {2}", DocumentId, TableId, ChannelId);
                var error = a2IaEngine.ScrGetLastError();
                var status = a2IaEngine.ScrGetChannelStatus(ChannelId);// CheckChannelStatus()
                Debug.WriteLine("Error {0}, Status {1}", error, status);
                Log.Error(ex, "Error {0}, Status {1}", error, status);
                throw;
            }
        }
        #endregion

        public static readonly int ProcessorCount = Environment.ProcessorCount - 1;

        public static int GetMaxThreadCount(int maxProcessorCount)
        {
            return Math.Min(ProcessorCount, maxProcessorCount);
        }

        public static bool ExceedsMaxProcessors(int maxProcessorCount)
        {
            return ProcessorCount < maxProcessorCount;
        }

        public static int ExceedsMaxProcessorsBy(int maxProcessorCount)
        {
            return maxProcessorCount - ProcessorCount;
        }

        public void GetIcrChannelResult(OcrVoucher voucher)
        {
            var requestId = 0;
            try
            {
                requestId = GetIcrChannelResult(ChannelId, voucher.RequestId, configuration.ChannelTimeout);
                if (voucher.RequestId == requestId) Functions.GetResult(a2IaEngine, requestId, voucher);
                CloseRequest(requestId);
            }
            catch (Exception ex)
            {
                var errorMessage = a2IaEngine.ScrGetLastError();
                Log.Error(ex, "An error has ocurred while processing the image for voucher {@voucherId} with message {1}", voucher.Id, errorMessage);
                if (requestId > 0) CloseRequest(requestId);
                throw;
            }
        }

        public void ProcessBatch(OcrBatch batch)
        {
            foreach (var ocrVoucher in batch.Vouchers)
            {
                ProcessVoucher(ocrVoucher);
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
                // Free managed code here
            }
            // free unmanaged code here
            Shutdown();
            disposed = true;
        }
    }
}
