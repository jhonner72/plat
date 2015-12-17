﻿using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Threading;
using System.Threading.Tasks;
using Autofac;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using Lombard.Adapters.Data;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.Data.Transaction;
using Lombard.Adapters.DipsAdapter.Helpers.Interfaces;
using Lombard.Adapters.DipsAdapter.MessageProcessors;
using Lombard.Adapters.DipsAdapter.Messages;
using Serilog;

namespace Lombard.Adapters.DipsAdapter.UnitTests.MessageProcessors
{
    [TestClass]
    public class CorrectTransactionRequestProcessorTests
    {
        private Mock<ILifetimeScope> lifetimeScope;
        private Mock<IDipsDbContext> dipsDbContext;
        private Mock<IMapper<CorrectBatchTransactionRequest, DipsQueue>> dipsQueueMapper;
        private Mock<IMapper<CorrectBatchTransactionRequest, IEnumerable<DipsNabChq>>> dipsVoucherMapper;
        private Mock<IMapper<CorrectBatchTransactionRequest, IEnumerable<DipsDbIndex>>> dipsDbIndexMapper;
        private Mock<IDipsDbContextTransaction> transaction;
        private Mock<ILogger> logger;
        private Mock<IImageMergeHelper> imageMergeHelper;

        private InMemoryDbSet<DipsQueue> dipsQueueSet;
        private InMemoryDbSet<DipsNabChq> dipsNabChqSet;
        private InMemoryDbSet<DipsDbIndex> dipsDbIndexSet;

        private CorrectBatchTransactionRequest sampleCorrectBatchTransactionRequest;
        private DipsQueue sampleDipsQueue;
        private IEnumerable<DipsNabChq> sampleDipsNabChqs;
        private IEnumerable<DipsDbIndex> sampleDipsDbIndexes;

        [TestInitialize]
        public void TestInitialize()
        {
            lifetimeScope = new Mock<ILifetimeScope>();
            dipsDbContext = new Mock<IDipsDbContext>();
            dipsQueueMapper = new Mock<IMapper<CorrectBatchTransactionRequest, DipsQueue>>();
            dipsVoucherMapper = new Mock<IMapper<CorrectBatchTransactionRequest, IEnumerable<DipsNabChq>>>();
            dipsDbIndexMapper = new Mock<IMapper<CorrectBatchTransactionRequest, IEnumerable<DipsDbIndex>>>();
            transaction = new Mock<IDipsDbContextTransaction>();
            logger = new Mock<ILogger>();
            imageMergeHelper = new Mock<IImageMergeHelper>();

            Log.Logger = logger.Object;

            dipsQueueSet = new InMemoryDbSet<DipsQueue>(true);
            dipsNabChqSet = new InMemoryDbSet<DipsNabChq>(true);
            dipsDbIndexSet = new InMemoryDbSet<DipsDbIndex>(true);

            sampleCorrectBatchTransactionRequest = new CorrectBatchTransactionRequest
            {
                voucherBatch = new VoucherBatch
                {
                    scannedBatchNumber = "58300013"
                },

                voucher = new[]
                {
                    new CorrectTransactionRequest
                    {
                        reasonCode = ExpertBalanceReason.Unbalanced,
                        voucher = new Voucher
                        {
                            auxDom = "001193",
                            bsbNumber ="013812",
                            accountNumber="256902729",
                            transactionCode ="50",
                            documentReferenceNumber = "583000026",
                            amount = "45.67",
                        }
                    },
                    new CorrectTransactionRequest
                    {
                        reasonCode = ExpertBalanceReason.None,
                        voucher = new Voucher
                        {
                            auxDom = "001193",
                            bsbNumber ="092002",
                            accountNumber="814649",
                            transactionCode ="50",
                            documentReferenceNumber = "583000027",
                            amount = "2341.45"
                        }
                    } 
                },
            };

            sampleDipsQueue = new DipsQueue
            {
                ResponseCompleted = false,
                S_LOCATION = "TransactionValidation",
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
                    balanceReason = "Unbalanced",
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
                    balanceReason = "",
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

        [TestMethod]
        public async Task WhenProcessAsync_ThenSaveBatchToDb_AndSaveCommit()
        {
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
            sut.Message = new CorrectBatchTransactionRequest
            {
                voucherBatch = new VoucherBatch
                {
                    scannedBatchNumber = "58300013"
                },

                voucher = new[]
                {
                    new CorrectTransactionRequest
                    {
                        reasonCode = ExpertBalanceReason.Unbalanced,
                        voucher = new Voucher
                        {
                            auxDom = "001193",
                            bsbNumber ="013812",
                            accountNumber="256902729",
                            transactionCode ="50",
                            documentReferenceNumber = "583000026",
                            amount = "45.67",
                            processingDate = DateTime.Now
                        }
                    },
                    new CorrectTransactionRequest
                    {
                        reasonCode = ExpertBalanceReason.None,
                        voucher = new Voucher
                        {
                            auxDom = "001193",
                            bsbNumber ="092002",
                            accountNumber="814649",
                            transactionCode ="50",
                            documentReferenceNumber = "583000027",
                            amount = "2341.45",
                            processingDate = DateTime.Now
                        }
                    } 
                }

            };

            await sut.ProcessAsync(cancellationToken, Guid.NewGuid().ToString(), string.Empty);

            dipsDbContext.Verify(x => x.SaveChanges());
            transaction.Verify(x => x.Commit());
        }

        [TestMethod]
        public void WhenProcessAsync_AndGeneralException_ThenRollBackAndLogError()
        {
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
            sut.Message = sampleCorrectBatchTransactionRequest;
            var jobIdentifier = Guid.NewGuid().ToString();

            try
            {
                Task.WaitAll(sut.ProcessAsync(cancellationToken, jobIdentifier, string.Empty));
            }
            // ReSharper disable once EmptyGeneralCatchClause
            catch (Exception)
            {
                //intentional
            }

            transaction.Verify(x => x.Rollback());
            logger.Verify(x => x.Error(ex, It.IsAny<string>(), sampleCorrectBatchTransactionRequest, jobIdentifier));
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
                .Setup(x => x.Map(It.IsAny<CorrectBatchTransactionRequest>()))
                .Returns(queue);
        }

        private void ExpectVoucherMapperToReturnVouchers(IEnumerable<DipsNabChq> vouchers)
        {
            dipsVoucherMapper
                .Setup(x => x.Map(It.IsAny<CorrectBatchTransactionRequest>()))
                .Returns(vouchers);
        }

        private void ExpectDbIndexMapperToReturnDbIndexes(IEnumerable<DipsDbIndex> dbIndices)
        {
            dipsDbIndexMapper
                .Setup(x => x.Map(It.IsAny<CorrectBatchTransactionRequest>()))
                .Returns(dbIndices);
        }

        private void ExpectComponentToCreateBeginLifetimeScope()
        {
            lifetimeScope
                .Setup(x => x.BeginLifetimeScope())
                .Returns(lifetimeScope.Object);
        }

        private void ExpectContextToCreateTransaction()
        {
            dipsDbContext
                .Setup(x => x.BeginTransaction())
                .Returns(transaction.Object);
        }

        private CorrectTransactionRequestProcessor CreateMessageProcessor()
        {
            return new CorrectTransactionRequestProcessor(lifetimeScope.Object, dipsQueueMapper.Object, dipsVoucherMapper.Object, dipsDbIndexMapper.Object, imageMergeHelper.Object, dipsDbContext.Object);
        }
    }
}
