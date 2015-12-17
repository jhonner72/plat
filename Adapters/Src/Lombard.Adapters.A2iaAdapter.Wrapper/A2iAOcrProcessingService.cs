using A2iACheckReaderLib;
using Lombard.Adapters.A2iaAdapter.Wrapper.Enums;
using Polly;
using Serilog;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lombard.Adapters.A2iaAdapter.Wrapper.So
{
    public class A2iAOcrProcessingService : IOcrProcessingService
    {
        private readonly API _a2iaEngine;
        private readonly string _parameterDirectory;
        private readonly string _documentTableFilename;
        private readonly LoadMethod _loadMethod;
        private readonly object processingLock = new object();
        
        public A2iAOcrProcessingService(API a2iaEngine, string parameterPath, string tableFilename, LoadMethod loadMethod)
        {
            _a2iaEngine = a2iaEngine;
            _parameterDirectory = parameterPath;
            _documentTableFilename = tableFilename;
            _loadMethod = loadMethod;
        }

        private int OpenChannel(int timeout = 10000)
        {
            int channelId = 0;

            try
            {
                int errorId = 0;
                var channelParmId = (int)_a2iaEngine.ScrCreateChannelParam();
                var machineName = System.Environment.MachineName;
                var processorCount = System.Environment.ProcessorCount;
                for (var i = 0; i < processorCount; i++)
                {
                    // Set CPU properties
                    _a2iaEngine.SetProperty(channelParmId, string.Format("CPU[{0}].cpuServer", i + 1), machineName);
                    // Set parms properties, find on the main page of the configuration tools
                    _a2iaEngine.SetProperty(channelParmId, string.Format("CPU[{0}].paramDir", i + 1), _parameterDirectory);
                }

                errorId = (int)_a2iaEngine.ScrOpenChannelExt2(ref channelId, channelParmId, timeout);
                if (errorId != 0) throw new Exception((string)_a2iaEngine.ScrGetErrorMessage(errorId));
                return channelId;
            }
            catch (Exception)
            {
                var error = _a2iaEngine.ScrGetLastError();
                throw new Exception(error);
            }
        }

        private int LoadDocumentTables(string filename)
        {
            return (int)_a2iaEngine.ScrOpenDocumentTable(filename);
        }

        private int GetDefaultDocument(int tableId)
        {
            return (int)_a2iaEngine.ScrGetDefaultDocument(tableId);
        }

        private void PrepareRequestForCheckReader(int documentId)
        {
            var fieldName = Constants.EngineFields.Amount;
            var value = Constants.Enabled;
            _a2iaEngine.set_ObjectProperty(documentId, fieldName, value);
        }

        private int OpenIcrChannel(int channelId, int documentId)
        {
            return (int)_a2iaEngine.ScrOpenRequest(channelId, documentId);
        }

        private int GetIcrChannelResult(int channelId, int requestId, int timeout)
        {
            var result = 0;
            try
            {
                result = (int)_a2iaEngine.ScrGetResult(channelId, 0, timeout);
            }
            catch (Exception)
            {
                _a2iaEngine.ScrCloseRequest(requestId);
                throw;
            }
            return result;
        }

        private byte[] LoadFromMemory(int documentId, byte[] imageBuffer)
        {
            // Following Image Parameters required to be set correctly
            System.Array sysArray = imageBuffer; // Intermediate Array use to store the MyimageBuffer.
            _a2iaEngine.SetProperty(documentId, "additionalPages[1].imageSourceType", "Memory");
            _a2iaEngine.SetProperty(documentId, "additionalPages[1].inputFormat", "TIF"); //' for  If MyImagebuffer is a TIFF buffer.

            // Then Set the buffer to the corresponding A2iA imageBuffer
            _a2iaEngine.ScrSetBuffer(documentId, "additionalPages[1].imageSourceTypeInfo.CaseMemory.buffer", ref sysArray);

            return (byte[])sysArray;
        }

        private void GetResult(int resultId, Domain.OcrVoucher voucher)
        {
            voucher.AmountResult.Result = _a2iaEngine.GetStringProperty(resultId, Constants.ResultFields.Amount);
            voucher.AmountResult.Score = _a2iaEngine.GetStringProperty(resultId, Constants.ResultFields.AmountConfidence);
            Log.Verbose("v {0} - amt {1} score {2}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
        }

        private void ReleaseResources(int requestId, int documentId, int tableId, int channelId)
        {
            if (requestId != 0) _a2iaEngine.ScrCloseRequest(requestId);
            if (documentId != 0) _a2iaEngine.ScrCloseDocument(documentId);
            if (tableId != 0) _a2iaEngine.ScrCloseDocumentTable(tableId);
            if (channelId != 0) _a2iaEngine.ScrCloseChannel(channelId);
        }

        public void Initialise()
        {
            _a2iaEngine.ScrInit(_parameterDirectory);
        }

        public async Task ProcessVoucherAsync(IList<Domain.OcrVoucher> vouchers)
        {
            int channelId = 0;
            int tableId = 0;
            int documentId = 0;

            try
            {
                channelId = OpenChannel();
                tableId = LoadDocumentTables(_documentTableFilename);
                documentId = GetDefaultDocument(tableId);
                PrepareRequestForCheckReader(documentId);
                foreach (var voucher in vouchers)
                {
                    try
                    {
                        if (_loadMethod == LoadMethod.File) _a2iaEngine.ScrDefineImage(documentId, "TIFF", "FILE", voucher.ImagePath);
                        if (_loadMethod == LoadMethod.Mem)
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
                                                _a2iaEngine.ScrDefineImage(documentId, "TIFF", "MEM", closureVoucher.ImageBuffer);
                                                closureVoucher.RequestId = (int)_a2iaEngine.ScrOpenRequest(channelId, documentId);
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
                                        _a2iaEngine.ScrCloseRequest(closureVoucher.RequestId);
                                    }
                                }
                            }
                        }
                        voucher.RequestId = OpenIcrChannel(channelId, documentId);

                    }
                    catch (Exception ex)
                    {
                        var error = _a2iaEngine.ScrGetLastError();
                        //Log.Error(ex, "An error has ocurred while processing the image for voucher {@voucherId}", voucher.Id);
                        if (voucher.RequestId > 0)
                        {
                            Policy.Handle<Exception>()
                                .CircuitBreaker(3, TimeSpan.FromMilliseconds(200))
                                .Execute(() => _a2iaEngine.ScrCloseRequest(voucher.RequestId));
                        }
                        throw ex;
                    }
                }
            }
            catch (Exception ex)
            {
                var error = _a2iaEngine.ScrGetLastError();
                Shutdown();
                throw new Exception(error, ex);
            }

            foreach (var voucher in vouchers)
            {
                await GetResultAsync(voucher.RequestId, documentId, tableId, channelId, voucher);
            }
            ReleaseResources(0, documentId, tableId, channelId);
        }

        public Task GetResultAsync(int requestId, int documentId, int tableId, int channelId, Domain.OcrVoucher voucher)
        {
            return Task.Run(() =>
            {
                int resultId;
                lock (processingLock)
                {
                    resultId = GetIcrChannelResult(channelId, requestId, 3000);
                }
                if (resultId == requestId) GetResult(resultId, voucher);
                _a2iaEngine.ScrCloseRequest(requestId);
            });
        }

        public void Shutdown()
        {
            _a2iaEngine.ScrClose();
        }


        public void SetParameters(string parameterPath, string tableFilename, LoadMethod loadMethod)
        {
            throw new NotImplementedException();
        }
    }
}
