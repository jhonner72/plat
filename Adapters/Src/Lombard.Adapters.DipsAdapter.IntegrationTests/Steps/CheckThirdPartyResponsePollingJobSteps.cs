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
    public class CheckThirdPartyResponsePollingJobSteps
    {
        [Given(@"there are CheckThirdParty database rows for batch number (.*) in DipsQueue database")]
        public void Given1(string batchNumber, Table table)
        {
            var batch = batchNumber.PadRight(8, ' ');
            using (var context = CheckThirdPartyBus.CreateContext())
            {
                context.Database.ExecuteSqlCommand("DELETE FROM [Queue] WHERE [S_BATCH] = @p0", batch);
                context.Database.ExecuteSqlCommand("DELETE FROM [NabChq] WHERE [S_BATCH] = @p0", batch);
                context.Database.ExecuteSqlCommand("DELETE FROM [DB_INDEX] WHERE [BATCH] = @p0", batch);

                var queue = table.CreateInstance<DipsQueue>();

                context.Queues.Add(queue);

                context.SaveChanges();
            }
        }

        [Given(@"there are CheckThirdParty database rows for batch number (.*) in DipsNabChq database")]
        public void Given2(string batchNumber, Table table)
        {
            using (var context = CheckThirdPartyBus.CreateContext())
            {
                var dbRows = table.CreateSet<DipsNabChq>();

                foreach (var dbRow in dbRows)
                {
                    context.NabChqPods.Add(dbRow);
                }

                context.SaveChanges();
            }
        }

        [When(@"wait for (.*) seconds until CheckThirdPartyResponsePollingJob executed")]
        public void When1(int waitSeconds)
        {
            Thread.Sleep(waitSeconds * 1000);
        }

        private CheckThirdPartyBatchResponse response;

        [Then(@"a CheckThirdPartyBatchResponse with batch number (.*) is added to the exchange and contains these vouchers:")]
        public void Then1(string batchNumber, Table table)
        {
            var task = CheckThirdPartyBus.GetSingleResponseAsync(5);
            task.Wait();

            response = task.Result;

            Assert.IsNotNull(response, "No response received");
            Assert.IsFalse(string.IsNullOrEmpty(response.voucherBatch.scannedBatchNumber));
            Assert.AreEqual(batchNumber, response.voucherBatch.scannedBatchNumber);

            table.CompareToSet(response.voucher.Select(c => new
            {
                documentReferenceNumber = c.voucher.documentReferenceNumber.Trim(),
                bsbNumber = c.voucher.bsbNumber.Trim(),
                accountNumber = c.voucher.accountNumber.Trim(),
                auxDom = c.voucher.auxDom.Trim(),
                extraAuxDom = c.voucher.extraAuxDom.Trim(),
                transactionCode = c.voucher.transactionCode.Trim(),
                amount = c.voucher.amount.Trim(),
                
                //targetEndPoint = c.voucher.targetEndPoint.Trim(),
                //unprocessable = c.voucher.unprocessable,
                //repostFromDRN = c.voucher.repostFromDRN,
                //repostFromProcessingDate = c.voucher.repostFromProcessingDate.ToString("yyyyMMdd")
            }));
        }

        [Then(@"a CheckThirdPartyBatchResponse with batch number (.*) contains this voucher batch:")]
        public void Then2(string batchNumber, Table table)
        {
            table.CompareToInstance(response.voucherBatch);
        }
    }
}
