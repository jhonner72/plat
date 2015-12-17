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
    using Lombard.ECLMatchingEngine.Service.Data;
    using Lombard.Vif.Service.Messages.XsdImports;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Newtonsoft;
    using Moq;

    [TestClass]
    public class UT_MatchVoucherRequestToECLRecordBatch
    {
        private Mock<IECLInfoDataEntityFrameworks> eclConfiguration;
        private Mock<IFileSystem> eclFileSystem;
        private readonly MatchVoucherRequest message;
        
        public UT_MatchVoucherRequestToECLRecordBatch()
        {
            message = new MatchVoucherRequest
            {
                jobIdentifier = "AnyJobID"
            };

            eclConfiguration = new Mock<IECLInfoDataEntityFrameworks>();
            eclFileSystem = new Mock<IFileSystem>();
        }

        [TestMethod]
        public void MatchVoucherRequestToECLRecordBatch_GivenAValidInput_ShouldReturnListOfVoucherInformation()
        {
            var matchVoucherProcessor = this.GetMatchVoucherRequestToECLRecordBatch();

            var JobLocation = eclFileSystem
                .Setup(a => a.Path.Combine(It.IsAny<string>(), It.IsAny<string>()))
                .Returns("ValidJobLocation");

            var PathExist = eclFileSystem
                .Setup(b => b.Directory.Exists(It.IsAny<string>()))
                .Returns(true);

            var eclFiles = eclFileSystem
                .Setup(c => c.Directory.EnumerateFiles("ValidJobLocation", "ecl*.txt"))
                .Returns(new[] { "ecl_vic.txt"});

            var eclRecords = eclFileSystem
                .Setup(d => d.File.ReadLines(It.IsAny<string>()))
                .Returns(new[] {"D0809990461087460   000233531000000000000000010000041M19/08/15 083029 083309 020211200 41    Z  083894 999999999"});

            matchVoucherProcessor.Map(message);
            
        }

        [TestMethod]
        public void MatchVoucherRequestToECLRecordBatch_GivenInValidJobID_ShouldCallFailureFunction()
        {
            var matchVoucherProcessor = this.GetMatchVoucherRequestToECLRecordBatch();
            message.jobIdentifier = string.Empty;

            var JobLocation = eclFileSystem
                .Setup(a => a.Path.Combine(It.IsAny<string>(), It.IsAny<string>()))
                .Returns("NothingWillBeReturned_CallingFailureFunction");

            matchVoucherProcessor.Map(message);

        }

        [TestMethod]
        public void MatchVoucherRequestToECLRecordBatch_GivenInValidPath_ShouldCallFailureFunction()
        {
            var matchVoucherProcessor = this.GetMatchVoucherRequestToECLRecordBatch();

            var JobLocation = eclFileSystem
                .Setup(a => a.Path.Combine(It.IsAny<string>(), It.IsAny<string>()))
                .Returns("ValidJobLocation");

            var PathExist = eclFileSystem
               .Setup(b => b.Directory.Exists(It.IsAny<string>()))
               .Returns(false);

            matchVoucherProcessor.Map(message);

        }
        [TestMethod]
        public void MatchVoucherRequestToECLRecordBatch_GivenNoECLFiles_ShouldCallFailureFunction()
        {
            var matchVoucherProcessor = this.GetMatchVoucherRequestToECLRecordBatch();

            var JobLocation = eclFileSystem
                .Setup(a => a.Path.Combine(It.IsAny<string>(), It.IsAny<string>()))
                .Returns("ValidJobLocation");

            var PathExist = eclFileSystem
               .Setup(b => b.Directory.Exists(It.IsAny<string>()))
               .Returns(true);

            var eclFiles = eclFileSystem
                .Setup(c => c.Directory.EnumerateFiles("ValidJobLocation", "ecl*.txt"))
                .Returns(new List<string>());

            matchVoucherProcessor.Map(message);

        }

        [TestMethod]
        public void MatchVoucherRequestToECLRecordBatch_GivenNoEntryInAnyECLFiles_ShouldCallFailureFunction()
        {
            var matchVoucherProcessor = this.GetMatchVoucherRequestToECLRecordBatch();

            var JobLocation = eclFileSystem
                .Setup(a => a.Path.Combine(It.IsAny<string>(), It.IsAny<string>()))
                .Returns("ValidJobLocation");

            var PathExist = eclFileSystem
                .Setup(b => b.Directory.Exists(It.IsAny<string>()))
                .Returns(true);

            var eclFiles = eclFileSystem
                .Setup(c => c.Directory.EnumerateFiles("ValidJobLocation", "ecl*.txt"))
                .Returns(new[] { "ecl_vic.txt" });

            var eclRecords = eclFileSystem
                .Setup(d => d.File.ReadLines(It.IsAny<string>()))
                .Returns(new List<string>());

            matchVoucherProcessor.Map(message);

        }

        private MatchVoucherRequestToECLRecordBatch GetMatchVoucherRequestToECLRecordBatch()
        {
            return new MatchVoucherRequestToECLRecordBatch(
                eclConfiguration.Object,
                eclFileSystem.Object);
        }
               

    }
}
