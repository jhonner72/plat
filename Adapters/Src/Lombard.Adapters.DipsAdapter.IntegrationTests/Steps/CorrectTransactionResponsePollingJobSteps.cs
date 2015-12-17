using System.Linq;
using System.Threading;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.IntegrationTests.Hooks;
using Lombard.Adapters.DipsAdapter.Messages;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using TechTalk.SpecFlow;
using TechTalk.SpecFlow.Assist;

namespace Lombard.Adapters.DipsAdapter.IntegrationTests.Steps
{
    [Binding]
    public class CorrectTransactionResponsePollingJobSteps
    {
        [Given(@"there are Transaction Correction database rows for batch number (.*) in DipsQueue database")]
        public void Given1(string batchNumber, Table table)
        {
            var batch = batchNumber.PadRight(8, ' ');
            using (var context = CorrectTransactionBus.CreateContext())
            {
                context.Database.ExecuteSqlCommand("DELETE FROM [Queue] WHERE [S_BATCH] = @p0", batch);
                context.Database.ExecuteSqlCommand("DELETE FROM [NabChq] WHERE [S_BATCH] = @p0", batch);
                context.Database.ExecuteSqlCommand("DELETE FROM [DB_INDEX] WHERE [BATCH] = @p0", batch);

                var queue = table.CreateInstance<DipsQueue>();

                context.Queues.Add(queue);

                context.SaveChanges();
            }
        }

        [Given(@"there are Transaction Correction database rows for batch number (.*) in DipsNabChq database")]
        public void Given2(string batchNumber, Table table)
        {
            using (var context = CorrectTransactionBus.CreateContext())
            {
                var dbRows = table.CreateSet<DipsNabChq>();

                foreach (var dbRow in dbRows)
                {
                    context.NabChqPods.Add(dbRow);
                }

                context.SaveChanges();
            }
        }

        [When(@"wait for (.*) seconds until CorrectTransactionResponsePollingJob executed")]
        public void When1(int waitSeconds)
        {
            Thread.Sleep(waitSeconds * 1000);
        }

        private CorrectBatchTransactionResponse response;

        [Then(@"a CorrectBatchTransactionResponse with batch number (.*) is added to the exchange and contains these vouchers:")]
        public void Then1(string batchNumber, Table table)
        {
            var task = CorrectTransactionBus.GetSingleResponseAsync(5);
            task.Wait();

            response = task.Result;

            Assert.IsNotNull(response, "No response received");
            Assert.IsFalse(string.IsNullOrEmpty(response.voucherBatch.scannedBatchNumber));
            Assert.AreEqual(batchNumber, response.voucherBatch.scannedBatchNumber);

            table.CompareToSet(response.voucher);
        }

        [Then(@"a CorrectBatchTransactionResponse with batch number (.*) contains this voucher batch:")]
        public void Then2(string batchNumber, Table table)
        {
            table.CompareToInstance(response.voucherBatch);
        }

        [Then(@"with these correct transaction voucher values:")]
        public void Then3(Table table)
        {
            foreach (var row in table.Rows)
            {
                var transactionResponse = response.voucher.SingleOrDefault(
                    x => x.voucher.documentReferenceNumber.Equals(row["documentReferenceNumber"]));

                if (transactionResponse != null)
                {
                    var actualObject = transactionResponse.voucher;

                    foreach (var col in table.Header)
                    {
                        if (actualObject != null)
                        {
                            var prop = actualObject.GetType().GetProperty(col);

                            Assert.AreEqual(row[col], prop.GetValue(actualObject, null).ToString().Trim());
                        }
                    }
                }
            }
        }
    }
}
