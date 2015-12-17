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
    public class GetVouchersInformationRequestPollingJobTests
    {
        private Mock<ILifetimeScope> lifetimeScope;
        private Mock<IDipsDbContext> dipsDbContext;
        private Mock<IExchangePublisher<GetVouchersInformationRequest>> exchangePublisher;
        private Mock<IDipsDbContextTransaction> transaction;
        private Mock<ILogger> logger;
        private Mock<IAdapterConfiguration> adapterConfiguration;

        private InMemoryDbSet<DipsQueue> queues;
        private InMemoryDbSet<DipsRequest> dipsRequests;

        [TestInitialize]
        public void TestInitialize()
        {
            lifetimeScope = new Mock<ILifetimeScope>();
            dipsDbContext = new Mock<IDipsDbContext>();
            exchangePublisher = new Mock<IExchangePublisher<GetVouchersInformationRequest>>();
            transaction = new Mock<IDipsDbContextTransaction>();
            logger = new Mock<ILogger>();
            adapterConfiguration = new Mock<IAdapterConfiguration>();

            Log.Logger = logger.Object;

            queues = new InMemoryDbSet<DipsQueue>(true);
            dipsRequests = new InMemoryDbSet<DipsRequest>(true);
        }

        [TestMethod]
        public void GetVouchersInformation_WhenExecute_ThenSaveBatchToDb_AndSaveCommit()
        {
            dipsRequests.Add(GenerateDipsRequest());

            ExpectContextToCreateTransaction();
            ExpectContextToReturnQueues(queues);
            ExpectContextToReturnDipsRequests(dipsRequests);
            ExpectToNotDelete();

            var sut = CreatePollingJob();

            sut.Execute(null);

            dipsDbContext.Verify(x => x.SaveChanges());
            transaction.Verify(x => x.Commit());
        }

        [TestMethod]
        public void GetVouchersInformation_WhenExecute_ThenFlagCodelineCorrectionAsCompleted_AndSave()
        {
            dipsRequests.Add(GenerateDipsRequest());

            ExpectContextToCreateTransaction();
            ExpectContextToReturnQueues(queues);
            ExpectContextToReturnDipsRequests(dipsRequests);

            var sut = CreatePollingJob();

            sut.Execute(null);

            Assert.IsFalse(queues.Any(q => !q.ResponseCompleted));
            dipsDbContext.Verify(x => x.SaveChanges());
        }

        [TestMethod]
        public void GetVouchersInformation_WhenExecute_AndConcurrencyException_ThenRollback()
        {
            dipsRequests.Add(GenerateDipsRequest());

            ExpectContextToCreateTransaction();
            ExpectContextToReturnQueues(queues);
            ExpectContextToReturnDipsRequests(dipsRequests);

            dipsDbContext.Setup(x => x.SaveChanges())
                .Throws(new OptimisticConcurrencyException());

            var sut = CreatePollingJob();

            sut.Execute(null);

            transaction.Verify(x => x.Rollback());
        }

        [TestMethod]
        public void GetVouchersInformation_WhenExecute_AndConcurrencyException_ThenLogWarning()
        {
            dipsRequests.Add(GenerateDipsRequest());

            ExpectContextToCreateTransaction();
            ExpectContextToReturnQueues(queues);
            ExpectContextToReturnDipsRequests(dipsRequests);

            dipsDbContext.Setup(x => x.SaveChanges())
                .Throws(new OptimisticConcurrencyException());

            var sut = CreatePollingJob();

            sut.Execute(null);

            logger.Verify(x => x.Warning(It.IsAny<string>(), dipsRequests.Single().guid_name));
        }

        [TestMethod]
        public void GetVouchersInformation_WhenExecute_AndGeneralException_ThenLogError()
        {
            dipsRequests.Add(GenerateDipsRequest());

            ExpectContextToCreateTransaction();
            ExpectContextToReturnQueues(queues);
            ExpectContextToReturnDipsRequests(dipsRequests);

            var ex = new Exception();
            dipsDbContext.Setup(x => x.SaveChanges())
                .Throws(ex);

            var sut = CreatePollingJob();

            sut.Execute(null);

            logger.Verify(x => x.Error(ex, It.IsAny<string>(), dipsRequests.Single().guid_name));
        }

        private static DipsRequest GenerateDipsRequest()
        {
            return new DipsRequest
            {
                guid_name = Guid.NewGuid().ToString(),
                payload = "[{\"name\":\"accountNumber\",\"value\":\"256902729\"},{\"name\":\"amount\",\"value\":\"45.67\"},{\"name\":\"auxDom\",\"value\":\"013812\"},{\"name\":\"bsbNumber\",\"value\":\"256902729\"},{\"name\":\"documentReferenceNumber\",\"value\":\"900111222\"},{\"name\":\"documentType\",\"value\":\"CRT\"},{\"name\":\"extraAuxDom\",\"value\":\"\"},{\"name\":\"processingDate\",\"value\":\"01/01/0001 00:00:00\"},{\"name\":\"transactionCode\",\"value\":\"62\"},{\"name\":\"batchAccountNumber\",\"value\":\"58300013\"},{\"name\":\"batchType\",\"value\":\"OTC_Vouchers\"},{\"name\":\"captureBsb\",\"value\":\"013812\"},{\"name\":\"client\",\"value\":\"NAB\"},{\"name\":\"collectingBank\",\"value\":\"900111222\"},{\"name\":\"processingState\",\"value\":\"VIC\"},{\"name\":\"scannedBatchNumber\",\"value\":\"58300013\"},{\"name\":\"source\",\"value\":\"DIPS\"},{\"name\":\"subBatchType\",\"value\":\"OTC_Vouchers\"},{\"name\":\"unitID\",\"value\":\"065\"},{\"name\":\"workType\",\"value\":\"NABCHQ_SURPLUS\"},{\"name\":\"adjustedBy\",\"value\":null},{\"name\":\"adjustedFlag\",\"value\":\"False\"},{\"name\":\"adjustmentDescription\",\"value\":null},{\"name\":\"adjustmentLetterRequired\",\"value\":\"False\"},{\"name\":\"adjustmentReasonCode\",\"value\":\"0\"},{\"name\":\"apPresentmentType\",\"value\":\"\"},{\"name\":\"customerLinkNumber\",\"value\":null},{\"name\":\"documentRetrievalFlag\",\"value\":\"False\"},{\"name\":\"forValueType\",\"value\":\"\"},{\"name\":\"highValueFlag\",\"value\":\"False\"},{\"name\":\"inactiveFlag\",\"value\":\"False\"},{\"name\":\"isGeneratedBulkCredit\",\"value\":\"False\"},{\"name\":\"isGeneratedVoucher\",\"value\":\"False\"},{\"name\":\"isReservedForBalancing\",\"value\":\"False\"},{\"name\":\"isRetrievedVoucher\",\"value\":\"False\"},{\"name\":\"listingPageNumber\",\"value\":null},{\"name\":\"manualRepair\",\"value\":\"0\"},{\"name\":\"micrFlag\",\"value\":\"False\"},{\"name\":\"operatorId\",\"value\":null},{\"name\":\"postTransmissionQaAmountFlag\",\"value\":\"False\"},{\"name\":\"postTransmissionQaCodelineFlag\",\"value\":\"False\"},{\"name\":\"preAdjustmentAmount\",\"value\":null},{\"name\":\"presentationMode\",\"value\":null},{\"name\":\"rawMICR\",\"value\":null},{\"name\":\"rawOCR\",\"value\":null},{\"name\":\"releaseFlag\",\"value\":\"\"},{\"name\":\"repostFromDRN\",\"value\":null},{\"name\":\"repostFromProcessingDate\",\"value\":\"\"},{\"name\":\"surplusItemFlag\",\"value\":\"False\"},{\"name\":\"suspectFraud\",\"value\":\"False\"},{\"name\":\"thirdPartyCheckFailed\",\"value\":\"False\"},{\"name\":\"thirdPartyMixedDepositReturnFlag\",\"value\":\"False\"},{\"name\":\"thirdPartyPoolFlag\",\"value\":\"False\"},{\"name\":\"transactionLinkNumber\",\"value\":null},{\"name\":\"unencodedECDReturnFlag\",\"value\":\"False\"},{\"name\":\"unprocessable\",\"value\":\"False\"},{\"name\":\"voucherDelayedIndicator\",\"value\":null}]"
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

        private void ExpectContextToReturnDipsRequests(IDbSet<DipsRequest> voucherSet)
        {
            dipsDbContext
                .Setup(x => x.DipsRequest)
                .Returns(voucherSet);
        }

        private GetVouchersInformationRequestPollingJob CreatePollingJob()
        {
            return new GetVouchersInformationRequestPollingJob(lifetimeScope.Object, exchangePublisher.Object, adapterConfiguration.Object, dipsDbContext.Object);
        }

        private void ExpectToNotDelete()
        {
            adapterConfiguration.Setup(x => x.DeleteDatabaseRows)
                .Returns(false);
        }
    }
}
