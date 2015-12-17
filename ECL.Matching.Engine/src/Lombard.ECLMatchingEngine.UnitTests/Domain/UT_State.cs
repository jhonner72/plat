namespace Lombard.ECLMatchingEngine.UnitTests.Domain
{
    using System;
    using System.Globalization;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Lombard.ECLMatchingEngine.Service.Domain;

    [TestClass]
    public class UT_State
    {
        [TestMethod]
        [ExpectedException(typeof(ArgumentException))]
        public void State_GivenAnInvalidString_ShouldReturnError()
        {
            string invalidNSWString = "2";
            var state = State.Parse(invalidNSWString.ToString(CultureInfo.InvariantCulture));

            Assert.AreEqual(state.Value, "NSW");

        }
        [TestMethod]
        public void State_GivenAValiStateCodeString_ShouldReturnCorrespondingStateName()
        {
            string validNSWString = "NSW";
            var state = State.Parse(validNSWString.ToString(CultureInfo.InvariantCulture));

            Assert.AreEqual(state.Value, "2");

        }
    }
}
