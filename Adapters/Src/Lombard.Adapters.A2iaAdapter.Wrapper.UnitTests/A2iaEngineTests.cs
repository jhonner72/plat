using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using A2iACheckReaderLib;
using Lombard.Adapters.A2iaAdapter.Wrapper.Configuration;
using Lombard.Adapters.A2iaAdapter.Wrapper.Domain;
using Lombard.Adapters.A2iaAdapter.Wrapper.Enums;
using Microsoft.VisualStudio.TestTools.UnitTesting;
// ReSharper disable InconsistentNaming
#pragma warning disable 1998

namespace Lombard.Adapters.A2iaAdapter.Wrapper.UnitTests
{
    /// <summary>
    /// Summary description for A2iaEngineTests
    /// </summary>

    [Ignore]
    [TestClass]
    public class A2IaEngineTests
    {
        //const string VouchersPath = @"C:\Lombard\Data\Jobs\17032015-3AEA-4069-A2DD-SSSS00000012\";
        //private const string VouchersPath = @"C:\Users\Stephen\Documents\Visual Studio 2013\Projects\Data";
        private const string VouchersBasePath = @"C:\Users\Stephen\Documents\Visual Studio 2013\Projects\Data";
        private const string dpi_100_jpg = @"100dpi jpg";
        private const string dpi_200_tif = @"200dpi tif";
        private const string dpi_200_jpg = @"200dpi greyscale";
        private const string dpi_240_tif = @"240dpi tif";
        private const string dpi_200_greyscaled_tif = @"200dpi greyscaled tif";
        private const string dpi_300_upscaled_tif = @"300dpi upscaled tif";
        private const string dpi_300_upscaled_950 = @"300dpi upscaled 950";
        private const string dpi_300_upscaled_950_1 = @"300dpi upscaled 950 preProcess 1";
        private const string dpi_300_upscaled_950_2 = @"300dpi upscaled 950 preProcess 2";
        private const string dpi_300_upscaled_950_3 = @"300dpi upscaled 950 preProcess 3";
        private const string dpi_300_upscaled_950_4 = @"300dpi upscaled 950 preProcess 4";
        private const string dpi_400_upscaled_tif = @"400dpi upscaled tif";
        private const string mixed_batch = @"Mixed Batch";

        private const string Checks = @"Checks\";
        private const string ResultPath = @".\Results";
        private const string ResultFilename = @"TestResults.csv";
        private const string ParamPath = @"C:\Program Files\A2iA\A2iA CheckReader V6.0 R3\Parms\SoftInt\Parms";

        private const string TableFile = "AU_Custom Combined.tbl";
        private const string DebitTableFile = "AU_Custom.tbl";
        private const string CreditTableFile = "Test.tbl";

        private API engine1;
        private IA2iaConfiguration configuration;
        private List<OcrBatch> batches;
        private List<OcrBatch> batchesExtra;

        public class TestConfig : IA2iaConfiguration
        {

            public string ParameterPath { get; set; }
            public string TablePath { get; set; }
            public LoadMethod LoadMethod { get; set; }
            public string FileType { get; set; }
            public int ChannelTimeout { get; set; }
            public int StickyChannelTimeout { get; set; }
            public string DebitTablePath { get; set; }
            public int DebitMaxProcessorCount { get; set; }
            public string CreditTablePath { get; set; }
            public int CreditMaxProcessorCount { get; set; }
            public int MaxProcessorCount { get; set; }
        }

        [TestInitialize]
        public void TestInitialize()
        {
            engine1 = new APIClass();
            configuration = new TestConfig
            {
                ParameterPath = ParamPath,
                TablePath = TableFile,
                DebitTablePath = DebitTableFile,
                CreditTablePath = CreditTableFile,
                LoadMethod = LoadMethod.File,
                FileType = "TIFF",
                ChannelTimeout = 13000,
                StickyChannelTimeout = 1,
                MaxProcessorCount = 1,
                DebitMaxProcessorCount = 1,
                CreditMaxProcessorCount = 1
            };
            batchesExtra = new List<OcrBatch>();
        }

        [TestMethod]
        public void A2iAOcrProcessingService_Test()
        {
            // Arrange
            var batch1 = new OcrBatch
            {
                JobIdentifier = "5830013",
                Vouchers = new List<OcrVoucher>()
            };

            var VouchersPath = Path.Combine(VouchersBasePath, dpi_240_tif);

            GetAllTestVouchers(batch1, Path.Combine(VouchersPath, Checks));
            configuration.MaxProcessorCount = 4;
            var a2IaService = new A2iACombinedTableService(engine1, configuration);
            a2IaService.Initialise();

            // Act
            a2IaService.ProcessBatch(batch1);

            // Assert
            WriteResults(batch1.Vouchers);
            //foreach (var voucher in batch1.Vouchers)
            //{
            //    Debug.WriteLine("v {0,-15}\tamt {1,-25}\tscore {2,-15}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
            //}
            var resultFile = Path.Combine(ResultPath, string.Format("{0} 500 {1}", "raw", ResultFilename));
            var results = new string[batch1.Vouchers.Count() + 1];
            var index = 0;
            results[index++] = "Voucher Id, Amount, Score";
            foreach (var voucher in batch1.Vouchers)
            {
                results[index++] = string.Format("{0}, {1}, {2}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
            }
            File.WriteAllLines(resultFile, results);
            a2IaService.Shutdown();
        }

        [TestMethod]
        public void A2iAOcrProcessingService_Test_Multi_Batch_500_Images()
        {
            // Arrange
            var batch1 = new OcrBatch
            {
                JobIdentifier = "5830013",
                Vouchers = new List<OcrVoucher>()
            };

            var batch2 = new OcrBatch
            {
                JobIdentifier = "5830014",
                Vouchers = new List<OcrVoucher>()
            };

            var batch3 = new OcrBatch
            {
                JobIdentifier = "5830015",
                Vouchers = new List<OcrVoucher>()
            };

            var batch4 = new OcrBatch
            {
                JobIdentifier = "5830016",
                Vouchers = new List<OcrVoucher>()
            };

            const string VouchersPath = VouchersBasePath;
            GetAllTestVouchers(batch1, Path.Combine(VouchersPath, Checks), 0, 100);
            GetAllTestVouchers(batch2, Path.Combine(VouchersPath, Checks), 100, 200);
            GetAllTestVouchers(batch3, Path.Combine(VouchersPath, Checks), 200, 300);
            GetAllTestVouchers(batch4, Path.Combine(VouchersPath, Checks), 300, 500);

            configuration.MaxProcessorCount = 4;
            var a2IaService = new A2iACombinedTableService(engine1, configuration);
            a2IaService.Initialise();

            // Act
            a2IaService.ProcessBatch(batch1);
            a2IaService.ProcessBatch(batch2);
            a2IaService.ProcessBatch(batch3);
            a2IaService.ProcessBatch(batch4);

            // Assert
            WriteResults(batch1.Vouchers);
            WriteResults(batch2.Vouchers);
            WriteResults(batch3.Vouchers);
            WriteResults(batch4.Vouchers);
            var resultFile = Path.Combine(ResultPath, ResultFilename);
            var batchCount = 1;
            batchCount += batch1.Vouchers.Count();
            batchCount += batch2.Vouchers.Count();
            batchCount += batch3.Vouchers.Count();
            batchCount += batch4.Vouchers.Count();
            var results = new string[batchCount];
            var index = 0;
            results[index++] = "Voucher Id, Amount, Score";
            foreach (var voucher in batch1.Vouchers)
            {
                results[index++] = string.Format("{0}, {1}, {2}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
            }
            foreach (var voucher in batch2.Vouchers)
            {
                results[index++] = string.Format("{0}, {1}, {2}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
            }
            foreach (var voucher in batch3.Vouchers)
            {
                results[index++] = string.Format("{0}, {1}, {2}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
            }
            foreach (var voucher in batch4.Vouchers)
            {
                results[index++] = string.Format("{0}, {1}, {2}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
            }
            File.WriteAllLines(resultFile, results);
            a2IaService.Shutdown();
        }

        [TestMethod]
        public void A2iAOcrProcessingService_Test_Multi_100_Batch_1_Images()
        {
            // Arrange
            var jobId = 5830013;
            var ocrBatches = new List<OcrBatch>();
            const string VouchersPath = VouchersBasePath;
            for (int i = 0; i < 100; i++)
            {
                var batch = new OcrBatch { JobIdentifier = jobId++.ToString(), Vouchers = new List<OcrVoucher>() };
                ocrBatches.Add(batch);
                GetAllTestVouchers(batch, Path.Combine(VouchersPath, Checks), i, 1);
            }

            configuration.MaxProcessorCount = 4;
            var a2IaService = new A2iACombinedTableService(engine1, configuration);
            a2IaService.Initialise();

            // Act
            foreach (var batch in ocrBatches)
            {
                //await a2IaService.ProcessVoucherAsync(batch.Vouchers);
                a2IaService.ProcessBatch(batch);
            }

            // Assert
            foreach (var batch in ocrBatches)
            {
                WriteResults(batch.Vouchers);
            }
            var resultFile = Path.Combine(ResultPath, ResultFilename);
            var batchCount = 1;
            foreach (var batch in ocrBatches)
            {
                batchCount += batch.Vouchers.Count();
            }
            var results = new string[batchCount];
            var index = 0;
            results[index++] = "Voucher Id, Amount, Score";
            foreach (var batch in ocrBatches)
            {
                foreach (var voucher in batch.Vouchers)
                {
                    results[index++] = string.Format("{0}, {1}, {2}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
                }
            }
            File.WriteAllLines(resultFile, results);
            a2IaService.Shutdown();
        }
        
        [TestMethod]
        public async Task A2iABatchPoolService_Test_Async_Multi_100_Batch_4_Images_With_Open_Connection()
        {
            // Arrange
            var jobId = 5830013;
            batches = new List<OcrBatch>();
            const string VouchersPath = VouchersBasePath;
            var voucherCount = 0;
            const int voucherIncrement = 4;
            for (int i = 0; i < 100; i++)
            {
                var batch = new OcrBatch { JobIdentifier = jobId++.ToString(), Vouchers = new List<OcrVoucher>() };
                GetAllTestVouchers(batch, Path.Combine(VouchersPath, Checks), voucherCount, voucherCount + voucherIncrement);
                voucherCount += voucherIncrement;
                batches.Add(batch);
            }

            batchesExtra = new List<OcrBatch>();

            for (int i = 0; i < 100; i++)
            {
                var batch = new OcrBatch { JobIdentifier = jobId++.ToString(), Vouchers = new List<OcrVoucher>() };
                GetAllTestVouchers(batch, Path.Combine(VouchersPath, Checks), voucherCount, voucherCount + voucherIncrement);
                voucherCount += voucherIncrement;
                batchesExtra.Add(batch);
            }

            configuration.MaxProcessorCount = 2;
            var a2IaService = new A2iAStickyConnectionService(engine1, configuration);
            try
            {
                a2IaService.Initialise();
                a2IaService.BatchComplete += BatchCompleteHandler;

                // Act
                Debug.WriteLine("Act");
                foreach (var batch in batches)
                {
                    a2IaService.ProcessBatch(batch);
                }
                Debug.WriteLine(string.Format("timer {0}", DateTime.Now.ToString("hh:mm:ss.fff")));

                //a2IaService.CloseOcrChannel();
                //foreach (var batch in batchesExtra)
                //{
                //    a2IaService.ProcessBatch(batch);
                //}

                Thread.Sleep(configuration.StickyChannelTimeout + 5000);
            }
            finally
            {
                //a2IaService.CloseOcrChannel();
                a2IaService.Shutdown();
            }
            //System.Threading.Thread.Sleep(35000);
            //foreach (var batch in batchesExtra)
            //{
            //    a2IaService.RegisterBatch(batch);
            //}
            //Debug.WriteLine("Second Pre Process {0} vouchers queued", a2IaService.voucherQueue.Count);

            //a2IaService.ProcessVoucherBatchAsync();

            //Debug.WriteLine("Second Post Process {0} vouchers queued", a2IaService.voucherQueue.Count);

            //await a2IaService.WaitResults();

            //a2IaService.ReleaseResources();

            // Assert
            var resultFile = Path.Combine(ResultPath, ResultFilename);
            var batchCount = 1;
            foreach (var batch in batches)
            {
                batchCount += batch.Vouchers.Count();
            }
            var results = new string[batchCount];
            var index = 0;
            results[index++] = "Voucher Id, Amount, Score";
            foreach (var batch in batches)
            {
                foreach (var voucher in batch.Vouchers)
                {
                    results[index++] = string.Format("{0}, {1}, {2}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
                }
            }
            File.WriteAllLines(resultFile, results);
            a2IaService.Shutdown();
            a2IaService.BatchComplete -= BatchCompleteHandler;
        }

        [TestMethod]
        public async Task A2iABatchPoolService_Test_Async_Upscale_Binarize_Images_200dpi_jpg_With_Open_Connection()
        {
            // Arrange
            const int jobId = 5830013;
            batches = new List<OcrBatch>();
            batchesExtra = new List<OcrBatch>();
            var VouchersPath = Path.Combine(VouchersBasePath, dpi_200_jpg);
            var directory = Path.Combine(VouchersPath, Checks, "ImageProcessed");
            var files = Directory.EnumerateFiles(directory);
            foreach (var file in files)
            {
                File.Delete(file);
            }

            var batch1 = new OcrBatch { JobIdentifier = jobId.ToString(), Vouchers = new List<OcrVoucher>() };
            GetAllTestVouchers(batch1, Path.Combine(VouchersPath, Checks), 0, 0, "jpg");
            batches.Add(batch1);

            configuration.MaxProcessorCount = 2;
            configuration.FileType = "TIF";
            configuration.StickyChannelTimeout = 0;
            var a2IaService = new A2iAStickyConnectionService(engine1, configuration);
            try
            {
                a2IaService.Initialise();
                a2IaService.BatchComplete += BatchCompleteHandler;

                // Act
                Debug.WriteLine("Act");
                foreach (var batch in batches)
                {
                    a2IaService.ProcessBatch(batch);
                }
                Debug.WriteLine(string.Format("timer {0}", DateTime.Now.ToString("hh:mm:ss.fff")));

                //a2IaService.CloseOcrChannel();
                //foreach (var batch in batchesExtra)
                //{
                //    a2IaService.ProcessBatch(batch);
                //}

                Thread.Sleep(configuration.StickyChannelTimeout + 5000);
            }
            finally
            {
                a2IaService.Shutdown();
            }

            // Assert
            var resultFile = Path.Combine(ResultPath, ResultFilename);
            var batchCount = 1;
            foreach (var batch in batches)
            {
                batchCount += batch.Vouchers.Count();
            }
            var results = new string[batchCount];
            var index = 0;
            results[index++] = "Voucher Id, Amount, Score";
            foreach (var batch in batches)
            {
                foreach (var voucher in batch.Vouchers)
                {
                    results[index++] = string.Format("{0}, {1}, {2}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
                }
            }
            File.WriteAllLines(resultFile, results);
            a2IaService.Shutdown();
            a2IaService.BatchComplete -= BatchCompleteHandler;
        }

        [TestMethod]
        public async Task A2iABatchPoolService_Test_Async_Upscale_Binarize_Images_200dpi_tif_With_Open_Connection()
        {
            // Arrange
            const int jobId = 5830013;
            batches = new List<OcrBatch>();
            batchesExtra = new List<OcrBatch>();
            //var directory = Path.Combine(VouchersPath, Checks, "ImageProcessed");
            //var files = Directory.EnumerateFiles(directory);
            //foreach (var file in files)
            //{
            //    File.Delete(file);
            //}

            var batch1 = new OcrBatch { JobIdentifier = jobId.ToString(), Vouchers = new List<OcrVoucher>() };
            //GetAllTestVouchers(batch1, Path.Combine(VouchersPath, Checks));
            const string VouchersPath = @"C:\Sandbox";
            const string Output = @"Output\";
            GetAllTestVouchers(batch1, Path.Combine(VouchersPath, Output));
            batches.Add(batch1);

            configuration.MaxProcessorCount = 2;
            configuration.FileType = "TIF";
            configuration.StickyChannelTimeout = 2000;
            var a2IaService = new A2iAStickyConnectionService(engine1, configuration);
            try
            {
                a2IaService.Initialise();
                a2IaService.BatchComplete += BatchCompleteHandler;

                // Act
                Debug.WriteLine("Act");
                foreach (var batch in batches)
                {
                    a2IaService.ProcessBatch(batch);
                }
                Debug.WriteLine(string.Format("timer {0}", DateTime.Now.ToString("hh:mm:ss.fff")));

                //a2IaService.CloseOcrChannel();
                //foreach (var batch in batchesExtra)
                //{
                //    a2IaService.ProcessBatch(batch);
                //}

                Thread.Sleep(configuration.StickyChannelTimeout + 5000);
            }
            finally
            {
                a2IaService.Shutdown();
            }

            // Assert
            var resultFile = Path.Combine(ResultPath, ResultFilename);
            var batchCount = 1;
            foreach (var batch in batches)
            {
                batchCount += batch.Vouchers.Count();
            }
            var results = new string[batchCount];
            var index = 0;
            results[index++] = "Voucher Id, Amount, Score";
            foreach (var batch in batches)
            {
                foreach (var voucher in batch.Vouchers)
                {
                    results[index++] = string.Format("{0}, {1}, {2}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
                }
            }
            File.WriteAllLines(resultFile, results);
            a2IaService.Shutdown();
            a2IaService.BatchComplete -= BatchCompleteHandler;
        }

        [TestMethod]
        public async Task A2iABatchPoolService_Test_Async_240dpi_tif_With_Open_Connection()
        {
            // Arrange
            const int jobId = 5830013;
            batches = new List<OcrBatch>();
            batchesExtra = new List<OcrBatch>();
            var VouchersPath = Path.Combine(VouchersBasePath, dpi_240_tif);
            //var directory = Path.Combine(VouchersPath, Checks, "ImageProcessed");
            //var files = Directory.EnumerateFiles(directory);
            //foreach (var file in files)
            //{
            //    File.Delete(file);
            //}

            var batch1 = new OcrBatch { JobIdentifier = jobId.ToString(), Vouchers = new List<OcrVoucher>() };
            //GetAllTestVouchers(batch1, Path.Combine(VouchersPath, Checks));
            //VouchersPath = @"C:\Sandbox";
            //const string Output = @"Output\";
            GetAllTestVouchers(batch1, Path.Combine(VouchersPath, Checks), 0, 42);
            batches.Add(batch1);

            configuration.MaxProcessorCount = 2;
            configuration.FileType = "TIF";
            configuration.StickyChannelTimeout = 2000;
            var a2IaService = new A2iAStickyConnectionService(engine1, configuration);
            try
            {
                a2IaService.Initialise();
                a2IaService.BatchComplete += BatchCompleteHandler;

                // Act
                Debug.WriteLine("Act");
                foreach (var batch in batches)
                {
                    a2IaService.ProcessBatch(batch);
                }
                Debug.WriteLine(string.Format("timer {0}", DateTime.Now.ToString("hh:mm:ss.fff")));

                //a2IaService.CloseOcrChannel();
                //foreach (var batch in batchesExtra)
                //{
                //    a2IaService.ProcessBatch(batch);
                //}

                Thread.Sleep(configuration.StickyChannelTimeout + 5000);
            }
            finally
            {
                a2IaService.Shutdown();
            }

            // Assert
            var resultFile = Path.Combine(ResultPath, ResultFilename);
            var batchCount = 1;
            foreach (var batch in batches)
            {
                batchCount += batch.Vouchers.Count();
            }
            var results = new string[batchCount];
            var index = 0;
            results[index++] = "Voucher Id, Amount, Score";
            foreach (var batch in batches)
            {
                foreach (var voucher in batch.Vouchers)
                {
                    results[index++] = string.Format("{0}, {1}, {2}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
                }
            }
            File.WriteAllLines(resultFile, results);
            a2IaService.Shutdown();
            a2IaService.BatchComplete -= BatchCompleteHandler;
        }

        [TestMethod]
        public async Task Upscaled_300dpi_950_Confidence()
        {
            // Arrange
            const int jobId = 5830013;
            batches = new List<OcrBatch>();
            batchesExtra = new List<OcrBatch>();
            var VouchersPath = Path.Combine(VouchersBasePath, dpi_300_upscaled_950);

            var batch1 = new OcrBatch { JobIdentifier = jobId.ToString(), Vouchers = new List<OcrVoucher>() };
            GetAllTestVouchers(batch1, Path.Combine(VouchersPath, Checks));
            batches.Add(batch1);

            configuration.MaxProcessorCount = 2;
            configuration.FileType = "TIF";
            configuration.StickyChannelTimeout = 200;
            var a2IaService = new A2iAStickyConnectionService(engine1, configuration);
            try
            {
                a2IaService.Initialise();
                a2IaService.BatchComplete += BatchCompleteHandler;

                // Act
                foreach (var batch in batches)
                {
                    a2IaService.ProcessBatch(batch);
                }
                Thread.Sleep(configuration.StickyChannelTimeout + 5000);
            }
            finally
            {
                a2IaService.Shutdown();
            }

            // Assert
            var resultFile = Path.Combine(ResultPath, ResultFilename);
            var batchCount = 1;
            foreach (var batch in batches)
            {
                batchCount += batch.Vouchers.Count();
            }
            var results = new string[batchCount];
            var index = 0;
            results[index++] = "Voucher Id, Amount, Score";
            foreach (var batch in batches)
            {
                foreach (var voucher in batch.Vouchers)
                {
                    results[index++] = string.Format("{0}, {1}, {2}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
                }
            }
            File.WriteAllLines(resultFile, results);
            a2IaService.Shutdown();
            a2IaService.BatchComplete -= BatchCompleteHandler;
        }

        [TestMethod]
        public async Task Upscaled_300dpi_950_Confidence_1()
        {
            // Arrange
            const int jobId = 5830013;
            batches = new List<OcrBatch>();
            batchesExtra = new List<OcrBatch>();
            var VouchersPath = Path.Combine(VouchersBasePath, dpi_300_upscaled_950_1);

            var batch1 = new OcrBatch { JobIdentifier = jobId.ToString(), Vouchers = new List<OcrVoucher>() };
            GetAllTestVouchers(batch1, Path.Combine(VouchersPath, Checks));
            batches.Add(batch1);

            configuration.MaxProcessorCount = 2;
            configuration.FileType = "TIF";
            configuration.StickyChannelTimeout = 200;
            var a2IaService = new A2iAStickyConnectionService(engine1, configuration);
            try
            {
                a2IaService.Initialise();
                a2IaService.BatchComplete += BatchCompleteHandler;

                // Act
                foreach (var batch in batches)
                {
                    a2IaService.ProcessBatch(batch);
                }
                Thread.Sleep(configuration.StickyChannelTimeout + 5000);
            }
            finally
            {
                a2IaService.Shutdown();
            }

            // Assert
            var resultFile = Path.Combine(ResultPath, ResultFilename);
            var batchCount = 1;
            foreach (var batch in batches)
            {
                batchCount += batch.Vouchers.Count();
            }
            var results = new string[batchCount];
            var index = 0;
            results[index++] = "Voucher Id, Amount, Score";
            foreach (var batch in batches)
            {
                foreach (var voucher in batch.Vouchers)
                {
                    results[index++] = string.Format("{0}, {1}, {2}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
                }
            }
            File.WriteAllLines(resultFile, results);
            a2IaService.Shutdown();
            a2IaService.BatchComplete -= BatchCompleteHandler;
        }

        [TestMethod]
        public async Task Upscaled_300dpi_950_Confidence_2()
        {
            // Arrange
            const int jobId = 5830013;
            batches = new List<OcrBatch>();
            batchesExtra = new List<OcrBatch>();
            var VouchersPath = Path.Combine(VouchersBasePath, dpi_300_upscaled_950_2);

            var batch1 = new OcrBatch { JobIdentifier = jobId.ToString(), Vouchers = new List<OcrVoucher>() };
            GetAllTestVouchers(batch1, Path.Combine(VouchersPath, Checks));
            batches.Add(batch1);

            configuration.MaxProcessorCount = 2;
            configuration.FileType = "TIF";
            configuration.StickyChannelTimeout = 200;
            var a2IaService = new A2iAStickyConnectionService(engine1, configuration);
            try
            {
                a2IaService.Initialise();
                a2IaService.BatchComplete += BatchCompleteHandler;

                // Act
                foreach (var batch in batches)
                {
                    a2IaService.ProcessBatch(batch);
                }
                Thread.Sleep(configuration.StickyChannelTimeout + 5000);
            }
            finally
            {
                a2IaService.Shutdown();
            }

            // Assert
            var resultFile = Path.Combine(ResultPath, ResultFilename);
            var batchCount = 1;
            foreach (var batch in batches)
            {
                batchCount += batch.Vouchers.Count();
            }
            var results = new string[batchCount];
            var index = 0;
            results[index++] = "Voucher Id, Amount, Score";
            foreach (var batch in batches)
            {
                foreach (var voucher in batch.Vouchers)
                {
                    results[index++] = string.Format("{0}, {1}, {2}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
                }
            }
            File.WriteAllLines(resultFile, results);
            a2IaService.Shutdown();
            a2IaService.BatchComplete -= BatchCompleteHandler;
        }

        [TestMethod]
        public async Task Upscaled_300dpi_950_Confidence_3()
        {
            // Arrange
            const int jobId = 5830013;
            batches = new List<OcrBatch>();
            batchesExtra = new List<OcrBatch>();
            var VouchersPath = Path.Combine(VouchersBasePath, dpi_300_upscaled_950_3);

            var batch1 = new OcrBatch { JobIdentifier = jobId.ToString(), Vouchers = new List<OcrVoucher>() };
            GetAllTestVouchers(batch1, Path.Combine(VouchersPath, Checks));
            batches.Add(batch1);

            configuration.MaxProcessorCount = 2;
            configuration.FileType = "TIF";
            configuration.StickyChannelTimeout = 200;
            var a2IaService = new A2iAStickyConnectionService(engine1, configuration);
            try
            {
                a2IaService.Initialise();
                a2IaService.BatchComplete += BatchCompleteHandler;

                // Act
                foreach (var batch in batches)
                {
                    a2IaService.ProcessBatch(batch);
                }
                Thread.Sleep(configuration.StickyChannelTimeout + 5000);
            }
            finally
            {
                a2IaService.Shutdown();
            }

            // Assert
            var resultFile = Path.Combine(ResultPath, ResultFilename);
            var batchCount = 1;
            foreach (var batch in batches)
            {
                batchCount += batch.Vouchers.Count();
            }
            var results = new string[batchCount];
            var index = 0;
            results[index++] = "Voucher Id, Amount, Score";
            foreach (var batch in batches)
            {
                foreach (var voucher in batch.Vouchers)
                {
                    results[index++] = string.Format("{0}, {1}, {2}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
                }
            }
            File.WriteAllLines(resultFile, results);
            a2IaService.Shutdown();
            a2IaService.BatchComplete -= BatchCompleteHandler;
        }

        [TestMethod]
        public async Task Upscaled_300dpi_950_Confidence_4()
        {
            // Arrange
            const int jobId = 5830013;
            batches = new List<OcrBatch>();
            batchesExtra = new List<OcrBatch>();
            var VouchersPath = Path.Combine(VouchersBasePath, dpi_300_upscaled_950_4);

            var batch1 = new OcrBatch { JobIdentifier = jobId.ToString(), Vouchers = new List<OcrVoucher>() };
            GetAllTestVouchers(batch1, Path.Combine(VouchersPath, Checks));
            batches.Add(batch1);

            configuration.MaxProcessorCount = 2;
            configuration.FileType = "TIF";
            configuration.StickyChannelTimeout = 200;
            var a2IaService = new A2iAStickyConnectionService(engine1, configuration);
            try
            {
                a2IaService.Initialise();
                a2IaService.BatchComplete += BatchCompleteHandler;

                // Act
                foreach (var batch in batches)
                {
                    a2IaService.ProcessBatch(batch);
                }
                Thread.Sleep(configuration.StickyChannelTimeout + 5000);
            }
            finally
            {
                a2IaService.Shutdown();
            }

            // Assert
            var resultFile = Path.Combine(ResultPath, ResultFilename);
            var batchCount = 1;
            foreach (var batch in batches)
            {
                batchCount += batch.Vouchers.Count();
            }
            var results = new string[batchCount];
            var index = 0;
            results[index++] = "Voucher Id, Amount, Score";
            foreach (var batch in batches)
            {
                foreach (var voucher in batch.Vouchers)
                {
                    results[index++] = string.Format("{0}, {1}, {2}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
                }
            }
            File.WriteAllLines(resultFile, results);
            a2IaService.Shutdown();
            a2IaService.BatchComplete -= BatchCompleteHandler;
        }

        [TestMethod]
        public void Execute_Wrapper_With_Two_Documents_In_Table()
        {
            // Arrange
            var jobId = 5830013;
            batches = new List<OcrBatch>();
            const string VouchersPath = VouchersBasePath;
            var voucherCount = 0;
            const int voucherIncrement = 100;
            for (int i = 0; i < 2; i++)
            {
                var batch = new OcrBatch { JobIdentifier = jobId++.ToString(), Vouchers = new List<OcrVoucher>() };
                GetAllTestVouchers(batch, Path.Combine(VouchersPath, Checks), voucherCount,
                    voucherCount + voucherIncrement);
                voucherCount += voucherIncrement;
                batches.Add(batch);
            }
            configuration.MaxProcessorCount = 2;
            configuration.FileType = "TIF";
            configuration.StickyChannelTimeout = 200;

            var a2IaService1 = new A2iAStickyConnectionService(engine1, configuration);
            a2IaService1.Initialise();
            a2IaService1.BatchComplete += BatchCompleteHandler;

            // Act
            a2IaService1.ProcessBatch(batches[0]);
            a2IaService1.ProcessBatch(batches[1]);
            Thread.Sleep(configuration.StickyChannelTimeout + 5000);
            a2IaService1.Shutdown();

            // Assert
            // Results in BatchCompleteHandler
        }

        [TestMethod]
        public async Task Execute_Two_Instances_Of_Wrapper()
        {
            // Arrange
            var jobId = 5830013;
            batches = new List<OcrBatch>();
            const string VouchersPath = VouchersBasePath;
            var voucherCount = 0;
            const int voucherIncrement = 100;
            for (int i = 0; i < 2; i++)
            {
                var batch = new OcrBatch { JobIdentifier = jobId++.ToString(), Vouchers = new List<OcrVoucher>() };
                GetAllTestVouchers(batch, Path.Combine(VouchersPath, Checks), voucherCount, voucherCount + voucherIncrement);
                voucherCount += voucherIncrement;
                batches.Add(batch);
            }
            configuration.MaxProcessorCount = 2;
            configuration.FileType = "TIF";
            configuration.StickyChannelTimeout = 200;

            var a2IaService1 = new A2iAStickyConnectionService(engine1, configuration);
            //var a2IaService2 = new A2iAStickyConnectionService(engine1, configuration);
            try
            {
                a2IaService1.Initialise();
                //a2IaService2.Initialise();
                a2IaService1.BatchComplete += BatchCompleteHandler;
                //a2IaService2.BatchComplete += BatchCompleteHandler;

                // Act
                object lockObject = new object();
                lock (lockObject)
                {
                    Parallel.Invoke(() => a2IaService1.ProcessBatch(batches[0]),
                        () => a2IaService1.ProcessBatch(batches[1]));
                }
                //await a2IaService1.ProcessBatchTask(batches[0], false);
                //await a2IaService2.ProcessBatchTask(batches[1], false);
                Thread.Sleep(configuration.StickyChannelTimeout + 5000);
            }
            finally
            {
                a2IaService1.Shutdown();
                //a2IaService2.Shutdown();
            }

            // Assert
            foreach (var batch in batches)
            {
                WriteResults(batch.Vouchers);
            }

        }

        [TestMethod]
        public async Task Collect_Images_With_Confidence_Greater_Than_950_240dpi_tif()
        {
            // Arrange
            const int jobId = 5830013;
            batches = new List<OcrBatch>();
            batchesExtra = new List<OcrBatch>();
            var VouchersPath = Path.Combine(VouchersBasePath, dpi_240_tif);

            var batch1 = new OcrBatch { JobIdentifier = jobId.ToString(), Vouchers = new List<OcrVoucher>() };
            GetAllTestVouchers(batch1, Path.Combine(VouchersPath, Checks), 0, 200);
            batches.Add(batch1);

            configuration.MaxProcessorCount = 2;
            configuration.FileType = "TIF";
            configuration.StickyChannelTimeout = 200;
            var a2IaService = new A2iAStickyConnectionService(engine1, configuration);
            try
            {
                a2IaService.Initialise();
                a2IaService.BatchComplete += BatchCompleteHandler950;

                // Act
                foreach (var batch in batches)
                {
                    a2IaService.ProcessBatch(batch);
                }
                Thread.Sleep(configuration.StickyChannelTimeout + 5000);
            }
            finally
            {
                a2IaService.Shutdown();
            }

            // Assert
            var resultFile = Path.Combine(ResultPath, ResultFilename);
            var batchCount = 1;
            foreach (var batch in batches)
            {
                batchCount += batch.Vouchers.Count();
            }
            var results = new string[batchCount];
            var index = 0;
            results[index++] = "Voucher Id, Amount, Score";
            foreach (var batch in batches)
            {
                foreach (var voucher in batch.Vouchers)
                {
                    results[index++] = string.Format("{0}, {1}, {2}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
                }
            }
            File.WriteAllLines(resultFile, results);
            a2IaService.Shutdown();
            a2IaService.BatchComplete -= BatchCompleteHandler950;
        }

        private void BatchCompleteHandler(object sender, string batchId)
        {
            var batch = batchesExtra.Union(batches).FirstOrDefault(b => b.JobIdentifier == batchId);
            Debug.WriteLine(string.Format("Batch {0}", batchId));
            if (batch != null) WriteResults(batch.Vouchers);
        }

        private void BatchCompleteHandler950(object sender, string batchId)
        {
            var batch = batchesExtra.Union(batches).FirstOrDefault(b => b.JobIdentifier == batchId);
            Debug.WriteLine(string.Format("Batch {0}", batchId));
            if (batch != null) WriteResultsExt(batch.Vouchers, 950, true);
        }

        private void WriteResults(IList<OcrVoucher> vouchers, int confidence = 800, bool showOnlySubset = false)
        {
            var subset = vouchers.Where(v => int.Parse(v.AmountResult.Score) < confidence).ToList();
            Debug.WriteLine("{0}/{2} vouchers with confidence less than {1}", subset.Count(), confidence, vouchers.Count());
            if (!showOnlySubset) subset = (List<OcrVoucher>) vouchers;
            foreach (var voucher in subset)
            {
                Debug.WriteLine("v {0,-15}\tamt {1,-25}\tscore {2,-15}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
            }
        }

        private void WriteResultsExt(IList<OcrVoucher> vouchers, int confidence = 800, bool showOnlySubset = false)
        {
            var subset = vouchers.Where(v => int.Parse(v.AmountResult.Score) >= confidence).ToList();
            Debug.WriteLine("{0}/{2} vouchers with confidence greater than or equal {1}", subset.Count(), confidence, vouchers.Count());
            if (!showOnlySubset) subset = (List<OcrVoucher>) vouchers;
            foreach (var voucher in subset)
            {
                Debug.WriteLine("v {0,-15}\tamt {1,-25}\tscore {2,-15}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
            }
        }

        [TestMethod]
        public void A2iAOcrProcessingService_Test_500_Images_100dpi_Jpg()
        {
            // Arrange
            var batch1 = new OcrBatch
            {
                JobIdentifier = "5830013",
                Vouchers = new List<OcrVoucher>()
            };

            var VouchersPath = Path.Combine(VouchersBasePath, dpi_100_jpg);
            GetAllTestVouchers(batch1, Path.Combine(VouchersPath, Checks), 0, 500, "jpg");

            configuration.FileType = "JPG";
            var a2IaService = new A2iACombinedTableService(engine1, configuration);
            a2IaService.Initialise();

            // Act
            a2IaService.ProcessBatch(batch1);

            // Assert
            const int confidence = 800;
            var subset = batch1.Vouchers.Where(v => int.Parse(v.AmountResult.Score) < confidence);
            Debug.WriteLine("{0}/{2} vouchers with confidence less than {1}", subset.Count(), confidence, batch1.Vouchers.Count());
            subset = batch1.Vouchers;
            foreach (var voucher in subset)
            {
                Debug.WriteLine("v {0,-15}\tamt {1,-25}\tscore {2,-15}\tmicr {3,-25}\tscore {4,-15}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score, voucher.CodelineResult.Result, voucher.CodelineResult.Score);
            }
            //foreach (var voucher in batch1.Vouchers)
            //{
            //    Debug.WriteLine("v {0,-15}\tamt {1,-25}\tscore {2,-15}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
            //}
            var resultFile = Path.Combine(ResultPath, string.Format("{0} 500 {1}", dpi_100_jpg, ResultFilename));
            var results = new string[batch1.Vouchers.Count() + 1];
            var index = 0;
            results[index++] = "Voucher Id, Amount, Score";
            foreach (var voucher in batch1.Vouchers)
            {
                results[index++] = string.Format("{0}, {1}, {2}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
            }
            File.WriteAllLines(resultFile, results);
            a2IaService.Shutdown();
        }

        [TestMethod]
        public void A2iAOcrProcessingService_Test_Images_100dpi_Jpg()
        {
            // Arrange
            var batch1 = new OcrBatch
            {
                JobIdentifier = "5830013",
                Vouchers = new List<OcrVoucher>()
            };

            var VouchersPath = Path.Combine(VouchersBasePath, dpi_100_jpg);
            GetAllTestVouchers(batch1, Path.Combine(VouchersPath, Checks), 0, 0, "jpg");

            configuration.FileType = "JPG";
            var a2IaService = new A2iACombinedTableService(engine1, configuration);
            a2IaService.Initialise();

            // Act
            a2IaService.ProcessBatch(batch1);

            // Assert
            const int confidence = 800;
            var subset = batch1.Vouchers.Where(v => int.Parse(v.AmountResult.Score) < confidence);
            Debug.WriteLine("{0}/{2} vouchers with confidence less than {1}", subset.Count(), confidence, batch1.Vouchers.Count());
            subset = batch1.Vouchers;
            foreach (var voucher in subset)
            {
                Debug.WriteLine("v {0,-15}\tamt {1,-25}\tscore {2,-15}\tmicr {3,-25}\tscore {4,-15}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score, voucher.CodelineResult.Result, voucher.CodelineResult.Score);
            }
            var resultFile = Path.Combine(ResultPath, string.Format("{0} {1}", dpi_100_jpg, ResultFilename));
            var results = new string[batch1.Vouchers.Count() + 1];
            var index = 0;
            results[index++] = "Voucher Id, Amount, Score";
            foreach (var voucher in batch1.Vouchers)
            {
                results[index++] = string.Format("{0}, {1}, {2}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
            }
            File.WriteAllLines(resultFile, results);
            a2IaService.Shutdown();
        }

        [TestMethod]
        public void A2iAOcrProcessingService_Test_Images_200dpi_Tiff()
        {
            // Arrange
            var batch1 = new OcrBatch
            {
                JobIdentifier = "5830013",
                Vouchers = new List<OcrVoucher>()
            };

            var VouchersPath = Path.Combine(VouchersBasePath, dpi_200_tif);
            GetAllTestVouchers(batch1, Path.Combine(VouchersPath, Checks));

            configuration.FileType = "TIF";
            var a2IaService = new A2iACombinedTableService(engine1, configuration);
            a2IaService.Initialise();

            // Act
            a2IaService.ProcessBatch(batch1);

            // Assert
            const int confidence = 800;
            var subset = batch1.Vouchers.Where(v => int.Parse(v.AmountResult.Score) < confidence);
            Debug.WriteLine("{0}/{2} vouchers with confidence less than {1}", subset.Count(), confidence, batch1.Vouchers.Count());
            subset = batch1.Vouchers;
            foreach (var voucher in subset)
            {
                Debug.WriteLine("v {0,-15}\tamt {1,-25}\tscore {2,-15}\tmicr {3,-25}\tscore {4,-15}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score, voucher.CodelineResult.Result, voucher.CodelineResult.Score);
            }
            var resultFile = Path.Combine(ResultPath, string.Format("{0} {1}", dpi_200_tif, ResultFilename));
            var results = new string[batch1.Vouchers.Count() + 1];
            var index = 0;
            results[index++] = "Voucher Id, Amount, Score";
            foreach (var voucher in batch1.Vouchers)
            {
                results[index++] = string.Format("{0}, {1}, {2}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
            }
            File.WriteAllLines(resultFile, results);
            a2IaService.Shutdown();
        }

        [TestMethod]
        public void A2iAOcrProcessingService_Test_Images_200dpi_Greyscaled_Tiff()
        {
            // Arrange
            var batch1 = new OcrBatch
            {
                JobIdentifier = "5830013",
                Vouchers = new List<OcrVoucher>()
            };

            var VouchersPath = Path.Combine(VouchersBasePath, dpi_200_greyscaled_tif);
            GetAllTestVouchers(batch1, Path.Combine(VouchersPath, Checks));

            configuration.FileType = "TIF";
            configuration.MaxProcessorCount = 3;
            var a2IaService = new A2iACombinedTableService(engine1, configuration);
            a2IaService.Initialise();

            // Act
            a2IaService.ProcessBatch(batch1);

            // Assert
            const int confidence = 800;
            var subset = batch1.Vouchers.Where(v => int.Parse(v.AmountResult.Score) < confidence);
            Debug.WriteLine("{0}/{2} vouchers with confidence less than {1}", subset.Count(), confidence, batch1.Vouchers.Count());
            subset = batch1.Vouchers;
            foreach (var voucher in subset)
            {
                Debug.WriteLine("v {0,-15}\tamt {1,-25}\tscore {2,-15}\tmicr {3,-25}\tscore {4,-15}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score, voucher.CodelineResult.Result, voucher.CodelineResult.Score);
            }
            var resultFile = Path.Combine(ResultPath, string.Format("{0} {1}", dpi_200_greyscaled_tif, ResultFilename));
            var results = new string[batch1.Vouchers.Count() + 1];
            var index = 0;
            results[index++] = "Voucher Id, Amount, Score";
            foreach (var voucher in batch1.Vouchers)
            {
                results[index++] = string.Format("{0}, {1}, {2}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
            }
            File.WriteAllLines(resultFile, results);
            a2IaService.Shutdown();
        }

        [TestMethod]
        public void A2iAOcrProcessingService_Test_Images_300dpi_Upscaled_Tiff()
        {
            // Arrange
            var batch1 = new OcrBatch
            {
                JobIdentifier = "5830013",
                Vouchers = new List<OcrVoucher>()
            };

            var VouchersPath = Path.Combine(VouchersBasePath, dpi_300_upscaled_tif);
            GetAllTestVouchers(batch1, Path.Combine(VouchersPath, Checks));

            configuration.FileType = "TIF";
            var a2IaService = new A2iACombinedTableService(engine1, configuration);
            a2IaService.Initialise();

            // Act
            a2IaService.ProcessBatch(batch1);

            // Assert
            const int confidence = 800;
            var subset = batch1.Vouchers.Where(v => int.Parse(v.AmountResult.Score) < confidence);
            Debug.WriteLine("{0}/{2} vouchers with confidence less than {1}", subset.Count(), confidence, batch1.Vouchers.Count());
            subset = batch1.Vouchers;
            foreach (var voucher in subset)
            {
                Debug.WriteLine("v {0,-15}\tamt {1,-25}\tscore {2,-15}\tmicr {3,-25}\tscore {4,-15}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score, voucher.CodelineResult.Result, voucher.CodelineResult.Score);
            }
            var resultFile = Path.Combine(ResultPath, string.Format("{0} {1}", dpi_300_upscaled_tif, ResultFilename));
            var results = new string[batch1.Vouchers.Count() + 1];
            var index = 0;
            results[index++] = "Voucher Id, Amount, Score";
            foreach (var voucher in batch1.Vouchers)
            {
                results[index++] = string.Format("{0}, {1}, {2}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
            }
            File.WriteAllLines(resultFile, results);
            a2IaService.Shutdown();
        }

        [TestMethod]
        public async Task A2iAOcrProcessingService_Test_Async_Images_400dpi_Upscaled_Tiff()
        {
            // Arrange
            var batch1 = new OcrBatch
            {
                JobIdentifier = "5830013",
                Vouchers = new List<OcrVoucher>()
            };

            var VouchersPath = Path.Combine(VouchersBasePath, dpi_400_upscaled_tif);
            GetAllTestVouchers(batch1, Path.Combine(VouchersPath, Checks));

            configuration.FileType = "TIF";
            configuration.MaxProcessorCount = 3;
            //configuration.MaxProcessorCount = 1;
            var a2IaService = new A2iACombinedTableService(engine1, configuration);
            a2IaService.Initialise();

            // Act
            a2IaService.ProcessBatch(batch1);

            // Assert
            const int confidence = 800;
            var subset = batch1.Vouchers.Where(v => int.Parse(v.AmountResult.Score) < confidence);
            Debug.WriteLine("{0}/{2} vouchers with confidence less than {1}", subset.Count(), confidence, batch1.Vouchers.Count());
            subset = batch1.Vouchers;
            foreach (var voucher in subset)
            {
                Debug.WriteLine("v {0,-15}\tamt {1,-25}\tscore {2,-15}\tmicr {3,-25}\tscore {4,-15}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score, voucher.CodelineResult.Result, voucher.CodelineResult.Score);
            }
            var resultFile = Path.Combine(ResultPath, string.Format("{0} {1}", dpi_400_upscaled_tif, ResultFilename));
            var results = new string[batch1.Vouchers.Count() + 1];
            var index = 0;
            results[index++] = "Voucher Id, Amount, Score";
            foreach (var voucher in batch1.Vouchers)
            {
                results[index++] = string.Format("{0}, {1}, {2}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
            }
            File.WriteAllLines(resultFile, results);
            a2IaService.Shutdown();
        }

        [TestMethod]
        public void A2iAOcrProcessingService_Test_Credit_Slips()
        {
            var batch1 = new OcrBatch
            {
                JobIdentifier = "5830015",
                Vouchers = new List<OcrVoucher>()
            };

            var VouchersPath = Path.Combine(VouchersBasePath, mixed_batch);
            GetAllTestVouchers(batch1, Path.Combine(VouchersPath, Checks));

            configuration.MaxProcessorCount = 2;
            var a2IaService = new A2iAStickyConnectionService(engine1, configuration);
            //a2IaService.SetParameters(ParamPath, TableFile, LoadMethod.File);
            a2IaService.Initialise();
            a2IaService.ProcessBatch(batch1);

            const int confidence = 800;
            var subset = batch1.Vouchers.Where(v => int.Parse(v.AmountResult.Score) < confidence).ToList();
            Debug.WriteLine("{0}/{2} vouchers with confidence less than {1}", subset.Count(), confidence, batch1.Vouchers.Count());
            foreach (var voucher in subset)
            {
                Debug.WriteLine("v {0,-15}\tamt {1,-25}\tscore {2,-15}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score);
            }

            a2IaService.Shutdown();
        }

        public void GetAllTestVouchers(OcrBatch batch, string path, int startIndex = 0, int endIndex = 0, string fileType = "tif", string filter = "FRONT")
        {
            var directoryInfo = new DirectoryInfo(path);
            var random = new Random();
            var files = directoryInfo.GetFiles(string.Format("*{0}*.{1}", filter, fileType));
            //foreach (var filename in files)
            if (startIndex >= files.Count()) throw new IndexOutOfRangeException("Start index out of bounds");
            if (endIndex == 0 || endIndex > files.Count()) endIndex = files.Count();
            for (int i = startIndex; i < endIndex; i++)
            {
                var voucherId = files[i].Name.ToUpper().Replace(string.Format(".{0}", fileType.ToUpper()), "");
                var voucherFilename = files[i].Name;
                var voucherType = random.Next(10) > 2 ? VoucherType.Debit : VoucherType.Credit;
                batch.Vouchers.Add(new OcrVoucher
                {
                    Id = voucherId,
                    VoucherType = voucherType,
                    ImagePath = string.Format("{0}{1}", path, voucherFilename)
                });
            }
        }
    }
}
