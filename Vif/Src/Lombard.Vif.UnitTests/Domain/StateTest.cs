using System;
using System.Globalization;
using Lombard.Vif.Service.Domain;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Lombard.Vif.UnitTests.Domain
{
    [TestClass]
    public class StateUnitTest
    {
        [TestMethod]
        [ExpectedException(typeof(ArgumentException))]
        public void State_TryParseAnInvalidString_ShouldReturnError()
        {
            string invalidNSWString = "nsw";
            var state = State.Parse(invalidNSWString.ToString(CultureInfo.InvariantCulture));

            Assert.AreEqual(state.Value, "NSW");

        }
        [TestMethod]
        public void State_TryParseValiStateCodeString_ShouldReturnCorrespondingStateName()
        {
            string validNSWString = "2";
            var state = State.Parse(validNSWString.ToString(CultureInfo.InvariantCulture));

            Assert.AreEqual(state.Value, "NSW");

        }
    }
}
