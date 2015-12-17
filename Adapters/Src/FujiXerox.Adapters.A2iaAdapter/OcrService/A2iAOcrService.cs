using System;
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using System.Threading.Tasks;
using A2iACheckReaderLib;
using FujiXerox.Adapters.A2iaAdapter.Configuration;
using FujiXerox.Adapters.A2iaAdapter.Enums;
using FujiXerox.Adapters.A2iaAdapter.Model;
using Serilog;

namespace FujiXerox.Adapters.A2iaAdapter.OcrService
{
    
    public abstract class A2iAOcrService : IOcrProcessingService
    {
        private readonly API _a2IaEngine;
        private readonly string _parameterDirectory;
        private readonly LoadMethod _loadMethod;
        private readonly string _fileType;
        private readonly int _channelTimeout;
        private readonly IA2iaConfiguration _configuration;

        private bool _disposed;
        //private int maxProcessorCount;
        //private string documentTableFilename;

        private int _channelId;
        private int _tableId;
        private int _documentId;

        public A2iAOcrService(IA2iaConfiguration configuration)
        {
            _a2IaEngine = new APIClass();
            _parameterDirectory = configuration.ParameterPath;
            _loadMethod = configuration.LoadMethod;
            _fileType = configuration.FileType;
            _channelTimeout = configuration.ChannelTimeout;

            _configuration = configuration;
        }

        private bool ChannelOpen { get; set; }

        public void Initialise()
        {
            _a2IaEngine.ScrInit(_parameterDirectory);
        }

        public abstract bool ProcessBatch(OcrBatch batch);

        public void OpenOcrChannel()
        {
            OpenOcrChannel(_configuration.TablePath, _configuration.MaxProcessorCount);
        }

        private void OpenOcrChannel(string documentTableFilename, int maxProcessorCount = 0)
        {
            if (ChannelOpen) return;

            try
            {
                _channelId = maxProcessorCount == 0 ? OpenChannel() : OpenChannel(maxProcessorCount);
                _tableId = LoadDocumentTables(documentTableFilename);
                //DocumentId = GetDefaultDocument(TableId);
                //PrepareRequestForCheckReader(DocumentId);
            }
            catch (Exception ex)
            {
                var error = _a2IaEngine.ScrGetLastError();
                Log.Error(ex, "An error occurred attempting to open the A2iA channel {0}", error);
                CloseOcrChannel();
                throw new Exception(error, ex);
            }
        }

        public void SelectDocumentTable(string documentName)
        {
            _documentId = GetDocument(_tableId, documentName);
            Log.Debug("Selected Document Id is {0} for requested document {1}", _documentId, documentName);
            PrepareRequestForCheckReader(_documentId);
        }

        public void CloseOcrChannel()
        {
            CloseDocument(_documentId);
            CloseDocumentTables(_tableId);
            CloseChannel(_channelId);
        }

        public void ProcessVoucher(OcrVoucher voucher)
        {
            try
            {
                LoadImage(_documentId, voucher.ImagePath);
                voucher.RequestId = OpenIcrChannel(_channelId, _documentId);
                Log.Debug("Voucher Request {0} in progress", voucher.RequestId);
            }
            catch (Exception ex)
            {
                Log.Error(ex, "An error has ocurred while processing the image for voucher {@voucherId}", voucher.Id);
                CloseChannel(_channelId);
                throw;
            }
        }

        private async Task LoadMem(int channelId, int documentId, OcrVoucher voucher)
        {
            using (var imageFile = File.OpenRead(voucher.ImagePath))
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
                                _a2IaEngine.ScrDefineImage(documentId, _fileType, "MEM", closureVoucher.ImageBuffer);
                                closureVoucher.RequestId = (int)_a2IaEngine.ScrOpenRequest(channelId, documentId);
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
                        _a2IaEngine.ScrCloseRequest(closureVoucher.RequestId);
                    }
                }
            }
        }

        public virtual void Shutdown()
        {
            ReleaseResources();
            _a2IaEngine.ScrClose();
            Log.Information("Closing A2iA engine");
        }

        public event BatchCompleteDelegate BatchComplete;

        protected void OnBatchComplete(object sender, OcrBatch batch)
        {
            var batchComplete = BatchComplete;
            if (batchComplete != null) batchComplete(sender, batch);
        }

        #region A2iA specific methods
        /// <summary>
        /// Open A2iA channel
        /// </summary>
        /// <returns>Channel Id</returns>
        private int OpenChannel(int maxProcessorCount)
        {
            try
            {
                var channelParmId = (int)_a2IaEngine.ScrCreateChannelParam();
                var machineName = Environment.MachineName;
                for (var i = 0; i < GetMaxThreadCount(maxProcessorCount); i++)
                {
                    // Set CPU properties
                    _a2IaEngine.SetProperty(channelParmId, string.Format("CPU[{0}].cpuServer", i + 1), machineName);
                    // Set Dongle properties
                    _a2IaEngine.SetProperty(channelParmId, string.Format("CPU[{0}].dongleServer", i + 1), machineName);
                    // Set parms properties, find on the main page of the configuration tools
                    _a2IaEngine.SetProperty(channelParmId, string.Format("CPU[{0}].paramDir", i + 1), _parameterDirectory);
                }

                // ReSharper disable once RedundantAssignment
                var errorId = 0;
                Log.Debug("Opening Channel with parameter {0}", channelParmId);
                //openChannelPolicy.Execute(() => errorId = (int)a2IaEngine.ScrOpenChannelExt2(ref ChannelId, channelParmId, channelTimeout));
                errorId = (int)_a2IaEngine.ScrOpenChannelExt2(ref _channelId, channelParmId, _channelTimeout);
                if (errorId != 0) throw new Exception(_a2IaEngine.ScrGetErrorMessage(errorId));
                Log.Debug("Channel Id {0}", _channelId);
                ChannelOpen = true;
                return _channelId;
            }
            catch (Exception ex)
            {
                var error = _a2IaEngine.ScrGetLastError();
                var status = _a2IaEngine.ScrGetChannelStatus(_channelId);// CheckChannelStatus()
                Log.Error(ex, "Open Channel Error {0}, Status {1}", error, status);
                CloseChannel(_channelId);
                throw new Exception(error);
            }
        }

        private int OpenChannel()
        {
            try
            {
                //openChannelPolicy.Execute(() => ChannelId = (int)a2IaEngine.ScrOpenChannel());
                _channelId = (int)_a2IaEngine.ScrOpenChannel();
                System.Diagnostics.Debug.WriteLine("Channel Id {0}", _channelId);
                ChannelOpen = true;
                return _channelId;
            }
            catch (Exception ex)
            {
                var error = _a2IaEngine.ScrGetLastError();
                var status = _a2IaEngine.ScrGetChannelStatus(_channelId);// CheckChannelStatus()
                Log.Error(ex, "Open Channel Error {0}, Status {1}", error, status);
                CloseChannel(_channelId);
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
            //circuitBreakerPolicy.Execute(() =>
            //{
                _a2IaEngine.ScrCloseChannel(channelId);
                ChannelOpen = false;
            //});
            _channelId = 0;
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
                //openChannelPolicy.Execute(() => result = (int)a2IaEngine.ScrOpenDocumentTable(filename));
                result = (int) _a2IaEngine.ScrOpenDocumentTable(filename);
            }
            catch (Exception ex)
            {
                var codebase = Assembly.GetExecutingAssembly().CodeBase;
                var uri = new UriBuilder(codebase);
                var path = Uri.UnescapeDataString(uri.Path);
                Log.Error(ex, "An error has occurred attempting to load the document table {0} from path {1}", filename, path);
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
            //circuitBreakerPolicy.Execute(() => a2IaEngine.ScrCloseDocumentTable(tableId));
            _a2IaEngine.ScrCloseDocumentTable(tableId);
            _tableId = 0;
        }

        /// <summary>
        /// Open A2iA default document within the document table
        /// </summary>
        /// <param name="tableId">Table where the default document resides</param>
        /// <returns>Document Id</returns>
        private int GetDefaultDocument(int tableId)
        {
            var result = 0;
            try
            {
                //openChannelPolicy.Execute(() => result = (int)a2IaEngine.ScrGetDefaultDocument(tableId));
                result = (int) _a2IaEngine.ScrGetDefaultDocument(tableId);
            }
            catch (Exception ex)
            {
                Log.Error(ex, "An error has occurred trying to open the default document");
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
                //openChannelPolicy.Execute(() => result = (int) a2IaEngine.ScrGetStringDocument(tableId, key));
                result = (int) _a2IaEngine.ScrGetStringDocument(tableId, key);
            }
            catch (Exception ex)
            {
                Log.Error(ex, "An error has occured trying to get document from table {0} with key {1}", tableId, key);
                if(result!=0) CloseDocument(result);
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
            //circuitBreakerPolicy.Execute(() => a2IaEngine.ScrCloseDocument(documentId));
            _a2IaEngine.ScrCloseDocument(documentId);
            _documentId = 0;
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
            //openChannelPolicy.Execute(() => a2IaEngine.set_ObjectProperty(documentId, fieldName, value));
            _a2IaEngine.set_ObjectProperty(documentId, fieldName, value);
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
            //openChannelPolicy.Execute(() => result = (int)a2IaEngine.ScrOpenRequest(channelId, documentId));
            result = (int) _a2IaEngine.ScrOpenRequest(channelId, documentId);
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
                //waitRetryPolicy.Execute(() =>
                //{
                    result = GetIcrChannelResult(requestId, timeout, true);
                //});
            }
            catch (Exception ex)
            {
                var errorMessage = _a2IaEngine.ScrGetLastError();
                Log.Error(ex, "GetIcrChannelResult Exception channelId {0}, requestId {1}, with error {2}", channelId, requestId, errorMessage);
                if (requestId != 0) CloseRequest(requestId);
                throw;
            }
            return result;
        }

        private int GetIcrChannelResult(int requestId, int timeout, bool useExt = false)
        {
            if (useExt)
            {
                var errorCode = (int)_a2IaEngine.ScrGetResultExt(_channelId, requestId, timeout);
                if (errorCode != 0)
                {
                    Log.Error("GetIcrChannelResult has thrown error id {0} for request {1}", errorCode, requestId);
                }
            }
            else
            {
                requestId = (int)_a2IaEngine.ScrGetResult(_channelId, 0, timeout);
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
            //circuitBreakerPolicy.Execute(() => a2IaEngine.ScrCloseRequest(requestId));
            _a2IaEngine.ScrCloseRequest(requestId);
        }
        
        /// <summary>
        /// Defines an image for A2iA to OCR
        /// </summary>
        /// <param name="documentId">Document template used for OCR</param>
        /// <param name="imagePath">Path to image source</param>
        private void LoadImage(int documentId, string imagePath)
        {
            //openChannelPolicy.Execute(() => a2IaEngine.ScrDefineImage(documentId, fileType, "FILE", imagePath));
            _a2IaEngine.ScrDefineImage(documentId, _fileType, "FILE", imagePath);
        }

        /// <summary>
        /// Release the A2iA resources
        /// </summary>
        private void ReleaseResources()
        {
            const string format = "ReleaseResources close {0} {1}";
            try
            {
                if (_documentId != 0)
                {
                    System.Diagnostics.Debug.WriteLine(format, "document", _documentId);
                    _a2IaEngine.ScrCloseDocument(_documentId);
                    _documentId = 0;
                }
                if (_tableId != 0)
                {
                    System.Diagnostics.Debug.WriteLine(format, "table", _tableId);
                    _a2IaEngine.ScrCloseDocumentTable(_tableId);
                    _tableId = 0;
                }
                if (_channelId == 0) return;
                System.Diagnostics.Debug.WriteLine(format, "channel", _channelId);
                _a2IaEngine.ScrCloseChannel(_channelId);
                _channelId = 0;
                ChannelOpen = false;
            }
            catch (Exception ex)
            {
                var error = _a2IaEngine.ScrGetLastError();
                var status = _a2IaEngine.ScrGetChannelStatus(_channelId);// CheckChannelStatus()
                Log.Error(ex, "An Error has occurred whilst attempting to release resources with Error {0}, Status {1}", error, status);
                throw;
            }
        }
        #endregion

        private static readonly int ProcessorCount = Environment.ProcessorCount - 1;

        private static int GetMaxThreadCount(int maxProcessorCount)
        {
            return Math.Min(ProcessorCount, maxProcessorCount);
        }

        public void GetIcrChannelResult(OcrVoucher voucher)
        {
            var requestId = 0;
            try
            {
                requestId = GetIcrChannelResult(_channelId, voucher.RequestId, _channelTimeout);
                if (voucher.RequestId == requestId) Functions.GetResult(_a2IaEngine, requestId, voucher);
                //Console.WriteLine("Voucher request {0} being retrieved", requestId);
                CloseRequest(requestId);
            }
            catch (Exception ex)
            {
                var errorMessage = _a2IaEngine.ScrGetLastError();
                Log.Error(ex, "An error has ocurred getting the results for the image for voucher {@voucherId}, with error {1}", voucher.Id, errorMessage);
                if (requestId > 0) CloseRequest(requestId);
                throw;
            }
        }

        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        protected virtual void Dispose(bool disposing)
        {
            if (_disposed) return;
            Log.Debug("Disposing A2iAOcrService");
            if (disposing)
            {
                // Free managed objects here
            }

            // Free unmanaged objects here
            Shutdown();
            _disposed = true;
        }
    }
}
