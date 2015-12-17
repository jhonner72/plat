using System;
using System.Data.Entity;
using System.Data.Entity.Core;
using System.Linq;
using Autofac;
using Lombard.Adapters.Data;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.Data.Transaction;
using Lombard.Adapters.DipsAdapter.Configuration;
using Lombard.Adapters.DipsAdapter.Jobs;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Common.Queues;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using Serilog;

namespace Lombard.Adapters.DipsAdapter.UnitTests.Jobs
{
    [TestClass]
    public class GenerateCorrespondingVoucherResponsePollingJobTests
    {
        private Mock<ILifetimeScope> lifetimeScope;
        private Mock<IDipsDbContext> dipsDbContext;
        private Mock<IExchangePublisher<GenerateCorrespondingVoucherResponse>> exchangePublisher;
        private Mock<IDipsDbContextTransaction> transaction;
        private Mock<ILogger> logger;
        private Mock<IAdapterConfiguration> adapterConfiguration;

        private InMemoryDbSet<DipsQueue> queues;
        private InMemoryDbSet<DipsNabChq> vouchers;

        [TestInitialize]
        public void TestInitialize()
        {
            lifetimeScope = new Mock<ILifetimeScope>();
            dipsDbContext = new Mock<IDipsDbContext>();
            exchangePublisher = new Mock<IExchangePublisher<GenerateCorrespondingVoucherResponse>>();
            transaction = new Mock<IDipsDbContextTransaction>();
            logger = new Mock<ILogger>();
            adapterConfiguration = new Mock<IAdapterConfiguration>();

            Log.Logger = logger.Object;

            queues = new InMemoryDbSet<DipsQueue>(true);
            vouchers = new InMemoryDbSet<DipsNabChq>(true);
        }

        [TestMethod]
        public void GenerateCorrespondingVoucher_WhenExecute_ThenGetCompletedBatches()
        {
            queues.Add(GenerateVoucher());

            ExpectContextToCreateTransaction();
            ExpectContextToReturnQueues(queues);
            ExpectContextToReturnVouchers(vouchers);
            ExpectToNotDelete();

            var sut = CreatePollingJob();

            sut.Execute(null);

            dipsDbContext.Verify(x => x.Queues);
        }

        [TestMethod]
        public void GenerateCorrespondingVoucher_WhenExecute_ThenFlagCodelineCorrectionAsCompleted_AndSave()
        {
            queues.Add(GenerateVoucher());

            ExpectContextToCreateTransaction();
            ExpectContextToReturnQueues(queues);
            ExpectContextToReturnVouchers(vouchers);

            var sut = CreatePollingJob();

            sut.Execute(null);

            Assert.IsFalse(queues.Any(q => !q.ResponseCompleted));
            dipsDbContext.Verify(x => x.SaveChanges());
        }

        [TestMethod]
        public void GenerateCorrespondingVoucher_WhenExecute_AndConcurrencyException_ThenRollback()
        {
            queues.Add(GenerateVoucher());

            ExpectContextToCreateTransaction();
            ExpectContextToReturnQueues(queues);

            dipsDbContext.Setup(x => x.SaveChanges())
                .Throws(new OptimisticConcurrencyException());

            var sut = CreatePollingJob();

            sut.Execute(null);

            transaction.Verify(x => x.Rollback());
        }

        [TestMethod]
        public void GenerateCorrespondingVoucher_WhenExecute_AndConcurrencyException_ThenLogWarning()
        {
            queues.Add(GenerateVoucher());

            ExpectContextToCreateTransaction();
            ExpectContextToReturnQueues(queues);

            dipsDbContext.Setup(x => x.SaveChanges())
                .Throws(new OptimisticConcurrencyException());

            var sut = CreatePollingJob();

            sut.Execute(null);

            logger.Verify(x => x.Warning(It.IsAny<string>(), queues.Single().S_BATCH));
        }

        [TestMethod]
        public void GenerateCorrespondingVoucher_WhenExecute_AndGeneralException_ThenLogError()
        {
            queues.Add(GenerateVoucher());

            ExpectContextToCreateTransaction();
            ExpectContextToReturnQueues(queues);

            var ex = new Exception();
            dipsDbContext.Setup(x => x.SaveChanges())
                .Throws(ex);

            var sut = CreatePollingJob();

            sut.Execute(null);

            logger.Verify(x => x.Error(ex, It.IsAny<string>(), queues.Single().S_BATCH));

        }

        private static DipsQueue GenerateVoucher()
        {
            return new DipsQueue
            {
                ResponseCompleted = false,
                S_LOCATION = "GenerateCorrespondingVoucherDone",
                S_LOCK = "0",
                S_SDATE = "01/01/15",
                S_STIME = "12:12:12"
            };
        }

        private void ExpectContextToCreateTransaction()
        {
            dipsDbContext
                .Setup(x => x.BeginTransaction())
                .Returns(transaction.Object);
        }

        private void ExpectContextToReturnQueues(IDbSet<DipsQueue> queueSet)
        {
            dipsDbContext
                .Setup(x => x.Queues)
                .Returns(queueSet);
        }

        private void ExpectContextToReturnVouchers(IDbSet<DipsNabChq> voucherSet)
        {
            dipsDbContext
                .Setup(x => x.NabChqPods)
                .Returns(voucherSet);
        }

        private GenerateCorrespondingVoucherResponsePollingJob CreatePollingJob()
        {
            return new GenerateCorrespondingVoucherResponsePollingJob(lifetimeScope.Object, exchangePublisher.Object, adapterConfiguration.Object, dipsDbContext.Object);
        }

        private void ExpectToNotDelete()
        {
            adapterConfiguration.Setup(x => x.DeleteDatabaseRows)
                .Returns(false);
        }
    }
}
