using System;
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
using Lombard.Adapters.DipsAdapter.MessageProcessors;
using Lombard.Adapters.DipsAdapter.Messages;
using Serilog;

namespace Lombard.Adapters.DipsAdapter.UnitTests.MessageProcessors
{
    [TestClass]
    public class GetVouchersInformationResponseProcessorTests
    {
        private Mock<ILifetimeScope> lifetimeScope;
        private Mock<IDipsDbContext> dipsDbContext;
        private Mock<IDipsDbContextTransaction> transaction;
        private Mock<ILogger> logger;
        private InMemoryDbSet<DipsQueue> dipsQueueSet;
        private InMemoryDbSet<DipsNabChq> dipsNabChqSet;
        private InMemoryDbSet<DipsDbIndex> dipsDbIndexSet;
        private InMemoryDbSet<DipsResponseDone> dipsResponseSet;

        private GetVouchersInformationResponse sampleGetVouchersInformationResponse;
        
        [TestInitialize]
        public void TestInitialize()
        {
            lifetimeScope = new Mock<ILifetimeScope>();
            dipsDbContext = new Mock<IDipsDbContext>();
            transaction = new Mock<IDipsDbContextTransaction>();
            logger = new Mock<ILogger>();
      
            Log.Logger = logger.Object;

            dipsQueueSet = new InMemoryDbSet<DipsQueue>(true);
            dipsNabChqSet = new InMemoryDbSet<DipsNabChq>(true);
            dipsDbIndexSet = new InMemoryDbSet<DipsDbIndex>(true);
            dipsResponseSet = new InMemoryDbSet<DipsResponseDone>();

            InitializeTestData();
        }

        [TestMethod]
        public async Task GetVouchersInformationResponse_WhenProcessAsync_ThenSaveBatchToDb_AndSaveCommit()
        {
            //Setup Context
            ExpectContextToReturnDipsResponseDone(dipsResponseSet);
            ExpectContextToReturnDipsQueues(dipsQueueSet);
            ExpectContextToReturnDipsNabChqs(dipsNabChqSet);
            ExpectContextToReturnDipsDbIndexes(dipsDbIndexSet);
            
            ExpectContextToCreateTransaction();

            var cancellationToken = new CancellationToken(false);

            var sut = CreateMessageProcessor();
            await sut.ProcessAsync(cancellationToken, Guid.NewGuid().ToString(), string.Empty);

            dipsDbContext.Verify(x => x.SaveChanges());
            transaction.Verify(x => x.Commit());
        }

        [TestMethod]
        public async Task GetVouchersInformationResponse_WhenProcessAsync_AndGeneralException_ThenRollBackAndLogError()
        {
            //Setup Context
            ExpectContextToReturnDipsResponseDone(dipsResponseSet);
            ExpectContextToReturnDipsQueues(dipsQueueSet);
            ExpectContextToReturnDipsNabChqs(dipsNabChqSet);
            ExpectContextToReturnDipsDbIndexes(dipsDbIndexSet);
            
            ExpectContextToCreateTransaction();

            var cancellationToken = new CancellationToken(false);

            var ex = new Exception();
            dipsDbContext.Setup(x => x.SaveChanges())
                .Throws(ex);

            var sut = CreateMessageProcessor();
            sut.Message = sampleGetVouchersInformationResponse;
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
            logger.Verify(x => x.Error(ex, It.IsAny<string>(), sampleGetVouchersInformationResponse, jobIdentifier));
        }

        private void ExpectContextToReturnDipsResponseDone(IDbSet<DipsResponseDone> responseSet)
        {
            dipsDbContext
                .Setup(x => x.DipsResponseDone)
                .Returns(responseSet);
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

        private GetVouchersInformationResponseProcessor CreateMessageProcessor()
        {
            return new GetVouchersInformationResponseProcessor(lifetimeScope.Object, dipsDbContext.Object)
            {
                Message = sampleGetVouchersInformationResponse
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
            sampleGetVouchersInformationResponse = new GetVouchersInformationResponse
            {
                voucherInformation = new VoucherInformation[]
                {
                }
            };
        }
    }
}