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
    public class ValidateCodelineResponsePollingJobTests
    {
        private Mock<ILifetimeScope> lifetimeScope;
        private Mock<IDipsDbContext> dipsDbContext;
        private Mock<IExchangePublisher<ValidateBatchCodelineResponse>> exchangePublisher;
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
            exchangePublisher = new Mock<IExchangePublisher<ValidateBatchCodelineResponse>>();
            transaction = new Mock<IDipsDbContextTransaction>();
            logger = new Mock<ILogger>();
            adapterConfiguration = new Mock<IAdapterConfiguration>();

            Log.Logger = logger.Object;

            queues = new InMemoryDbSet<DipsQueue>(true);
            vouchers = new InMemoryDbSet<DipsNabChq>(true);

        }

        [TestMethod]
        public void WhenExecute_ThenGetCompletedBatches()
        {
            queues.Add(new DipsQueue
            {
                ResponseCompleted = false,
                S_LOCATION = "CodelineValidationDone",
                S_LOCK = "0",
                S_SDATE = "01/01/15",
                S_STIME = "12:12:12"
            });

            ExpectContextToCreateTransaction();
            ExpectContextToReturnQueues(queues);
            ExpectContextToReturnVouchers(vouchers);

            var sut = CreatePollingJob();

            sut.Execute(null);

            dipsDbContext.Verify(x => x.Queues);
        }

        [TestMethod]
        public void GivenBadVoucher_WhenExecute_ThenPublishMappedVoucherStatus()
        {
            queues.Add(new DipsQueue
            {
                ResponseCompleted = false,
                S_BATCH = "xxx",
                S_LOCATION = "CodelineValidationDone",
                S_LOCK = "0",
                S_SDATE = "01/01/15",
                S_STIME = "12:12:12",
                CorrelationId = "yyy",
                S_JOB_ID = "58300013",
                RoutingKey = "123456"
            });

            //all flags are true
            vouchers.Add(new DipsNabChq
            {
                S_BATCH = "xxx",
                S_TRACE = "zzz",
                S_STATUS1 = "1008",
                proc_date = "20150707",
                processing_state = "SA",
                captureBSB = "085384",
                doc_ref_num = "zzz"
            });

            ExpectContextToCreateTransaction();
            ExpectContextToReturnQueues(queues);
            ExpectContextToReturnVouchers(vouchers);

            var sut = CreatePollingJob();

            sut.Execute(null);

            exchangePublisher.Verify(x => x.PublishAsync(
                It.Is<ValidateBatchCodelineResponse>(r =>
                    !r.voucher.Single().extraAuxDomStatus
                    && !r.voucher.Single().auxDomStatus
                    && !r.voucher.Single().accountNumberStatus
                    && !r.voucher.Single().amountStatus
                    && !r.voucher.Single().bsbNumberStatus
                    && !r.voucher.Single().transactionCodeStatus
                    && r.voucher.Single().documentReferenceNumber == "zzz"),
                "yyy", "123456"));
        }

        [TestMethod]
        public void GivenGoodVoucher_WhenExecute_ThenPublishMappedVoucherStatus()
        {
            queues.Add(new DipsQueue
            {
                ResponseCompleted = false,
                S_BATCH = "xxx",
                S_LOCATION = "CodelineValidationDone",
                S_LOCK = "0",
                S_SDATE = "01/01/15",
                S_STIME = "12:12:12",
                CorrelationId = "yyy",
                S_JOB_ID = "58300013",
                RoutingKey = "123456"
            });

            //all flags are true
            vouchers.Add(new DipsNabChq
            {
                S_BATCH = "xxx", 
                S_TRACE = "zzz", 
                S_STATUS1 = "0",
                proc_date = "20150707",
                processing_state = "SA",
                captureBSB = "085384",
                doc_ref_num = "zzz"
            });

            ExpectContextToCreateTransaction();
            ExpectContextToReturnQueues(queues);
            ExpectContextToReturnVouchers(vouchers);

            var sut = CreatePollingJob();

            sut.Execute(null);

            exchangePublisher.Verify(x => x.PublishAsync(
                It.Is<ValidateBatchCodelineResponse>(r =>
                    r.voucher.Single().extraAuxDomStatus
                    && r.voucher.Single().auxDomStatus
                    && r.voucher.Single().accountNumberStatus
                    && r.voucher.Single().amountStatus
                    && r.voucher.Single().bsbNumberStatus
                    && r.voucher.Single().transactionCodeStatus
                    && r.voucher.Single().documentReferenceNumber == "zzz"),
                "yyy", "123456"));
        }

        [TestMethod]
        public void WhenExecute_ThenFlagCodelineValidationAsCompleted_AndSave()
        {
            queues.Add(new DipsQueue
            {
                ResponseCompleted = false,
                S_LOCATION = "CodelineValidationDone",
                S_LOCK = "0",
                S_SDATE = "01/01/15",
                S_STIME = "12:12:12"
            });

            ExpectContextToCreateTransaction();
            ExpectContextToReturnQueues(queues);
            ExpectContextToReturnVouchers(vouchers);

            var sut = CreatePollingJob();

            sut.Execute(null);

            Assert.IsFalse(queues.Any(q => !q.ResponseCompleted));
            dipsDbContext.Verify(x => x.SaveChanges());
        }

        [TestMethod]
        public void WhenExecute_AndConcurrencyException_ThenRollback()
        {
            queues.Add(new DipsQueue
            {
                ResponseCompleted = false,
                S_LOCATION = "CodelineValidationDone",
                S_LOCK = "0",
                S_SDATE = "01/01/15",
                S_STIME = "12:12:12"
            });

            ExpectContextToCreateTransaction();
            ExpectContextToReturnQueues(queues);

            dipsDbContext.Setup(x => x.SaveChanges())
                .Throws(new OptimisticConcurrencyException());

            var sut = CreatePollingJob();

            sut.Execute(null);

            transaction.Verify(x => x.Rollback());
        }

        [TestMethod]
        public void WhenExecute_AndConcurrencyException_ThenLogWarning()
        {

            queues.Add(new DipsQueue
            {
                ResponseCompleted = false,
                S_LOCATION = "CodelineValidationDone",
                S_LOCK = "0",
                S_SDATE = "01/01/15",
                S_STIME = "12:12:12"
            });

            ExpectContextToCreateTransaction();
            ExpectContextToReturnQueues(queues);
            ExpectContextToReturnVouchers(vouchers);

            dipsDbContext.Setup(x => x.SaveChanges())
                .Throws(new OptimisticConcurrencyException());

            var sut = CreatePollingJob();

            sut.Execute(null);

            logger.Verify(x => x.Warning(It.IsAny<string>(), queues.Single().S_BATCH));
        }

        [TestMethod]
        public void WhenExecute_AndGeneralException_ThenLogError()
        {

            queues.Add(new DipsQueue
            {
                ResponseCompleted = false,
                S_LOCATION = "CodelineValidationDone",
                S_LOCK = "0",
                S_SDATE = "01/01/15",
                S_STIME = "12:12:12"
            });

            ExpectContextToCreateTransaction();
            ExpectContextToReturnQueues(queues);
            ExpectContextToReturnVouchers(vouchers);

            var ex = new Exception();
            dipsDbContext.Setup(x => x.SaveChanges())
                .Throws(ex);

            var sut = CreatePollingJob();

            sut.Execute(null);


            logger.Verify(x => x.Error(ex, It.IsAny<string>(), queues.Single().S_BATCH));

        }

        //private void ExpectPublisherToPublishAsync()
        //{
        //    exchangePublisher
        //        .Setup(x => x.PublishAsync())
        //        .Returns(exchangePublisher.Object.PublishAsync());
        //}

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

        private ValidateCodelineResponsePollingJob CreatePollingJob()
        {
            return new ValidateCodelineResponsePollingJob(lifetimeScope.Object, exchangePublisher.Object, adapterConfiguration.Object, dipsDbContext.Object);
        }
    }
}
