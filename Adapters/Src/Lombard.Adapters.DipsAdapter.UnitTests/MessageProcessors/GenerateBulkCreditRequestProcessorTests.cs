using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using Autofac;
using Castle.Core.Internal;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using Lombard.Adapters.Data;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.Data.Transaction;
using Lombard.Adapters.DipsAdapter.Configuration;
using Lombard.Adapters.DipsAdapter.Helpers;
using Lombard.Adapters.DipsAdapter.Helpers.Interfaces;
using Lombard.Adapters.DipsAdapter.MessageProcessors;
using Lombard.Adapters.DipsAdapter.Messages;
using Serilog;

namespace Lombard.Adapters.DipsAdapter.UnitTests.MessageProcessors
{
    [TestClass]
    public class GenerateBulkCreditRequestProcessorTests
    {
        private Mock<ILifetimeScope> lifetimeScope;
        private Mock<IDipsDbContext> dipsDbContext;
        private Mock<IMapper<VoucherInformation[], DipsQueue>> dipsQueueMapper;
        private Mock<IMapper<VoucherInformation[], IEnumerable<DipsNabChq>>> dipsVoucherMapper;
        private Mock<IMapper<VoucherInformation[], IEnumerable<DipsDbIndex>>> dipsDbIndexMapper;
        private Mock<IDipsDbContextTransaction> transaction;
        private Mock<ILogger> logger;
        private Mock<IAdapterConfiguration> adapterConfiguration;
        private InMemoryDbSet<DipsQueue> dipsQueueSet;
        private InMemoryDbSet<DipsNabChq> dipsNabChqSet;
        private InMemoryDbSet<DipsDbIndex> dipsDbIndexSet;
        private Mock<IScannedBatchHelper> scannedBatchHelper;

        private string batchNumberFormat = "702{0}";
        private VoucherInformation[] sampleVoucherInformation;
        
        private GenerateBatchBulkCreditRequest sampleGenerateBatchBulkCreditRequest;
        private DipsQueue sampleDipsQueue;
        private IEnumerable<DipsNabChq> sampleDipsNabChqs;
        private IEnumerable<DipsDbIndex> sampleDipsDbIndexes;


        [TestInitialize]
        public void TestInitialize()
        {
            lifetimeScope = new Mock<ILifetimeScope>();
            dipsDbContext = new Mock<IDipsDbContext>();
            dipsQueueMapper = new Mock<IMapper<VoucherInformation[], DipsQueue>>();
            dipsVoucherMapper = new Mock<IMapper<VoucherInformation[], IEnumerable<DipsNabChq>>>();
            dipsDbIndexMapper = new Mock<IMapper<VoucherInformation[], IEnumerable<DipsDbIndex>>>();
            transaction = new Mock<IDipsDbContextTransaction>();
            logger = new Mock<ILogger>();
            adapterConfiguration = new Mock<IAdapterConfiguration>();
            scannedBatchHelper = new Mock<IScannedBatchHelper>();

            Log.Logger = logger.Object;

            dipsQueueSet = new InMemoryDbSet<DipsQueue>(true);
            dipsNabChqSet = new InMemoryDbSet<DipsNabChq>(true);
            dipsDbIndexSet = new InMemoryDbSet<DipsDbIndex>(true);

            InitializeTestData();
        }

        [TestMethod]
        public async Task GenerateBulkCredit_WhenProcessAsync_ThenSaveBatchToDb_AndSaveCommit()
        {
            ExpectAdapterToReturnBatchNumberFormat(batchNumberFormat);

            //Setup Context
            ExpectContextToReturnDipsQueues(dipsQueueSet);
            ExpectContextToReturnDipsNabChqs(dipsNabChqSet);
            ExpectContextToReturnDipsDbIndexes(dipsDbIndexSet);

            //Setup Batch Data
            ExpectQueueMapperToReturnQueue(sampleDipsQueue);
            ExpectVoucherMapperToReturnVouchers(sampleDipsNabChqs);
            ExpectDbIndexMapperToReturnDbIndexes(sampleDipsDbIndexes);

            ExpectContextToCreateTransaction();

            var cancellationToken = new CancellationToken(false);

            var sut = CreateMessageProcessor();
            await sut.ProcessAsync(cancellationToken, Guid.NewGuid().ToString(), string.Empty);

            dipsDbContext.Verify(x => x.SaveChanges());
            transaction.Verify(x => x.Commit());
        }

        [TestMethod]
        public async Task GenerateBulkCredit_WhenProcessAsync_AndGeneralException_ThenRollBackAndLogError()
        {
            ExpectAdapterToReturnBatchNumberFormat(batchNumberFormat);

            //Setup Context
            ExpectContextToReturnDipsQueues(dipsQueueSet);
            ExpectContextToReturnDipsNabChqs(dipsNabChqSet);
            ExpectContextToReturnDipsDbIndexes(dipsDbIndexSet);

            //Setup Batch Data
            ExpectQueueMapperToReturnQueue(sampleDipsQueue);
            ExpectVoucherMapperToReturnVouchers(sampleDipsNabChqs);
            ExpectDbIndexMapperToReturnDbIndexes(sampleDipsDbIndexes);

            ExpectContextToCreateTransaction();

            var cancellationToken = new CancellationToken(false);

            var ex = new Exception();
            dipsDbContext.Setup(x => x.SaveChanges())
                .Throws(ex);

            var sut = CreateMessageProcessor();
            sut.Message = sampleGenerateBatchBulkCreditRequest;
            var jobIdentifier = Guid.NewGuid().ToString();

            try
            {
                await sut.ProcessAsync(cancellationToken, jobIdentifier, string.Empty);
            }
            // ReSharper disable once EmptyGeneralCatchClause 
            catch (Exception)
            {
                //intentional
            }

            transaction.Verify(x => x.Rollback());
            logger.Verify(x => x.Error(ex, It.IsAny<string>(), sampleGenerateBatchBulkCreditRequest, jobIdentifier));
        }

        // Generate Batch Number
        [TestMethod]
        public async Task GenerateBulkCredit_WhenProcessAsync_GenerateBatchNumber_AndUpdateBulkCreditVouchers()
        {
            ExpectAdapterToReturnBatchNumberFormat(batchNumberFormat);
            ExpectScannedBatchHelperToReturnVouchers();

            //Setup Context
            ExpectContextToReturnDipsQueues(dipsQueueSet);
            ExpectContextToReturnDipsNabChqs(dipsNabChqSet);
            ExpectContextToReturnDipsDbIndexes(dipsDbIndexSet);

            //Setup Batch Data
            ExpectQueueMapperToReturnQueue(sampleDipsQueue);
            ExpectVoucherMapperToReturnVouchers(sampleDipsNabChqs);
            ExpectDbIndexMapperToReturnDbIndexes(sampleDipsDbIndexes);

            ExpectContextToCreateTransaction();

            var cancellationToken = new CancellationToken(false);
            
            var sut = CreateMessageProcessor();
            sut.Message = sampleGenerateBatchBulkCreditRequest;
            var jobIdentifier = Guid.NewGuid().ToString();

            try
            {
                await sut.ProcessAsync(cancellationToken, jobIdentifier, string.Empty);
            }
            // ReSharper disable once EmptyGeneralCatchClause 
            catch (Exception)
            {
                //intentional
            }

            Assert.IsTrue(sampleVoucherInformation.All(n => !string.IsNullOrEmpty(n.voucherBatch.scannedBatchNumber)));
        }

        // Generate Batch Number
        [TestMethod]
        public async Task GenerateBulkCredit_WhenProcessAsync_ThenUpdateVouchers_WithMaxDebit()
        {
            ExpectAdapterToReturnBatchNumberFormat(batchNumberFormat);
            ExpectScannedBatchHelperToReturnVouchers();

            //Setup Context
            ExpectContextToReturnDipsQueues(dipsQueueSet);
            ExpectContextToReturnDipsNabChqs(dipsNabChqSet);
            ExpectContextToReturnDipsDbIndexes(dipsDbIndexSet);

            //Setup Batch Data
            ExpectQueueMapperToReturnQueue(sampleDipsQueue);
            ExpectVoucherMapperToReturnVouchers(sampleDipsNabChqs);
            ExpectDbIndexMapperToReturnDbIndexes(sampleDipsDbIndexes);

            ExpectContextToCreateTransaction();

            var cancellationToken = new CancellationToken(false);

            var sut = CreateMessageProcessor();
            sut.Message = sampleGenerateBatchBulkCreditRequest;
            var jobIdentifier = Guid.NewGuid().ToString();

            try
            {
                await sut.ProcessAsync(cancellationToken, jobIdentifier, string.Empty);
            }
            // ReSharper disable once EmptyGeneralCatchClause 
            catch (Exception)
            {
                //intentional
            }

            Assert.IsTrue(sampleDipsNabChqs.All(n => !string.IsNullOrEmpty(n.maxVouchers)));
        }

        private void ExpectScannedBatchHelperToReturnVouchers()
        {
            scannedBatchHelper
                .Setup(x => x.ReadScannedBatch(sampleGenerateBatchBulkCreditRequest, sampleGenerateBatchBulkCreditRequest.jobIdentifier, It.IsAny<DateTime>()))
                .Returns(sampleVoucherInformation);
        }

        private void ExpectAdapterToReturnBatchNumberFormat(string adapterbatchNumberFormat)
        {
            adapterConfiguration
                .Setup(x => x.BatchNumberFormat)
                .Returns(adapterbatchNumberFormat);
        }

        private void ExpectContextToReturnDipsQueues(IDbSet<DipsQueue> queueSet)
        {
            dipsDbContext
                .Setup(x => x.Queues)
                .Returns(queueSet);
        }

        private void ExpectContextToReturnDipsNabChqs(IDbSet<DipsNabChq> voucherSet)
        {
            dipsDbContext
                .Setup(x => x.NabChqPods)
                .Returns(voucherSet);
        }

        private void ExpectContextToReturnDipsDbIndexes(IDbSet<DipsDbIndex> indexSet)
        {
            dipsDbContext
                .Setup(x => x.DbIndexes)
                .Returns(indexSet);
        }

        private void ExpectQueueMapperToReturnQueue(DipsQueue queue)
        {
            dipsQueueMapper
                .Setup(x => x.Map(It.IsAny<VoucherInformation[]>()))
                .Returns(queue);
        }

        private void ExpectVoucherMapperToReturnVouchers(IEnumerable<DipsNabChq> vouchers)
        {
            dipsVoucherMapper
                .Setup(x => x.Map(It.IsAny<VoucherInformation[]>()))
                .Returns(vouchers);
        }

        private void ExpectDbIndexMapperToReturnDbIndexes(IEnumerable<DipsDbIndex> dbIndices)
        {
            dipsDbIndexMapper
                .Setup(x => x.Map(It.IsAny<VoucherInformation[]>()))
                .Returns(dbIndices);
        }

        private GenerateBulkCreditRequestProcessor CreateMessageProcessor()
        {
            return new GenerateBulkCreditRequestProcessor(lifetimeScope.Object, dipsQueueMapper.Object, dipsVoucherMapper.Object, dipsDbIndexMapper.Object, scannedBatchHelper.Object, adapterConfiguration.Object, dipsDbContext.Object)
            {
                Message = sampleGenerateBatchBulkCreditRequest
            };
        }

        private void ExpectContextToCreateTransaction()
        {
            dipsDbContext
                .Setup(x => x.BeginTransaction())
                .Returns(transaction.Object);
        }

        private void InitializeTestData()
        {
            sampleGenerateBatchBulkCreditRequest = new GenerateBatchBulkCreditRequest
            {
                jobIdentifier = "NECL-47887b18-e731-4839-b77a-d48c00003328/NGBC-04d91de6-5051-4a17-ab70-b296bb1c4cc4",
                maxDebitVouchers = 10,
                vouchers = new VoucherGroupCriteria[]
                {
                    new VoucherGroupCriteria
                    {
                        captureBsb = "085384",
                        documentReferenceNumber = "895214001",
                        processingDate = Convert.ToDateTime("2015/10/20")
                    },
                    new VoucherGroupCriteria
                    {
                        captureBsb = "085385",
                        documentReferenceNumber = "895214003",
                        processingDate = Convert.ToDateTime("2015/10/20")
                    }
                }
            };

            sampleDipsQueue = new DipsQueue
            {
                ResponseCompleted = false,
                S_LOCATION = "CodelineCorrection",
                S_LOCK = "0",
                S_SDATE = "01/01/15",
                S_STIME = "12:12:12",
                S_BATCH = "58300013"
            };

            sampleDipsNabChqs = new List<DipsNabChq>
            {
                new DipsNabChq
                {
                    amount = "45.67",
                    acc_num = "256902729",
                    bsb_num = "013812",
                    batch = "58300013",
                    doc_ref_num = "583000026",
                    ser_num = "001193",
                    trancode = "50",
                    tpcRequired = "Y",
                    tpcResult = "F",
                    fxa_unencoded_ECD_return = "1",
                    tpcMixedDepRet = "1",
                    fxa_tpc_suspense_pool_flag = "1",
                    highValueFlag = "1"
                },
                new DipsNabChq
                {
                    amount = "2341.45",
                    acc_num = "814649",
                    bsb_num = "092002",
                    batch = "58300013",
                    doc_ref_num = "583000027",
                    ser_num = "001193",
                    trancode = "50",
                    tpcRequired = "Y",
                    tpcResult = "F",
                    fxa_unencoded_ECD_return = "1",
                    tpcMixedDepRet = "1",
                    fxa_tpc_suspense_pool_flag = "1",
                    highValueFlag = "1"
                }
            };

            sampleDipsDbIndexes = new List<DipsDbIndex>
            {
                new DipsDbIndex
                {
                    BATCH = "58300013",
                    TRACE = "583000026"
                },
                new DipsDbIndex
                {
                    BATCH = "58300013",
                    TRACE = "583000027"
                }
            };

            sampleVoucherInformation = new VoucherInformation[]
            {
                new VoucherInformation()
                {
                    voucherBatch = new VoucherBatch()
                    {
                        scannedBatchNumber = string.Empty
                    }
                },
                new VoucherInformation()
                {
                    voucherBatch = new VoucherBatch()
                    {
                        scannedBatchNumber = string.Empty
                    }
                }
            };
        }
    }
}
