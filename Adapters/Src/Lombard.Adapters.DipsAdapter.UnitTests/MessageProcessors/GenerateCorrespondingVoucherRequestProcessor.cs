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
    public class GenerateCorrespondingVoucherRequestProcessorTests
    {
        private Mock<ILifetimeScope> lifetimeScope;
        private Mock<IDipsDbContext> dipsDbContext;
        private Mock<IMapper<GenerateCorrespondingVoucherRequest, DipsQueue>> dipsQueueMapper;
        private Mock<IMapper<GenerateCorrespondingVoucherRequest, IEnumerable<DipsNabChq>>> dipsVoucherMapper;
        private Mock<IMapper<GenerateCorrespondingVoucherRequest, IEnumerable<DipsDbIndex>>> dipsDbIndexMapper;
        private Mock<IDipsDbContextTransaction> transaction;
        private Mock<ILogger> logger;
        private Mock<IAdapterConfiguration> adapterConfiguration;
        private InMemoryDbSet<DipsQueue> dipsQueueSet;
        private InMemoryDbSet<DipsNabChq> dipsNabChqSet;
        private InMemoryDbSet<DipsDbIndex> dipsDbIndexSet;

        private string batchNumberFormat = "702{0}";
        
        private GenerateCorrespondingVoucherRequest sampleGenerateCorrespondingVoucherRequest;
        private DipsQueue sampleDipsQueue;
        private IEnumerable<DipsNabChq> sampleDipsNabChqs;
        private IEnumerable<DipsDbIndex> sampleDipsDbIndexes;


        [TestInitialize]
        public void TestInitialize()
        {
            lifetimeScope = new Mock<ILifetimeScope>();
            dipsDbContext = new Mock<IDipsDbContext>();
            dipsQueueMapper = new Mock<IMapper<GenerateCorrespondingVoucherRequest, DipsQueue>>();
            dipsVoucherMapper = new Mock<IMapper<GenerateCorrespondingVoucherRequest, IEnumerable<DipsNabChq>>>();
            dipsDbIndexMapper = new Mock<IMapper<GenerateCorrespondingVoucherRequest, IEnumerable<DipsDbIndex>>>();
            transaction = new Mock<IDipsDbContextTransaction>();
            logger = new Mock<ILogger>();
            adapterConfiguration = new Mock<IAdapterConfiguration>();

            Log.Logger = logger.Object;

            dipsQueueSet = new InMemoryDbSet<DipsQueue>(true);
            dipsNabChqSet = new InMemoryDbSet<DipsNabChq>(true);
            dipsDbIndexSet = new InMemoryDbSet<DipsDbIndex>(true);

            InitializeTestData();
        }

        [TestMethod]
        public async Task GenerateCorrespondingVoucher_WhenProcessAsync_ThenSaveBatchToDb_AndSaveCommit()
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
        public async Task GenerateCorrespondingVoucher_WhenProcessAsync_AndGeneralException_ThenRollBackAndLogError()
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
            sut.Message = sampleGenerateCorrespondingVoucherRequest;
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
            logger.Verify(x => x.Error(ex, It.IsAny<string>(), sampleGenerateCorrespondingVoucherRequest, jobIdentifier));
        }

        // Generate Batch Number
        [TestMethod]
        public async Task GenerateCorrespondingVoucher_WhenProcessAsync_GenerateBatchNumber_AndUpdateRequest()
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

            Assert.IsTrue(sut.Message.generateVoucher.All(n => !string.IsNullOrEmpty(n.voucherBatch.scannedBatchNumber)));
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
                .Setup(x => x.Map(It.IsAny<GenerateCorrespondingVoucherRequest>()))
                .Returns(queue);
        }

        private void ExpectVoucherMapperToReturnVouchers(IEnumerable<DipsNabChq> vouchers)
        {
            dipsVoucherMapper
                .Setup(x => x.Map(It.IsAny<GenerateCorrespondingVoucherRequest>()))
                .Returns(vouchers);
        }

        private void ExpectDbIndexMapperToReturnDbIndexes(IEnumerable<DipsDbIndex> dbIndices)
        {
            dipsDbIndexMapper
                .Setup(x => x.Map(It.IsAny<GenerateCorrespondingVoucherRequest>()))
                .Returns(dbIndices);
        }

        private GenerateCorrespondingVoucherRequestProcessor CreateMessageProcessor()
        {
            return new GenerateCorrespondingVoucherRequestProcessor(lifetimeScope.Object, dipsQueueMapper.Object, dipsVoucherMapper.Object, dipsDbIndexMapper.Object, adapterConfiguration.Object, dipsDbContext.Object)
            {
                Message = sampleGenerateCorrespondingVoucherRequest
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
            sampleGenerateCorrespondingVoucherRequest = new GenerateCorrespondingVoucherRequest
            {
                jobIdentifier = "NCST-6e5bc63b-be84-4053-a4ce-191abbd69f27",
                generateVoucher = new VoucherInformation[]
                {
                    new VoucherInformation
                    {
                        voucherBatch = new VoucherBatch
                        {
                            scannedBatchNumber = string.Empty
                        },
                        voucherProcess = new VoucherProcess
                        {
                        },
                        voucher = new Voucher
                        {
                        }
                    }
                }
            };

            sampleDipsQueue = new DipsQueue
            {
                ResponseCompleted = false,
                S_LOCATION = "GenerateCorrespondingVoucher",
                S_LOCK = "0"
            };

            sampleDipsNabChqs = new List<DipsNabChq>
            {
                new DipsNabChq
                {
                    
                },
                new DipsNabChq
                {
                   
                }
            };

            sampleDipsDbIndexes = new List<DipsDbIndex>
            {
                new DipsDbIndex
                {
                },
                new DipsDbIndex
                {
                }
            };
        }
    }
}