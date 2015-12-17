using System;
using System.Collections.Generic;
using System.Configuration;
using System.IO.Abstractions.TestingHelpers;
using System.Linq;
using System.Threading.Tasks;
using Castle.Components.DictionaryAdapter;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Configuration;
using Lombard.Adapters.DipsAdapter.Helpers;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Diagnostics.CodeAnalysis;

namespace Lombard.Adapters.DipsAdapter.UnitTests.Helpers
{
    [TestClass]
    public class ImageMergeHelperTests
    {
        private IAdapterConfiguration adapterConfig;
        private MockFileSystem fileSystem;

        private const string JobIdentifier = "17032015-3AEA-4069-A2DD-SSSS00000012";
        private const string BatchNumber = "20022015";
        private readonly DateTime processingDate = new DateTime(2015, 03, 17);

        private string imageDirectory;
        private string targetDirectory;

        [TestInitialize]
        public void TestInitialize()
        {
            adapterConfig = new DictionaryAdapterFactory().GetAdapter<IAdapterConfiguration>(ConfigurationManager.AppSettings);
            fileSystem = new MockFileSystem();

            imageDirectory = string.Format(adapterConfig.PackageSourceDirectory, JobIdentifier);
            targetDirectory = string.Format(@"{0}\{1}", adapterConfig.ImagePath, BatchNumber.Substring(0, 5));
        }

        [TestMethod]
        public async Task WhenEnsureMergedImageFilesExist_ThenWriteImageFiles()
        {
            ExpectDirectory(imageDirectory);
            ExpectDirectory(targetDirectory);
            ExpectImageFiles();

            var sut = CreateHelper();

            await sut.EnsureMergedImageFilesExistAsync(JobIdentifier, BatchNumber, processingDate);

            Assert.IsTrue(fileSystem.FileExists(string.Format(@"{0}\{1}.IM1", targetDirectory, BatchNumber)));
            Assert.IsTrue(fileSystem.FileExists(string.Format(@"{0}\{1}.IM2", targetDirectory, BatchNumber)));
            Assert.IsTrue(fileSystem.FileExists(string.Format(@"{0}\IM{1}.xml", targetDirectory, BatchNumber)));
        }

        [TestMethod]
        public void WhenPopulateMergedImageInfo_ThenPopulateVouchers()
        {
            var vouchers = new List<DipsNabChq>
            {
                new DipsNabChq {S_TRACE = "000000000001"},
                new DipsNabChq {S_TRACE = "000000000002"},
            };
            
            ExpectDirectory(imageDirectory);
            ExpectDirectory(targetDirectory);
            ExpectImageFiles();
            ExpectMergedImageFiles();
            ExpectImageMetadataFile();

            var sut = CreateHelper();

            sut.PopulateMergedImageInfo(JobIdentifier, BatchNumber, vouchers);

            var firstVoucher = vouchers.Single(x => x.S_TRACE == "000000000001");
            Assert.AreEqual("0", firstVoucher.S_IMG1_OFF.Trim());
            Assert.AreEqual("1", firstVoucher.S_IMG1_LEN.Trim());
            Assert.AreEqual("0", firstVoucher.S_IMG2_OFF.Trim());
            Assert.AreEqual("2", firstVoucher.S_IMG2_LEN.Trim());

            var secondVoucher = vouchers.Single(x => x.S_TRACE == "000000000002");
            Assert.AreEqual("1", secondVoucher.S_IMG1_OFF.Trim());
            Assert.AreEqual("3", secondVoucher.S_IMG1_LEN.Trim());
            Assert.AreEqual("2", secondVoucher.S_IMG2_OFF.Trim());
            Assert.AreEqual("4", secondVoucher.S_IMG2_LEN.Trim());
        }

        private void ExpectImageFiles()
        {
            fileSystem.AddFile(string.Format(@"{0}\VOUCHER_{1:ddMMyyyy}_000000001_FRONT.JPG", imageDirectory, processingDate), new MockFileData(new byte[] { 0x41 }));
            fileSystem.AddFile(string.Format(@"{0}\VOUCHER_{1:ddMMyyyy}_000000001_REAR.JPG", imageDirectory, processingDate), new MockFileData(new byte[] { 0x42, 0x42 }));
            fileSystem.AddFile(string.Format(@"{0}\VOUCHER_{1:ddMMyyyy}_000000002_FRONT.JPG", imageDirectory, processingDate), new MockFileData(new byte[] { 0x43, 0x43, 0x43 }));
            fileSystem.AddFile(string.Format(@"{0}\VOUCHER_{1:ddMMyyyy}_000000002_REAR.JPG", imageDirectory, processingDate), new MockFileData(new byte[] { 0x44, 0x44, 0x44, 0x44 }));
        }

        private void ExpectMergedImageFiles()
        {
            fileSystem.AddFile(string.Format(@"{0}\{1}.IM1", targetDirectory, BatchNumber), new MockFileData(new byte[] { 0x41, 0x43, 0x43, 0x43 }));
            fileSystem.AddFile(string.Format(@"{0}\{1}.IM2", targetDirectory, BatchNumber), new MockFileData(new byte[] { 0x42, 0x42, 0x44, 0x44, 0x44, 0x44 }));
        }

        [SuppressMessage("Microsoft.StyleCop.CSharp.ReadabilityRules", "SA1118:ParameterMustNotSpanMultipleLines", Justification = "Test Data")]
        private void ExpectImageMetadataFile()
        {

            fileSystem.AddFile(
                string.Format(@"{0}\IM{1}.xml", targetDirectory, BatchNumber),
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                +"<ImageMergeBatch xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"
                + "  <BatchNumber>20022015</BatchNumber>"
                + "  <Vouchers>"
                + "    <ImageMergeVoucher>"
                + "      <TraceNumber>000000000001</TraceNumber>"
                + "      <FrontOffset>0</FrontOffset>"
                + "      <FrontLength>1</FrontLength>"
                + "      <RearOffset>0</RearOffset>"
                + "      <RearLength>2</RearLength>"
                + "    </ImageMergeVoucher>"
                + "    <ImageMergeVoucher>"
                + "      <TraceNumber>000000000002</TraceNumber>"
                + "      <FrontOffset>1</FrontOffset>"
                + "      <FrontLength>3</FrontLength>"
                + "      <RearOffset>2</RearOffset>"
                + "      <RearLength>4</RearLength>"
                + "    </ImageMergeVoucher>"
                + "  </Vouchers>"
                + "</ImageMergeBatch>");
        }

        private void ExpectDirectory(string directoryName)
        {
            fileSystem.AddDirectory(directoryName);
        }

        private ImageMergeHelper CreateHelper()
        {
            return new ImageMergeHelper(fileSystem, adapterConfig);
        }
    }
}
