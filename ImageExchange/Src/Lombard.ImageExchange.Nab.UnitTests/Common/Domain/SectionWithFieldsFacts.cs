using System.Collections.Generic;
using System.Linq;
using Lombard.ImageExchange.Nab.OutboundService.Domain;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Lombard.ImageExchange.Nab.UnitTests.Common.Domain
{
    [TestClass]
    public class SectionWithFieldsFacts
    {
        internal class ConcreteSection : SectionWithFields
        {
            public ConcreteSection(IDictionary<string, string> metadata) : base(metadata)
            {
            }
        }

        [TestMethod]
        public void ItInitializeMetadataFromDataInCtor()
        {
            var data = new Dictionary<string, string>
            {
                {"field1", "value 1"}
            };
            var section = new ConcreteSection(data);

            Assert.IsTrue(section.Metadata.ContainsKey("field1"));
            Assert.AreEqual(section.Metadata.Count(), 1);
        }
    }
}