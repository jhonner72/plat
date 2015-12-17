namespace Lombard.ECLMatchingEngine.UnitTests.MessageProcessors
{
    using System;
    using System.Threading;
    using System.ComponentModel.DataAnnotations;
    using System.IO;
    using System.Collections.Generic;
    using System.IO.Abstractions;
    using Lombard.Common.FileProcessors;
    using Lombard.Common.Queues;
    using Lombard.ECLMatchingEngine.Service.Configuration;
    using Lombard.ECLMatchingEngine.Service.Domain;
    using Lombard.ECLMatchingEngine.Service.MessageProcessors;
    using Lombard.ECLMatchingEngine.Service.Mappers;
    using Lombard.ECLMatchingEngine.Service.Utils;
    using Lombard.ECLMatchingEngine.Service.Constants;
    using Lombard.Vif.Service.Messages.XsdImports;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Moq;

    [TestClass]
    public class UT_CreateECLFileRequestProcessor
    {
        private Mock<IExchangePublisher<MatchVoucherResponse>> publisher;
        private Mock<IMatchVoucherRequestToECLRecordBatch> ECLFiles;
        private Mock<IMatchVoucherRequestToVoucherInformationBatch> VoucherBatch;
        private Mock<IMatchedVoucherInformationToECLFileInfo> MatchedVouchers;
        private Mock<IECLRecordFileSystem> fileWriter;

        public UT_CreateECLFileRequestProcessor()
        {
            publisher = new Mock<IExchangePublisher<MatchVoucherResponse>>();
            ECLFiles = new Mock<IMatchVoucherRequestToECLRecordBatch>();
            VoucherBatch = new Mock<IMatchVoucherRequestToVoucherInformationBatch>();
            MatchedVouchers = new Mock<IMatchedVoucherInformationToECLFileInfo>();
            fileWriter = new Mock<IECLRecordFileSystem>();
        }

        [TestMethod]
        public void CreateECLFileRequestProcessor()
        {
            var matchVoucherProcessor = this.GetCreateECLFileRequestProcessor();
            var request = matchVoucherProcessor.Message;
            var mockVoucherInformationData = MockVoucherInformation();
            
            
            var VoucherInfoBatch = VoucherBatch
                .Setup(a => a.Map(request))
                .Returns(ValidatedResponse<iVoucherInfoBatch>.Success(new VoucherInfoBatch
                {
                    JobIdentifier = "AnyJobID",
                    VoucherInformation = mockVoucherInformationData.VoucherInformation
                }));

            var ECLItems = ECLFiles
                .Setup(b => b.Map(request))
                .Returns(ValidatedResponse<IEnumerable<ECLRecord>>.Success(new[] { new ECLRecord("D0809990461087460   000233531000000000000000010000041M19/08/15 083029 083309 020211200 41    Z  083894 999999999") { Amount = It.IsAny<string>(), ChequeSerialNumber = It.IsAny<string>(), DrawerAccountNumber = It.IsAny<string>(), ECLInput = It.IsAny<string>(), ExchangeModeCode = It.IsAny<string>(), LedgerBSBCode = It.IsAny<string>() } }));

            var matchedVouchers = new Mock<IEnumerable<VoucherInformation>>();

            //matchVoucherProcessor.ProcessAsync(new CancellationToken(), "someCorrelationId", "aRoutingKey").Wait();
        }
        [TestMethod]
        [ExpectedException(typeof(AggregateException))]
        public void CreateECLFileRequestProcessor_GivenNoJSONFiles_ShouldExitWithError()
        {
            var matchVoucherProcessor = this.GetCreateECLFileRequestProcessor();
            var request = matchVoucherProcessor.Message;

            var VoucherInfoBatch = VoucherBatch
                .Setup(a => a.Map(request))
                .Returns(ValidatedResponse<iVoucherInfoBatch>.Failure(new[] { new ValidationResult("ErrorMessage") }));

           matchVoucherProcessor.ProcessAsync(new CancellationToken(), "someCorrelationId", "aRoutingKey").Wait();
        }

        [TestMethod]
        [ExpectedException(typeof(AggregateException))]
        public void CreateECLFileRequestProcessor_GivenNoReturnedECLFIles_ShouldExitWithError()
        {
            var matchVoucherProcessor = this.GetCreateECLFileRequestProcessor();
            var request = matchVoucherProcessor.Message;
            var mockVoucherInformationData = new List<IECLRecordVoucherInfo>()
            {
                new ECLRecordVoucherInfo{
                     Voucher =  new VoucherInformation { voucher = null, voucherProcess = null, voucherBatch = null }}
            };
            var VoucherInfoBatch = VoucherBatch
                .Setup(a => a.Map(request))
                .Returns(ValidatedResponse<iVoucherInfoBatch>.Success(new VoucherInfoBatch
                {
                    JobIdentifier = "AnyJobID",
                    VoucherInformation = mockVoucherInformationData
                }));

            var ECLItems = ECLFiles
               .Setup(b => b.Map(request))
               .Returns(ValidatedResponse<IEnumerable<ECLRecord>>.Failure(new[] { new ValidationResult("ErrorMessage") }));

            var matchedVouchers = new Mock<IEnumerable<VoucherInformation>>();

            matchVoucherProcessor.ProcessAsync(new CancellationToken(), "someCorrelationId", "aRoutingKey").Wait();
        }
        private CreateECLFileRequestProcessor GetCreateECLFileRequestProcessor()
        {
            var request = new MatchVoucherRequest { jobIdentifier = "AnyJobID" };

            return new CreateECLFileRequestProcessor(
                publisher.Object,
                ECLFiles.Object,
                VoucherBatch.Object,
                MatchedVouchers.Object,
                fileWriter.Object) 
                { 
                    Message =  new MatchVoucherRequest 
                    { 
                        jobIdentifier = "AnyJobID" 
                    }
                };
        }
        private VoucherInfoBatch MockVoucherInformation()
        {
            return new VoucherInfoBatch
            {
                VoucherInformation = new List<IECLRecordVoucherInfo>()
                {
                    new ECLRecordVoucherInfo
                    {
                         Voucher = new VoucherInformation
                                {
                                      voucher =  new Voucher
                                      { 
                                         amount = "100000",
                                         auxDom = "233531",
                                         accountNumber = "461087460",
                                         bsbNumber ="080999"
                                      },
                                       voucherProcess = new VoucherProcess
                                       {
                                            apPresentmentType = APPresentmentTypeEnum.M
                                       }
                                }
                    },
                    new ECLRecordVoucherInfo
                    {
                         Voucher = new VoucherInformation
                            {
                                 voucher = new Voucher
                                 {
                                    amount = "15728",
                                    auxDom = "475651",
                                    accountNumber = "NotValid",
                                    bsbNumber ="033225"
                                 },
                                 voucherProcess = new VoucherProcess
                                   {
                                        apPresentmentType = APPresentmentTypeEnum.M
                                   }
                            }
                    }
                }

            };

        }
    }
}
