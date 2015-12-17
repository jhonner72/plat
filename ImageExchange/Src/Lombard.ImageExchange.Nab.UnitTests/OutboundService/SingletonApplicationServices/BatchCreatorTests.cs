using System;
using System.Collections.Generic;
using Lombard.ImageExchange.Nab.OutboundService.Domain;
using Lombard.ImageExchange.Nab.OutboundService.Mappers;
using Lombard.ImageExchange.Nab.OutboundService.SingletonApplicationServices;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;

namespace Lombard.ImageExchange.Nab.UnitTests.OutboundService.SingletonApplicationServices
{
    [TestClass]
    public class BatchCreatorTests
    {
        private Mock<IFileNameCreator> coinFileName;
        
        private const string FakeEndpointShortName = "ABC";
        private const long FakeBatchNumber = 123;
        
        [TestInitialize]
        public void TestInitialize()
        {
            coinFileName = new Mock<IFileNameCreator>();
        }

        [TestMethod]
        public void Execute_SavesBatch_ThenEnsureVoucherBatchNumberIsSameAsBatchId()
        {
            var batchCreator = GetBatchCreator();
            
            var someVoucherItems = new List<OutboundVoucher> { new OutboundVoucher() };

            var someBatch = new Batch
            {
                BatchNumber = FakeBatchNumber,
                OutboundVouchers = someVoucherItems,
                ProcessingDate = DateTime.Today,
                ShortTargetEndpoint = FakeEndpointShortName
            };
            
            ExpectCoinFileNameToReturnForOutboundImageExchangeFile();

            var result = batchCreator.Execute(someBatch);

            Assert.AreEqual("someFileName", result.FileName);
            
            coinFileName.VerifyAll();
        }
        
        private void ExpectCoinFileNameToReturnForOutboundImageExchangeFile()
        {
            coinFileName
                .Setup(x => x.Execute(
                    It.Is<OutboundVoucherFile>(batch =>
                       batch.EndpointShortName == FakeEndpointShortName &&
                       batch.ProcessingDate == DateTime.Today &&
                       batch.BatchNumber == FakeBatchNumber)))
               .Returns("someFileName");
        }

        public BatchCreator GetBatchCreator()
        {
            return new BatchCreator(coinFileName.Object);
        }
    }
}