using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.IntegrationTests.Hooks;
using Lombard.Adapters.DipsAdapter.Messages;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Collections.Generic;
using System.Threading;
using System.Linq;
using TechTalk.SpecFlow;
using TechTalk.SpecFlow.Assist;

namespace Lombard.Adapters.DipsAdapter.IntegrationTests.Steps
{
    [Binding]
    public class CorrectCodelineSteps
    {
        [Given(@"there are no CorrectCodeline rows for batch number (.*)")]
        public void Given1(string batchNumber)
        {
            var batch = batchNumber.PadRight(8, ' ');
            using (var context = CorrectCodelineBus.CreateContext())
            {
                context.Database.ExecuteSqlCommand("DELETE FROM [Queue] WHERE [S_BATCH] = @p0", batch);
                context.Database.ExecuteSqlCommand("DELETE FROM [NabChq] WHERE [S_BATCH] = @p0", batch);
                context.Database.ExecuteSqlCommand("DELETE FROM [DB_INDEX] WHERE [BATCH] = @p0", batch);
            }
        }

        private readonly CorrectBatchCodelineRequest message = new CorrectBatchCodelineRequest();

        [Given(@"a CorrectBatchCodelineRequest with batch number (.*) is added to the queue for the following CorrectCodeline vouchers:")]
        public void Given2(string batchNumber, Table table)
        {
            var vouchers = table.CreateSet<CorrectCodelineRequest>();

            message.voucher = vouchers.ToArray();
        }

        [Given(@"a CorrectBatchCodelineRequest with batch number (.*) contains this voucher batch:")]
        public void Given3(string batchNumber, Table table)
        {
            var vouchers = table.CreateInstance<VoucherBatch>();

            message.voucherBatch = vouchers;
        }

        [When(@"the message is published to the queue and CorrectBatchCodelineRequest process the message with this information:")]
        public void When1(Table table)
        {
            var publishInfo = table.CreateInstance<PublishInformation>();

            message.jobIdentifier = publishInfo.CorrelationId;

            // synchronous Publish will wait until confirmed publish or timeout exception thrown
            CorrectCodelineBus.Publish(message, publishInfo.CorrelationId, publishInfo.RoutingKey);

            Thread.Sleep(publishInfo.PublishTimeOutSeconds * 1000);
        }

        [Then(@"a DipsQueue table row will exist with the following CorrectCodeline values")]
        public void Then1(Table table)
        {
            var expected = table.CreateInstance<DipsQueue>();

            DipsQueue actual;

            using (var context = CorrectCodelineBus.CreateContext())
            {
                actual = context.Queues.SingleOrDefault(q => q.S_BATCH == expected.S_BATCH);
            }

            Assert.IsNotNull(actual, "Could not find queue row with batch {0} in the database", expected.S_BATCH);

            TrimAllProperties(actual);

            table.CompareToInstance(actual);
        }

        [Then(@"DipsNabChq table rows will exist with the following CorrectCodeline values")]
        public void Then2(Table table)
        {
            var expected = table.CreateSet<DipsNabChq>();

            var dbRows = new List<DipsNabChq>();

            using (var context = CorrectCodelineBus.CreateContext())
            {
                foreach (var chqPod in expected)
                {
                    var actual = context.NabChqPods.SingleOrDefault(c => c.S_BATCH == chqPod.S_BATCH && c.S_TRACE == chqPod.S_TRACE);

                    Assert.IsNotNull(actual, string.Format("Could not find NabChqScanPod row with batch {0} and trace {1} in the database", chqPod.S_BATCH, chqPod.S_TRACE));

                    TrimAllProperties(actual);

                    dbRows.Add(actual);
                }
            }

            table.CompareToSet(dbRows);
        }

        [Then(@"DipsNabChq table rows will exist with the following CorrectCodeline values - long Drn")]
        public void Then2WithLongDrn(Table table)
        {
            var expected = table.CreateSet<DipsNabChq>();

            var dbRows = new List<DipsNabChq>();

            using (var context = CorrectCodelineBus.CreateContext())
            {
                foreach (var chqPod in expected)
                {
                    var actual = context.NabChqPods.SingleOrDefault(c => c.S_BATCH == chqPod.S_BATCH && c.repostFromDRN == chqPod.repostFromDRN);

                    Assert.IsNotNull(actual, string.Format("Could not find NabChqScanPod row with batch {0} and trace {1} in the database", chqPod.S_BATCH, chqPod.S_TRACE));

                    TrimAllProperties(actual);

                    dbRows.Add(actual);
                }
            }

            table.CompareToSet(dbRows);
        }

        [Then(@"DipsDbIndex table rows will exist with the following CorrectCodeline values - trace")]
        public void Then3(Table table)
        {
            var expected = table.CreateSet<DipsDbIndex>();

            var dbRows = new List<DipsDbIndex>();

            using (var context = CorrectCodelineBus.CreateContext())
            {
                foreach (var dbIndex in expected)
                {
                    var actual = context.DbIndexes.SingleOrDefault(i => i.BATCH == dbIndex.BATCH && i.TRACE == dbIndex.TRACE);

                    Assert.IsNotNull(actual, string.Format("Could not find DB_INDEX row with batch {0} and trace {1} in the database", dbIndex.BATCH, dbIndex.TRACE));

                    TrimAllProperties(actual);

                    dbRows.Add(actual);
                }
            }

            table.CompareToSet(dbRows);
        }

        [Then(@"DipsDbIndex table rows will exist with batch number (.*)")]
        public void Then3(string batchNumber)
        {
            using (var context = CorrectCodelineBus.CreateContext())
            {
                Assert.IsTrue(context.DbIndexes.Any(i => i.BATCH == batchNumber));
            }
        }

        private static void TrimAllProperties<T>(T actual)
        {
            var type = typeof(T);
            foreach (var prop in type.GetProperties())
            {
                if (prop.PropertyType == typeof(string))
                {
                    prop.SetValue(actual, (prop.GetValue(actual) ?? string.Empty).ToString().Trim());
                }
            }
        }
    }
}
