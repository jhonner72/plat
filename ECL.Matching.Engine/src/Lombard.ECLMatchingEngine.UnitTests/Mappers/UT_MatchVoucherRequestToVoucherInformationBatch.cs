namespace Lombard.ECLMatchingEngine.UnitTests.Mappers
{
    using System;
    using System.ComponentModel.DataAnnotations;
    using System.IO;
    using System.Collections.Generic;
    using System.IO.Abstractions;
    using Lombard.Common.FileProcessors;
    using Lombard.Common.Queues;
    using Lombard.ECLMatchingEngine.Service.Configuration;
    using Lombard.ECLMatchingEngine.Service.MessageProcessors;
    using Lombard.ECLMatchingEngine.Service.Mappers;
    using Lombard.ECLMatchingEngine.Service.Utils;
    using Lombard.Vif.Service.Messages.XsdImports;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Newtonsoft;
    using Moq;

    [TestClass]
    public class UT_MatchVoucherRequestToVoucherInformationBatch
    {
        private Mock<IECLRecordConfiguration> eclConfiguration;
        private Mock<IFileSystem> eclFileSystem;
        private readonly MatchVoucherRequest message;
        
        public UT_MatchVoucherRequestToVoucherInformationBatch()
        {
            message = new MatchVoucherRequest
            {
                jobIdentifier = "AnyJobID"
            };
            
            eclConfiguration = new Mock<IECLRecordConfiguration>();
            eclFileSystem = new Mock<IFileSystem>();
        }

        [TestMethod]
        public void MatchVoucherRequestToVoucherInformationBatch_GivenAValidJobID_ShouldReturnASuccessValidationResponse()
        {
            var matchVoucherProcessor = this.GetMatchVoucherRequestToVoucherInformationBatch();
            var test = new List<string>();
            var JobIDPath = eclFileSystem
                .Setup(f => f.Path.Combine(It.IsAny<string>(), It.IsAny<string>()))
                .Returns("somePath");

            var metadataFiles = eclFileSystem
                .Setup(g => g.Directory.EnumerateFiles("somePath", "VOUCHER_*.json"))
                .Returns(new [] { "AnyFilenameString.JSON" });

            var stringContents = eclFileSystem
                .Setup(d => d.File.ReadAllText("AnyFilenameString.JSON"))
                .Returns("{\"voucher\": {},\"voucherBatch\": {}, \"voucherProcess\": {}}");

            matchVoucherProcessor.Map(this.message);

        }

        [TestMethod]
        public void MatchVoucherRequestToVoucherInformationBatch_GivenInValidJobID_ShouldReturnAFailureValidationResponse()
        {
            var matchVoucherProcessor = this.GetMatchVoucherRequestToVoucherInformationBatch();
            message.jobIdentifier = string.Empty;

            matchVoucherProcessor.Map(this.message);

        }
        [TestMethod]
        public void MatchVoucherRequestToVoucherInformationBatch_GivenInValidPath_ShouldReturnAFailureValidationResponse()
        {
            var matchVoucherProcessor = this.GetMatchVoucherRequestToVoucherInformationBatch();
            var test = new List<string>();
            var JobIDPath = eclFileSystem
                .Setup(f => f.Path.Combine(It.IsAny<string>(), "inValidPath"))
                .Returns(string.Empty);

            matchVoucherProcessor.Map(this.message);

        }
        [TestMethod]
        public void MatchVoucherRequestToVoucherInformationBatch_GivenNoVouchersAvailable_ShouldReturnASuccessValidationResponseWithEmptyList()
        {
            var matchVoucherProcessor = this.GetMatchVoucherRequestToVoucherInformationBatch();
            var test = new List<string>();

            var JobIDPath = eclFileSystem
                .Setup(f => f.Path.Combine(It.IsAny<string>(), It.IsAny<string>()))
                .Returns("somePath");

            var metadataFiles = eclFileSystem
                .Setup(g => g.Directory.EnumerateFiles("somePath", "VOUCHER_*.json"))
                .Returns(new List<string>());

            matchVoucherProcessor.Map(this.message);

        }
        private MatchVoucherRequestToVoucherInformationBatch GetMatchVoucherRequestToVoucherInformationBatch()
        {
            return new MatchVoucherRequestToVoucherInformationBatch(
                eclConfiguration.Object,
                eclFileSystem.Object
                );
        }

    }
}
