using System;
using System.Collections.Generic;
using System.IO.Abstractions;
using System.IO.Abstractions.TestingHelpers;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Emc.Documentum.FS.DataModel.Core;
using Emc.Documentum.FS.DataModel.Core.Properties;
using Lombard.Common.Configuration;
using Lombard.Common.Domain;
using Lombard.Documentum.Data.Constants;
using Lombard.Documentum.Data.Repository;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using Ploeh.AutoFixture;

namespace Lombard.Documentum.UnitTests.Data.Repository
{
    [TestClass]
    public class DishonourLetterRepositoryTests
    {
        private const string LetterPath = "/letterPath";

        private static readonly Fixture Fixture = new Fixture();
        private Mock<IDataObjectRepository> objectRepository;
        private Mock<IDfsConfiguration> dfsConfiguration;
        private Mock<IVoucherRepository> voucherRepository;

        [TestInitialize]
        public void TestInitialize()
        {
            objectRepository = new Mock<IDataObjectRepository>();
            dfsConfiguration = new Mock<IDfsConfiguration>();
            voucherRepository = new Mock<IVoucherRepository>();
        }

        [TestMethod]
        public async Task WhenCreateLetter_ThenEverythingIsOk()
        {
            var processingDate = new DateTime(2015, 12, 25);

            var voucher = Fixture.Create<Voucher>();

            voucher.AccountNumber = "AccountNumber";
            voucher.Bsb = "Bsb";
            voucher.AuxiliaryDomestic = "AuxDom";
            voucher.Amount = "1550";
            voucher.ProcessingDate = processingDate;

            var expectedFolder = string.Format("{0}/{1:yyyy}/{1:MM}/{1:dd}", LetterPath, processingDate);
            
            var expectedFileName =  DishonourLetter.GetFileName(                
                voucher.AuxiliaryDomestic,
                voucher.Bsb,
                voucher.AccountNumber,
                voucher.Amount,
                voucher.ProcessingDate);

            var expectedPath = string.Format("{0}/{1}", expectedFolder, expectedFileName);

            var properties = new Dictionary<string, string>
            {
                {DocumentumAttributes.ObjectName, expectedFileName},
                {DocumentumAttributes.Title, expectedFileName},
                {DishonourLetterAttributes.DishonourStatus, DishonourLetterStatus.LetterRaised.Value}
            };

            var voucherObject = new DataObject();
            var letterObject = new DataObject();

            ExpectConfig(letterPath:true, timeWindowDays:true);
            ExpectObjectRepositoryToCreateLetterWithProperties(expectedFolder, expectedFileName, properties);
            ExpectVoucherRepoToGetVoucher(voucher);
            ExpectObjectRepositoryToGetObjectByObjectId(voucher.Id, voucherObject);
            ExpectObjectRepositoryToGetObjectByPath(expectedPath, letterObject);
            ExpectObjectRepositoryToCreateRelationship(voucherObject, letterObject, DocumentumTypes.DishonourVoucherRelation);
            
            var sut = CreateRepository();

            var result = await sut.CreateLetterForVoucher(voucher, processingDate);

            Assert.AreEqual(expectedFileName, result.Name);

            dfsConfiguration.VerifyAll();
            objectRepository.VerifyAll();
            voucherRepository.VerifyAll();

        }

        [TestMethod]
        [ExpectedException(typeof(ArgumentException))]
        public async Task GivenVoucherDoesNotExist_WhenCreateLetter_ThenThrow()
        {
            var processingDate = new DateTime(2015, 12, 25);

            var voucher = Fixture.Create<Voucher>();

            voucher.AccountNumber = "AccountNumber";
            voucher.Bsb = "Bsb";
            voucher.AuxiliaryDomestic = "AuxDom";
            voucher.Amount = "1550";
            voucher.ProcessingDate = processingDate;

            var expectedFolder = string.Format("{0}/{1:yyyy}/{1:MM}/{1:dd}", LetterPath, processingDate);

            var expectedFileName = DishonourLetter.GetFileName(
                voucher.AuxiliaryDomestic,
                voucher.Bsb,
                voucher.AccountNumber,
                voucher.Amount,
                voucher.ProcessingDate);

            var properties = new Dictionary<string, string>
            {
                {DocumentumAttributes.ObjectName, expectedFileName},
                {DocumentumAttributes.Title, expectedFileName},
                {DishonourLetterAttributes.DishonourStatus, DishonourLetterStatus.LetterRaised.Value}
            };

            ExpectConfig(letterPath: true, timeWindowDays: true);
            ExpectObjectRepositoryToCreateLetterWithProperties(expectedFolder, expectedFileName, properties);
            ExpectVoucherRepoToGetVoucher(voucher);
            //cant find voucher data object
            ExpectObjectRepositoryNotToGetObjectByObjectId(voucher.Id);

            var sut = CreateRepository();

            await sut.CreateLetterForVoucher(voucher, processingDate);
        }

        [TestMethod]
        [ExpectedException(typeof(ArgumentException))]
        public async Task GivenLetterDoesNotExist_WhenCreateLetter_ThenThrow()
        {
            var processingDate = new DateTime(2015, 12, 25);

            var voucher = Fixture.Create<Voucher>();

            voucher.AccountNumber = "AccountNumber";
            voucher.Bsb = "Bsb";
            voucher.AuxiliaryDomestic = "AuxDom";
            voucher.Amount = "1550";
            voucher.ProcessingDate = processingDate;

            var expectedFolder = string.Format("{0}/{1:yyyy}/{1:MM}/{1:dd}", LetterPath, processingDate);

            var expectedFileName = DishonourLetter.GetFileName(
                voucher.AuxiliaryDomestic,
                voucher.Bsb,
                voucher.AccountNumber,
                voucher.Amount,
                voucher.ProcessingDate);

            var expectedPath = string.Format("{0}/{1}", expectedFolder, expectedFileName);

            var properties = new Dictionary<string, string>
            {
                {DocumentumAttributes.ObjectName, expectedFileName},
                {DocumentumAttributes.Title, expectedFileName},
                {DishonourLetterAttributes.DishonourStatus, DishonourLetterStatus.LetterRaised.Value}
            };

            var voucherObject = new DataObject();

            ExpectConfig(letterPath: true, timeWindowDays: true);
            ExpectObjectRepositoryToCreateLetterWithProperties(expectedFolder, expectedFileName, properties);
            ExpectVoucherRepoToGetVoucher(voucher);
            ExpectObjectRepositoryToGetObjectByObjectId(voucher.Id, voucherObject);
            //cant find letter data object
            ExpectObjectRepositoryNotToGetObjectByPath(expectedPath);

            var sut = CreateRepository();

            await sut.CreateLetterForVoucher(voucher, processingDate);
        }

        [TestMethod]
        public async Task WhenGet_ThenEverythingIsOk()
        {
            var fileName = "xxx";
            var processingDate = new DateTime(2015, 12, 25);

            var letterObject = new DataObject()
            {
                Properties = new PropertySet(
                    new StringProperty(DocumentumAttributes.ObjectName, fileName),
                    new StringProperty(DishonourLetterAttributes.DishonourStatus,
                        DishonourLetterStatus.LetterIssued.Value))
            };

            var expectedPath = string.Format("{0}/{1:yyyy}/{1:MM}/{1:dd}/{2}", LetterPath, processingDate, fileName);

            ExpectConfig(letterPath: true);
            ExpectObjectRepositoryToGetObjectByPath(expectedPath, letterObject);

            var sut = CreateRepository();

            var result = await sut.Get(fileName, processingDate);

            Assert.AreEqual(fileName, result.Name);
            Assert.AreEqual(processingDate, result.ProcessingDate);
            Assert.AreEqual(DishonourLetterStatus.LetterIssued, result.Status);

            dfsConfiguration.VerifyAll();
            objectRepository.VerifyAll();
        }

        [TestMethod]
        public async Task WhenUpdate_ThenEverythingIsOk()
        {
            var fileName = "xxx";
            var processingDate = new DateTime(2015, 12, 25);

            var letterObject = new DataObject()
            {
                Properties = new PropertySet(
                    new StringProperty(DocumentumAttributes.ObjectName, fileName),
                    new StringProperty(DishonourLetterAttributes.DishonourStatus,
                        DishonourLetterStatus.LetterRaised.Value))
            };

            var expectedPath = string.Format("{0}/{1:yyyy}/{1:MM}/{1:dd}/{2}", LetterPath, processingDate, fileName);

            var letter = new DishonourLetter(fileName, DishonourLetterStatus.LetterIssued, processingDate)
            {
                FileInfo = GetMockFileInfo("aaa")
            };

            ExpectConfig(letterPath: true);
            ExpectObjectRepositoryToGetObjectByPath(expectedPath, letterObject);
            ExpectObjectRepositoryToUpdateWithContent(letterObject);

            var sut = CreateRepository();

            await sut.Update(letter, true);

            Assert.AreEqual(3, letterObject.Contents[0].GetAsByteArray().Length);
            Assert.AreEqual(DocumentumFormats.Pdf, letterObject.Contents[0].Format);

            dfsConfiguration.VerifyAll();
            objectRepository.VerifyAll();
        }

        [TestMethod]
        [ExpectedException(typeof(InvalidOperationException))]
        public async Task WhenUpdate_AndBufferIsTooBig_ThenThrow()
        {
            var fileName = "xxx";
            var processingDate = new DateTime(2015, 12, 25);

            var letterObject = new DataObject()
            {
                Properties = new PropertySet(
                    new StringProperty(DocumentumAttributes.ObjectName, fileName),
                    new StringProperty(DishonourLetterAttributes.DishonourStatus,
                        DishonourLetterStatus.LetterRaised.Value))
            };

            var expectedPath = string.Format("{0}/{1:yyyy}/{1:MM}/{1:dd}/{2}", LetterPath, processingDate, fileName);

            var letter = new DishonourLetter(fileName, DishonourLetterStatus.LetterIssued, processingDate)
            {
                FileInfo = GetMockFileInfo(new string(new char[1048577]))
            };

            ExpectConfig(letterPath: true);
            ExpectObjectRepositoryToGetObjectByPath(expectedPath, letterObject);
            ExpectObjectRepositoryToUpdateWithContent(letterObject);

            var sut = CreateRepository();

            await sut.Update(letter, true);
        }

        private void ExpectObjectRepositoryToUpdateWithContent(DataObject dataObject)
        {
            objectRepository
                .Setup(x => x.Update(dataObject))
                .Verifiable();
        }

        private void ExpectObjectRepositoryToCreateRelationship(DataObject parentObject, DataObject childObject, string relationshipType)
        {
            objectRepository
                .Setup(x => x.CreateParentChildRelationship(parentObject, childObject, relationshipType))
                .Verifiable();
        }

        private DishonourLetterRepository CreateRepository()
        {
            return new DishonourLetterRepository(objectRepository.Object, dfsConfiguration.Object, voucherRepository.Object);
        }

        private void ExpectObjectRepositoryNotToGetObjectByPath(string path)
        {
            objectRepository
                .Setup(x => x.GetDataObject(path))
                .Returns<DataObject>(null)
                .Verifiable();
        }

        private void ExpectObjectRepositoryToGetObjectByPath(string path, DataObject dataObject)
        {
            objectRepository
                .Setup(x => x.GetDataObject(path))
                .Returns(dataObject)
                .Verifiable();
        }

        private void ExpectObjectRepositoryNotToGetObjectByObjectId(string id)
        {
            objectRepository
                .Setup(x => x.GetDataObjectByObjectId(id))
                .Returns<DataObject>(null)
                .Verifiable();
        }

        private void ExpectObjectRepositoryToGetObjectByObjectId(string id, DataObject dataObject)
        {
            objectRepository
                .Setup(x => x.GetDataObjectByObjectId(id))
                .Returns(dataObject)
                .Verifiable();
        }

        private void ExpectVoucherRepoToGetVoucher(Voucher voucher)
        {
            voucherRepository.Setup(x => x.GetVoucher(
                    voucher.AuxiliaryDomestic,
                    voucher.Bsb,
                    voucher.AccountNumber,
                    voucher.Amount,
                    false,
                    It.IsAny<DateTime>()))
                .Returns(voucher)
                .Verifiable();
        }

        private void ExpectObjectRepositoryToCreateLetterWithProperties(string path, string objectName, Dictionary<string, string> properties)
        {
            objectRepository
                .Setup(
                    x => x.CreateDataObject(
                        path,
                        objectName,
                        DocumentumTypes.FxaDishonourLetter,
                        DocumentumFormats.Pdf,
                        It.Is<Dictionary<string, string>>(y => 
                            y.Count == 3
                            && y[DocumentumAttributes.ObjectName] == properties[DocumentumAttributes.ObjectName]
                            && y[DocumentumAttributes.Title] == properties[DocumentumAttributes.Title]
                            && y[DishonourLetterAttributes.DishonourStatus] == properties[DishonourLetterAttributes.DishonourStatus]
                        ),
                        false
                    ))
                .Verifiable();
        }

        private void ExpectConfig(bool letterPath = false, bool timeWindowDays = false)
        {
            if (letterPath)
            {
                dfsConfiguration.Setup(x => x.DishonourLetterPath)
                    .Returns(LetterPath)
                    .Verifiable();
            }

            if (timeWindowDays)
            {
                dfsConfiguration.Setup(x => x.DishonourTimeWindowDays)
                    .Returns(14)
                    .Verifiable();
            }
        }

        private FileInfoBase GetMockFileInfo(string data)
        {
            var mockFileSystem = new MockFileSystem(new Dictionary<string, MockFileData>
            {
                { @"someDir:\someFile.txt", new MockFileData(data) }
            });

            return mockFileSystem.FileInfo.FromFileName(@"someDir:\someFile.txt");
        }
    }
}
