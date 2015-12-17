using System.Linq;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.IntegrationTests.Hooks;
using System.Threading;
using Lombard.Adapters.DipsAdapter.Messages;
using TechTalk.SpecFlow;
using TechTalk.SpecFlow.Assist;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Lombard.Adapters.DipsAdapter.IntegrationTests.Steps
{
    [Binding]
    public class CorrectCodelineResponsePollingJobSteps
    {
        [Given(@"there are Codeline Correction database rows for batch number (.*) in DipsQueue database")]
        public void Given1(string batchNumber, Table table)
        {
            var batch = batchNumber.PadRight(8, ' ');
            using (var context = CorrectCodelineBus.CreateContext())
            {
                context.Database.ExecuteSqlCommand("DELETE FROM [Queue] WHERE [S_BATCH] = @p0", batch);
                context.Database.ExecuteSqlCommand("DELETE FROM [NabChq] WHERE [S_BATCH] = @p0", batch);
                context.Database.ExecuteSqlCommand("DELETE FROM [DB_INDEX] WHERE [BATCH] = @p0", batch);

                var queue = table.CreateInstance<DipsQueue>();

                context.Queues.Add(queue);

                context.SaveChanges();
            }
        }

        [Given(@"there are Codeline Correction database rows for batch number (.*) in DipsNabChq database")]
        public void Given2(string batchNumber, Table table)
        {
            using (var context = CorrectCodelineBus.CreateContext())
            {
                var dbRows = table.CreateSet<DipsNabChq>();

                foreach (var dbRow in dbRows)
                {
                    context.NabChqPods.Add(dbRow);
                    
                }

                context.SaveChanges();
            }
        }

        [When(@"wait for (.*) seconds until CorrectCodelineResponsePollingJob executed")]
        public void When1(int waitSeconds)
        {
            Thread.Sleep(waitSeconds * 1000);
        }

        private CorrectBatchCodelineResponse response;

        [Then(@"a CorrectBatchCodelineResponse with batch number (.*) is added to the exchange and contains these vouchers:")]
        public void Then1(string batchNumber, Table table)
        {
            var task = CorrectCodelineBus.GetSingleResponseAsync(5);
            task.Wait();

            response = task.Result;

            Assert.IsNotNull(response, "No response received");
            Assert.IsFalse(string.IsNullOrEmpty(response.voucherBatch.scannedBatchNumber));
            Assert.AreEqual(batchNumber, response.voucherBatch.scannedBatchNumber);

            table.CompareToSet(response.voucher.Select(c => new
            {
                documentReferenceNumber = c.documentReferenceNumber.Trim(),
                bsbNumber = c.bsbNumber.Trim(),
                accountNumber = c.accountNumber.Trim(),
                auxDom = c.auxDom.Trim(),
                extraAuxDom = c.extraAuxDom.Trim(),
                transactionCode = c.transactionCode.Trim(),
                amount = c.amount.Trim(),
                targetEndPoint = c.targetEndPoint.Trim(),
                unprocessable = c.unprocessable,
                repostFromDRN = c.repostFromDRN,
                repostFromProcessingDate = (c.repostFromProcessingDate == null) ? string.Empty : ((System.DateTime)c.repostFromProcessingDate).ToString("yyyyMMdd"),
                collectingBank = c.collectingBank
            }));
        }
        
        [Then(@"a CorrectBatchCodelineResponse with batch number (.*) contains this voucher batch:")]
        public void Then2(string batchNumber, Table table)
        {
            table.CompareToInstance(response.voucherBatch);
        }
    }
}
