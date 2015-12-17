using System.Collections.Generic;
using System.IO;
using System.IO.Abstractions;
using System.IO.Abstractions.TestingHelpers;
using System.Linq;
using Emc.Documentum.FS.DataModel.Core;
using Emc.Documentum.FS.DataModel.Core.Content;
using Emc.Documentum.FS.DataModel.Core.Profiles;
using Emc.Documentum.FS.DataModel.Core.Query;
using Lombard.Common.Configuration;
using Lombard.Documentum.Data;
using Lombard.Documentum.Data.Constants;
using Lombard.Documentum.Data.Repository;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;

namespace Lombard.Documentum.UnitTests.Data.Repository
{
    [TestClass]
    public class DataObjectRepositoryTests
    {
        private const string FakeResourceLocation = "SomeFolder/SomeFile.txt";

        private Mock<IDfsContext> context;
        private Mock<IDfsConfiguration> configuration;
        private Mock<IFileSystem> fileSystem;

        [TestInitialize]
        public void TestInitialize()
        {
            context = new Mock<IDfsContext>();
            configuration = new Mock<IDfsConfiguration>();
            fileSystem = new Mock<IFileSystem>();
        }

        [TestMethod]
        public void WhenCheckout_ThenEverythingIsOk()
        {
            string objectId = "aaa";
            var dp = new DataPackage(CreateDataObject(objectId));

            ExpectConfiguration(repository: true);
            ExpectVersionControlServiceToCheckout(objectId, dp);

            var sut = CreateDataObjectRepository();

            var result = sut.Checkout(objectId);

            Assert.AreEqual(dp.DataObjects[0], result);

            context.VerifyAll();
            configuration.VerifyAll();
        }

        [TestMethod]
        public void WhenCancelCheckout_ThenEveryhtingIsOk()
        {
            string objectId = "aaa";

            ExpectConfiguration(repository: true);
            ExpectVersionControlServiceToCancelCheckout(objectId);

            var sut = CreateDataObjectRepository();

            sut.CancelCheckout(objectId);

            context.VerifyAll();
            configuration.VerifyAll();
        }

        [TestMethod]
        public void WhenCheckin_ThenEverythingIsOk()
        {
            string objectId = "aaa";

            var dataObject = CreateDataObject(objectId);

            ExpectVersionControlServiceToCheckin(dataObject);

            var sut = CreateDataObjectRepository();

            sut.Checkin(dataObject);

            context.VerifyAll();
        }

        [TestMethod]
        public void WhenUpdate_ThenEverythingIsOk()
        {
            var dataObject = CreateDataObject("aaa");
            var dp = new DataPackage(new DataObject());

            ExpectObjectServiceToUpdate(dataObject, dp);

            var sut = CreateDataObjectRepository();

            var result = sut.Update(dataObject);

            Assert.AreEqual(dp, result);

            context.VerifyAll();
        }

        [TestMethod]
        public void WhenGetFileInfo_ThenEverythingIsOk()
        {
            var dp = new DataPackage(new DataObject { Contents = new List<Content> { new BinaryContent(new byte[] { 0x61, 0x61, 0x61 }, "crtext") } });

            ExpectConfiguration(repository: true);
            ExpectObjectServiceToGetDataPackageWithContent(FakeResourceLocation, dp);

            var sut = CreateDataObjectRepository();

            var result = sut.GetFileInfo(FakeResourceLocation);

            Assert.AreEqual(3, result.Length);

            configuration.VerifyAll();
            context.VerifyAll();
        }

        [TestMethod]
        public void GivenNoContent_WhenGetFileInfo_ThenEverythingIsOk()
        {
            var dp = new DataPackage(new DataObject { Contents = null });

            ExpectConfiguration(repository: true);
            ExpectObjectServiceToGetDataPackageWithContent(FakeResourceLocation, dp);

            var sut = CreateDataObjectRepository();

            var result = sut.GetFileInfo(FakeResourceLocation);

            Assert.IsNull(result);

            configuration.VerifyAll();
            context.VerifyAll();
        }

        [TestMethod]
        public void WhenGetResourceContents_ThenEverythingIsOk()
        {
            var dp = new DataPackage(new DataObject { Contents = new List<Content> { new BinaryContent(new byte[] { 0x61, 0x61, 0x61 }, "crtext") } });

            ExpectConfiguration(repository: true);
            ExpectObjectServiceToGetDataPackageWithContent(FakeResourceLocation, dp);

            var sut = CreateDataObjectRepository();

            var result = sut.GetResourceContents(FakeResourceLocation);

            Assert.AreEqual("aaa", result);

            configuration.VerifyAll();
            context.VerifyAll();
        }

        [TestMethod]
        public void GivenNoContent_WhenGetResourceContents_ThenEverythingIsOk()
        {
            var dp = new DataPackage(new DataObject { Contents = null });

            ExpectConfiguration(repository: true);
            ExpectObjectServiceToGetDataPackageWithContent(FakeResourceLocation, dp);

            var sut = CreateDataObjectRepository();

            var result = sut.GetResourceContents(FakeResourceLocation);

            Assert.AreEqual(null, result);

            configuration.VerifyAll();
            context.VerifyAll();
        }

        [TestMethod]
        public void WhenGetDataObjectByObjectId_ThenEverythingIsOk()
        {
            var objectId = "aaa";
            var dp = new DataPackage(CreateDataObject(objectId));

            ExpectConfiguration(repository: true);
            ExpectObjectServiceToGetDataPackage(objectId, dp);

            var sut = CreateDataObjectRepository();

            var result = sut.GetDataObjectByObjectId(objectId);

            Assert.AreEqual(result, dp.DataObjects[0]);

            configuration.VerifyAll();
            context.VerifyAll();
        }

        [TestMethod]
        public void WhenGetDataObject_ThenEverythingIsOk()
        {
            var dp = new DataPackage(CreateDataObject(FakeResourceLocation));

            ExpectConfiguration(repository: true);
            ExpectObjectServiceToGetDataPackage(FakeResourceLocation, dp);

            var sut = CreateDataObjectRepository();

            var result = sut.GetDataObject(FakeResourceLocation);

            Assert.AreEqual(dp.DataObjects[0], result);

            configuration.VerifyAll();
            context.VerifyAll();
        }

        [TestMethod]
        public void WhenGetDataObjectWithContents_ThenEverythingIsOk()
        {
            var dp = new DataPackage(new DataObject { Contents = new List<Content> { new BinaryContent(new byte[] { 0x61, 0x61, 0x61 }, "crtext") } });

            ExpectConfiguration(repository: true);
            ExpectObjectServiceToGetDataPackageWithContent(FakeResourceLocation, dp);

            var sut = CreateDataObjectRepository();

            var result = sut.GetDataObjectWithContents(FakeResourceLocation);

            Assert.AreEqual(dp.DataObjects[0], result);

            configuration.VerifyAll();
            context.VerifyAll();
        }

        [TestMethod]
        public void WhenGetDataObjectWithContentsObjectId_ThenEverythingIsOk()
        {
            var objectId = "aaa";
            var dp = new DataPackage(new DataObject { Contents = new List<Content> { new BinaryContent(new byte[] { 0x61, 0x61, 0x61 }, "crtext") } });

            ExpectConfiguration(repository: true);
            ExpectObjectServiceToGetDataPackageWithContent(objectId, dp);

            var sut = CreateDataObjectRepository();

            var result = sut.GetDataObjectWithContents(objectId);

            Assert.AreEqual(dp.DataObjects[0], result);

            configuration.VerifyAll();
            context.VerifyAll();
        }

        [TestMethod]
        public void WhenFindDataObjectsWithContent_ThenEverythingIsOk()
        {
            string objecType = "xxx";
            string objectId = "aaa";

            var criteria = new List<PropertyExpression>
            {
                new PropertyExpression("a", Condition.EQUAL, "b")
            };

            var dataObject = CreateDataObject(objectId);

            dataObject.Contents = new List<Content> { new BinaryContent(new byte[] { 0x61, 0x61, 0x61 }, "crtext") };
            var dp = new DataPackage(dataObject);

            var results = new QueryResult(dp)
            {
                QueryStatus = new QueryStatus()
                {
                    IsCompleted = true,
                    RepositoryStatusInfos = new List<RepositoryStatusInfo> { new RepositoryStatusInfo() { Status = Status.SUCCESS } }
                },
            };

            ExpectConfiguration(repository: true, maxQuery: true);
            ExpectObjectServiceToGetDataPackageWithContent(objectId, dp);
            ExpectSearchServiceToReturnResults(results);

            var sut = CreateDataObjectRepository();

            var result = sut.FindDataObjects(objecType, criteria, true);

            Assert.AreEqual(1, result.Count);
            Assert.IsNotNull(result.First().Contents);

            configuration.VerifyAll();
            context.VerifyAll();
        }

        [TestMethod]
        public void WhenFindDataObjectsWithoutContent_ThenEverythingIsOk()
        {
            string objecType = "xxx";
            string objectId = "aaa";

            var criteria = new List<PropertyExpression>
            {
                new PropertyExpression("a", Condition.EQUAL, "b")
            };

            var dataObject = CreateDataObject(objectId);

            var dp = new DataPackage(dataObject);

            var results = new QueryResult(dp)
            {
                QueryStatus = new QueryStatus()
                {
                    IsCompleted = true,
                    RepositoryStatusInfos = new List<RepositoryStatusInfo> { new RepositoryStatusInfo() { Status = Status.SUCCESS } }
                },
            };

            ExpectConfiguration(repository: true, maxQuery: true);
            ExpectObjectServiceToGetDataPackage(objectId, dp);
            ExpectSearchServiceToReturnResults(results);

            var sut = CreateDataObjectRepository();

            var result = sut.FindDataObjects(objecType, criteria, false);

            Assert.AreEqual(1, result.Count);
            Assert.IsFalse(result.First().Contents.Any());

            configuration.VerifyAll();
            context.VerifyAll();
        }

        [TestMethod]
        public void WhenFindDataObjects_AndSearchFails_ThenReturnsNull()
        {
            string objecType = "xxx";

            var criteria = new List<PropertyExpression>
            {
                new PropertyExpression("a", Condition.EQUAL, "b")
            };

            var results = new QueryResult()
            {
                QueryStatus = new QueryStatus()
                {
                    IsCompleted = true,
                    RepositoryStatusInfos = new List<RepositoryStatusInfo> { new RepositoryStatusInfo() { Status = Status.FAILURE } }
                },
            };

            ExpectConfiguration(repository: true, maxQuery: true);
            ExpectSearchServiceToReturnResults(results);

            var sut = CreateDataObjectRepository();

            var result = sut.FindDataObjects(objecType, criteria, false);

            Assert.IsNull(result);

            context.VerifyAll();
        }

        [TestMethod]
        public void WhenCreateDataObjectFromFile_ThenEverythingIsOk()
        {
            var objectType = "xxx";
            string filename = @"someDrive:\someFile";
            string destinationFolder = "someFolder";

            var properties = new Dictionary<string, string>
            {
                {"a", "b"},
                {DocumentumAttributes.ObjectName, "objName"}
            };

            ExpectFileExists(filename, true);
            ExpectConfiguration(repository: true);
            ExpectObjectServiceToCreateObjectInFolder(destinationFolder, properties);

            var sut = CreateDataObjectRepository();

            var result = sut.CreateDataObject(destinationFolder, filename, objectType, "aaa", properties);

            Assert.AreEqual(objectType, result.Type);

            context.VerifyAll();
            configuration.VerifyAll();
            fileSystem.VerifyAll();
        }

        [TestMethod]
        public void WhenCreateParentChildRelationship_ThenEverythingIOk()
        {
            var parent = CreateDataObject("parent");
            var child = CreateDataObject("child");

            ExpectConfiguration(repository: true);
            ExpectObjectServiceToCreateRelationship(parent, child, DocumentumTypes.DishonourVoucherRelation);

            var sut = CreateDataObjectRepository();

            sut.CreateParentChildRelationship(parent, child, DocumentumTypes.DishonourVoucherRelation);
            context.VerifyAll();
            configuration.VerifyAll();
        }

        private void ExpectObjectServiceToCreateRelationship(DataObject parent, DataObject child, string relationshipType)
        {
            context
                .Setup(x => x.ObjectService.Create(
                    It.Is<DataPackage>(
                        dp => dp.DataObjects.Any(dataObject =>
                            dataObject.Properties.Get("relation_name").GetValueAsString() == relationshipType
                            && dataObject.Properties.Get("parent_id").GetValueAsString() == parent.Identity.GetValueAsString()
                            && dataObject.Properties.Get("child_id").GetValueAsString() == child.Identity.GetValueAsString())),
                    It.IsAny<OperationOptions>()))
                .Verifiable();
        }

        private void ExpectObjectServiceToCreateObjectInFolder(string destinationFolder, Dictionary<string, string> properties)
        {
            var firstProp = properties.First();
            context
                .Setup(x => x.ObjectService.Create(
                    It.Is<DataPackage>(
                        dp => dp.DataObjects.Any(dataObject =>
                            dataObject.Relationships.Any(a => ((ObjectPath)((ReferenceRelationship)a).Target.Value).Path == destinationFolder)
                            && dataObject.Properties.Get(firstProp.Key).GetValueAsString() == firstProp.Value
                        )),
                    It.IsAny<OperationOptions>()))
                .Verifiable();
        }

        private void ExpectFileExists(string filename, bool exists)
        {
            fileSystem.Setup(x => x.File.Exists(filename))
                .Returns(exists);
        }

        private void ExpectSearchServiceToReturnResults(QueryResult result)
        {
            context
                .Setup(x =>
                    x.SearchService.Execute(It.IsAny<Query>(), It.IsAny<QueryExecution>(), It.IsAny<OperationOptions>()))
                .Returns(result);
        }

        private DataObject CreateDataObject(string objectId)
        {
            return new DataObject(new ObjectIdentity(objectId, "repo"));
        }

        private void ExpectObjectServiceToGetDataPackageWithContent(string objectId, DataPackage dp)
        {
            context
                .Setup(x => x.ObjectService.Get(
                    It.Is<ObjectIdentitySet>(
                        y => y.Identities.Any(
                            z => z.RepositoryName == "Repository" && z.Value.ToString() == objectId)),
                    It.Is<OperationOptions>(oo =>
                        oo.ContentProfile.FormatFilter == FormatFilter.ANY
                        && oo.ContentProfile.UrlReturnPolicy == UrlReturnPolicy.PREFER)))
                .Returns(dp);
        }

        private void ExpectObjectServiceToGetDataPackage(string objectId, DataPackage dp)
        {
            context
                .Setup(x => x.ObjectService.Get(
                    It.Is<ObjectIdentitySet>(
                        y => y.Identities.Any(
                            z => z.RepositoryName == "Repository" && z.Value.ToString() == objectId)),
                    It.IsAny<OperationOptions>()))
                .Returns(dp);
        }

        private void ExpectObjectServiceToUpdate(DataObject dataObject, DataPackage dp)
        {
            context
                .Setup(x => x.ObjectService.Update(
                    It.Is<DataPackage>(y => y.DataObjects.Any(z => z == dataObject)),
                    It.IsAny<OperationOptions>()))
                .Returns(dp);
        }

        private void ExpectVersionControlServiceToCheckin(DataObject dataObject)
        {
            context
                .Setup(x => x.VersionControlService.Checkin(
                    It.Is<DataPackage>(y => y.DataObjects.Any(z => z == dataObject)),
                    VersionStrategy.NEXT_MINOR,
                    false,
                    It.Is<List<string>>(y => y.Contains("CURRENT")),
                    It.IsAny<OperationOptions>()))
                .Verifiable();
        }

        private void ExpectConfiguration(bool repository = false, bool maxQuery = false)
        {
            if (repository)
            {
                configuration.Setup(x => x.Repository)
                    .Returns("Repository");
            }

            if (maxQuery)
            {
                configuration.Setup(x => x.MaxQueryResults)
                    .Returns(10);
            }
        }

        private void ExpectVersionControlServiceToCheckout(string objectId, DataPackage dataPackage)
        {
            context
                .Setup(x =>
                    x.VersionControlService.Checkout(
                        It.Is<ObjectIdentitySet>(
                            y => y.Identities.Any(
                                z => z.RepositoryName == "Repository" && z.Value.ToString() == objectId)),
                        It.IsAny<OperationOptions>()))
                .Returns(dataPackage);
        }

        private void ExpectVersionControlServiceToCancelCheckout(string objectId)
        {
            context
                .Setup(x => x.VersionControlService.CancelCheckout(
                    It.Is<ObjectIdentitySet>(
                        y => y.Identities.Any(
                            z => z.RepositoryName == "Repository" && z.Value.ToString() == objectId))))
                .Verifiable();
        }

        private DataObjectRepository CreateDataObjectRepository()
        {
            return new DataObjectRepository(context.Object, configuration.Object, fileSystem.Object);
        }
    }
}
