﻿using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data.Entity;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using Autofac;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using Lombard.Adapters.Data;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.Data.Transaction;
using Lombard.Adapters.DipsAdapter.Configuration;
using Lombard.Adapters.DipsAdapter.Helpers.Interfaces;
using Lombard.Adapters.DipsAdapter.MessageProcessors;
using Lombard.Adapters.DipsAdapter.Messages;
using Serilog;

namespace Lombard.Adapters.DipsAdapter.UnitTests.MessageProcessors
{
    [TestClass]
    public class CorrectCodelineRequestProcessorTests
    {
        private Mock<ILifetimeScope> lifetimeScope;
        private Mock<IDipsDbContext> dipsDbContext;
        private Mock<IMapper<CorrectBatchCodelineRequest, DipsQueue>> dipsQueueMapper;
        private Mock<IMapper<CorrectBatchCodelineRequest, IEnumerable<DipsNabChq>>> dipsVoucherMapper;
        private Mock<IMapper<CorrectBatchCodelineRequest, IEnumerable<DipsDbIndex>>> dipsDbIndexMapper;
        private Mock<IDipsDbContextTransaction> transaction;
        private Mock<ILogger> logger;
        private Mock<IImageMergeHelper> imageMergeHelper;
        private Mock<IAdapterConfiguration> adapterConfiguration;
        private InMemoryDbSet<DipsQueue> dipsQueueSet;
        private InMemoryDbSet<DipsNabChq> dipsNabChqSet;
        private InMemoryDbSet<DipsDbIndex> dipsDbIndexSet;

        private CorrectBatchCodelineRequest sampleCorrectBatchCodelineRequest;
        private DipsQueue sampleDipsQueue;
        private IEnumerable<DipsNabChq> sampleDipsNabChqs;
        private IEnumerable<DipsDbIndex> sampleDipsDbIndexes;

        private string drnFormat = "702{0}";

        [TestInitialize]
        public void TestInitialize()
        {
            lifetimeScope = new Mock<ILifetimeScope>();
            dipsDbContext = new Mock<IDipsDbContext>();
            dipsQueueMapper = new Mock<IMapper<CorrectBatchCodelineRequest, DipsQueue>>();
            dipsVoucherMapper = new Mock<IMapper<CorrectBatchCodelineRequest, IEnumerable<DipsNabChq>>>();
            dipsDbIndexMapper = new Mock<IMapper<CorrectBatchCodelineRequest, IEnumerable<DipsDbIndex>>>();
            transaction = new Mock<IDipsDbContextTransaction>();
            logger = new Mock<ILogger>();
            imageMergeHelper = new Mock<IImageMergeHelper>();
            adapterConfiguration = new Mock<IAdapterConfiguration>();

            Log.Logger = logger.Object;

            dipsQueueSet = new InMemoryDbSet<DipsQueue>(true);
            dipsNabChqSet = new InMemoryDbSet<DipsNabChq>(true);
            dipsDbIndexSet = new InMemoryDbSet<DipsDbIndex>(true);
        }

        [TestMethod]
        public async Task WhenProcessAsync_ThenSaveBatchToDb_AndSaveCommit()
        {
            InitializeTestData();

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
        public async Task WhenProcessAsync_AndGeneralException_ThenRollBackAndLogError()
        {
            InitializeTestData();

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
            sut.Message = sampleCorrectBatchCodelineRequest;
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
            logger.Verify(x => x.Error(ex, It.IsAny<string>(), sampleCorrectBatchCodelineRequest, jobIdentifier));
        }

        [TestMethod]
        public async Task WhenProcessAsync_ThenEnsureImages()
        {
            InitializeTestData();

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
            await sut.ProcessAsync(cancellationToken, sampleCorrectBatchCodelineRequest.jobIdentifier, string.Empty);

            imageMergeHelper.Verify(x => x.EnsureMergedImageFilesExistAsync(sampleCorrectBatchCodelineRequest.jobIdentifier, sampleCorrectBatchCodelineRequest.voucherBatch.scannedBatchNumber, sampleCorrectBatchCodelineRequest.voucher[0].processingDate));
        }

        [TestMethod]
        public async Task WhenProcessAsync_ThenPopulateVouchersWithData()
        {
            InitializeTestData();

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
            await sut.ProcessAsync(cancellationToken, sampleCorrectBatchCodelineRequest.jobIdentifier, string.Empty);

            imageMergeHelper.Verify(x => x.PopulateMergedImageInfo(sampleCorrectBatchCodelineRequest.jobIdentifier, sampleCorrectBatchCodelineRequest.voucherBatch.scannedBatchNumber, It.IsAny<IEnumerable<DipsNabChq>>()));
        }

        [TestMethod]
        public async Task CorrectCodeline_WhenLongDRN_ThenUpdateVoucherDrns()
        {
            InitializeLongDrnTestData();

            ExpectAdapterToReturnDrnFormat(drnFormat);

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
            await sut.ProcessAsync(cancellationToken, sampleCorrectBatchCodelineRequest.jobIdentifier, string.Empty);

            Assert.IsTrue(sampleDipsNabChqs.All(n => n.doc_ref_num.Length == 9));
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
                .Setup(x => x.Map(It.IsAny<CorrectBatchCodelineRequest>()))
                .Returns(queue);
        }

        private void ExpectVoucherMapperToReturnVouchers(IEnumerable<DipsNabChq> vouchers)
        {
            dipsVoucherMapper
                .Setup(x => x.Map(It.IsAny<CorrectBatchCodelineRequest>()))
                .Returns(vouchers);
        }

        private void ExpectDbIndexMapperToReturnDbIndexes(IEnumerable<DipsDbIndex> dbIndices)
        {
            dipsDbIndexMapper
                .Setup(x => x.Map(It.IsAny<CorrectBatchCodelineRequest>()))
                .Returns(dbIndices);
        }

        private CorrectCodelineRequestProcessor CreateMessageProcessor()
        {
            return new CorrectCodelineRequestProcessor(lifetimeScope.Object, dipsQueueMapper.Object, dipsVoucherMapper.Object, dipsDbIndexMapper.Object, imageMergeHelper.Object, adapterConfiguration.Object, dipsDbContext.Object)
            {
                Message = sampleCorrectBatchCodelineRequest
            };
        }

        private void ExpectContextToCreateTransaction()
        {
            dipsDbContext
                .Setup(x => x.BeginTransaction())
                .Returns(transaction.Object);
        }

        private void ExpectAdapterToReturnDrnFormat(string adapterdrnFormat)
        {
            adapterConfiguration
                .Setup(x => x.DrnFormat)
                .Returns(adapterdrnFormat);
        }

        private void InitializeTestData()
        {
            sampleCorrectBatchCodelineRequest = new CorrectBatchCodelineRequest
            {
                jobIdentifier = "aaa",

                voucherBatch = new VoucherBatch
                {
                    scannedBatchNumber = "58300013"
                },

                voucher = new[]
                {
                    new CorrectCodelineRequest
                    {
                        auxDom = "001193",
                        bsbNumber = "013812",
                        accountNumber = "256902729",
                        transactionCode = "50",
                        documentReferenceNumber = "583000026",
                        capturedAmount = "45.67"
                    },
                    new CorrectCodelineRequest
                    {
                        auxDom = "001193",
                        bsbNumber = "092002",
                        accountNumber = "814649",
                        transactionCode = "50",
                        documentReferenceNumber = "583000027",
                        capturedAmount = "2341.45"
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
        }

        private void InitializeLongDrnTestData()
        {
            sampleCorrectBatchCodelineRequest = new CorrectBatchCodelineRequest
            {
                jobIdentifier = "NSBDFGSLA",

                voucherBatch = new VoucherBatch
                {
                    scannedBatchNumber = "58300013"
                },

                voucher = new[]
                {
                    new CorrectCodelineRequest
                    {
                        auxDom = "001193",
                        bsbNumber = "013812",
                        accountNumber = "256902729",
                        transactionCode = "50",
                        documentReferenceNumber = "000583000026",
                        capturedAmount = "45.67"
                    },
                    new CorrectCodelineRequest
                    {
                        auxDom = "001193",
                        bsbNumber = "092002",
                        accountNumber = "814649",
                        transactionCode = "50",
                        documentReferenceNumber = "000583000027",
                        capturedAmount = "2341.45"
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
                S_BATCH = "58300013",
                S_TRACE = "000583000027"
            };

            sampleDipsNabChqs = new List<DipsNabChq>
            {
                new DipsNabChq
                {
                    amount = "45.67",
                    acc_num = "256902729",
                    bsb_num = "013812",
                    batch = "58300013",
                    doc_ref_num = "000583000027",
                    trace = "000583000027",
                    S_TRACE = "000583000027",
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
                    doc_ref_num = "000583000028",
                    trace = "000583000028",
                    S_TRACE = "000583000028",
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
                    TRACE = "000583000028"
                },
                new DipsDbIndex
                {
                    BATCH = "58300013",
                    TRACE = "000583000028"
                }
            };
        }
    }
}