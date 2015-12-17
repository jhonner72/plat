using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Collections.Generic;
using System.Linq;

namespace BpmActivityExtractor.UnitTests
{
    [TestClass]
    public class BpmActivityTest
    {
        private BpmActivity expected;

        [TestInitialize]
        public void TestInitialize()
        {
            expected = new BpmActivity()
            {
                BusinessKey = Guid.NewGuid().ToString(),
                EndTime = DateTime.Today,
                StartTime = DateTime.Today.AddDays(-1),
                Id = Guid.NewGuid().ToString(),
                Name = Guid.NewGuid().ToString(),
                ProcessInstanceId = Guid.NewGuid().ToString(),
                ProcessDefinitionId = Guid.NewGuid().ToString(),
                State = 1,
                Duration="2"
            };
        }

        [TestMethod]
        public void Given2BpmActivity_WhenBothObjectAreEqualInValue_ThenReturnTrue()
        {
            var actual = new BpmActivity()
            {
                BusinessKey = expected.BusinessKey,
                EndTime = expected.EndTime,
                StartTime = expected.StartTime,
                Id = expected.Id,
                Name = expected.Name,
                ProcessInstanceId = expected.ProcessInstanceId,
                ProcessDefinitionId = expected.ProcessDefinitionId,
                State = expected.State,
                Duration=expected.Duration
            };

            Assert.IsTrue(expected.Equals(actual));
        }

        [TestMethod]
        public void GivenAListAndBpmActivity_WhenObjectWithEqualValueIsInList_ThenReturnTrue()
        {
            var list = new List<BpmActivity>() { expected };

            var actual = new BpmActivity()
            {
                BusinessKey = expected.BusinessKey,
                EndTime = expected.EndTime,
                StartTime = expected.StartTime,
                Id = expected.Id,
                Name = expected.Name,
                ProcessInstanceId = expected.ProcessInstanceId,
                ProcessDefinitionId = expected.ProcessDefinitionId,
                State = expected.State,
                Duration = expected.Duration
            };

            Assert.IsTrue(list.Contains(actual));
        }
    }
}
