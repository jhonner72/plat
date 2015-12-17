using System;
using System.Linq;
using System.Xml.Linq;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Lombard.ImageExchange.Nab.UnitTests.OutboundService.Mappers
{
    public static class AssertEx
    {
        public static T AssertException<T>(Action action)
            where T : Exception
        {
            try
            {
                action();
            }
            catch (T e)
            {
                return e;
            }
            catch
            {
                throw new AssertFailedException();
            }

            throw new AssertFailedException();
        }

        public static void AssertRootIs(this XDocument document, string rootName)
        {
            Assert.IsNotNull(document);
            Assert.IsNotNull(document.Root);
            Assert.AreEqual(rootName, document.Root.Name);
        }

        public static void AssertRootHas(this XDocument document, string elementName)
        {
            Assert.IsNotNull(document);
            Assert.IsNotNull(document.Root);

            var element = document.Root.Element(elementName);
            Assert.IsNotNull(element);
        }

        public static void AssertRootHas(this XDocument document, string elementName, int number)
        {
            Assert.IsNotNull(document);
            Assert.IsNotNull(document.Root);

            var element = document.Root.Elements(elementName);
            Assert.IsNotNull(element);

            Assert.AreEqual(number, element.Count());
        }
    }
}
