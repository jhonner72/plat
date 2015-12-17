using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Xml.Linq;
using Lombard.ImageExchange.Nab.OutboundService.Constants;
using Lombard.ImageExchange.Nab.OutboundService.Mappers;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Lombard.ImageExchange.Nab.UnitTests.OutboundService.Mappers
{
    [TestClass]
    public class CoinMetadataFieldToXmlMapperTests
    {
        private IMapper<IReadOnlyDictionary<string, string>, IEnumerable<XElement>> mapper;
        private IEnumerable<XElement> result;
        private readonly IDictionary<string, string> metadata;

        public CoinMetadataFieldToXmlMapperTests()
        {
            metadata = new Dictionary<string, string>
            {
                {"item1", "value1"},
                {"item2", "value2"},
                {"item3", "value3"}
            };
        }

        [TestInitialize]
        public void Setup()
        {
            mapper = new CoinMetadataFieldToXmlMapper();

            result = mapper.Map(new ReadOnlyDictionary<string, string>(metadata));
        }

        [TestMethod]
        public void ItReturnsNonNullElements()
        {
            Assert.IsNotNull(result);
        }

        [TestMethod]
        public void ItReturnsElementsWithTheSameLengthOfMetadata()
        {
            Assert.AreEqual(metadata.Count, result.Count());
        }

        [TestMethod]
        public void AllFieldsHasTheRFieldElementName()
        {
            Assert.IsTrue(result.All(x => x.Name == CoinElementConstants.Field));
        }

        [TestMethod]
        public void AllFieldsContainsNameAndValue()
        {
            foreach (var element in result)
            {
                var name = element.Attribute(CoinElementConstants.NameAttribute);
                Assert.IsNotNull(name);

                var value = element.Attribute(CoinElementConstants.ValueAttribute);
                Assert.IsNotNull(value);
            }
        }

        [TestMethod]
        public void AllFieldsMapNameAndValue()
        {
            foreach (var element in result)
            {
                var name = element.Attribute(CoinElementConstants.NameAttribute);
                Assert.IsTrue(metadata.ContainsKey(name.Value));

                var value = element.Attribute(CoinElementConstants.ValueAttribute);
                Assert.AreEqual(metadata[name.Value], value.Value);
            }
        }

        [TestMethod]
        [ExpectedException(typeof(ArgumentOutOfRangeException))]
        public void IfEmptyInputThenThrow()
        {
            var empty = new ReadOnlyDictionary<string, string>(new Dictionary<string, string>());
            mapper.Map(empty);
        }

        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void IfNullInputThenThrow()
        {
            mapper.Map(null);
        }
    }
}