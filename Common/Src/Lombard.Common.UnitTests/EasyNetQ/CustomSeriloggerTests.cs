using System;
using Lombard.Common.EasyNetQ;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using Serilog;

namespace Lombard.Common.UnitTests.EasyNetQ
{
    [TestClass]
    public class CustomSerilogLoggerTests
    {
        private Mock<ILogger> logger;

        [TestInitialize]
        public void TestInitialize()
        {
            logger = new Mock<ILogger>();

            Log.Logger = logger.Object;
        }

        [TestMethod]
        public void WhenDebugWrite_ThenWriteLog()
        {
            const string Format = "xyz";
            object[] args = { "a", "b" };

            var sut = CreateLogger();

            sut.DebugWrite(Format, args);

            logger.Verify(x => x.Debug(Format, args));
        }

        [TestMethod]
        public void WhenErrorWrite_ThenWriteLog()
        {
            const string Format = "xyz";
            object[] args = { "a", "b" };

            var sut = CreateLogger();

            sut.ErrorWrite(Format, args);

            logger.Verify(x => x.Error(Format, args));
        }

        [TestMethod]
        public void WhenErrorWrite_AndException_ThenWriteLog()
        {
            var exception = new Exception();

            var sut = CreateLogger();

            sut.ErrorWrite(exception);

            logger.Verify(x => x.Error(exception, It.IsAny<string>()));
        }

        [TestMethod]
        public void WhenInfoWrite_ThenWriteLog()
        {
            const string Format = "xyz";
            object[] args = { "a", "b" };

            var sut = CreateLogger();

            sut.InfoWrite(Format, args);

            logger.Verify(x => x.Information(Format, args));
        }

        private CustomSerilogLogger CreateLogger()
        {
            return new CustomSerilogLogger();
        }
    }
}
