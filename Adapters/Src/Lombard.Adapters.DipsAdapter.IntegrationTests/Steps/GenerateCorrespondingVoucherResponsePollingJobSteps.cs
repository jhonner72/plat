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
    public class GenerateCorrespondingVoucherResponsePollingJobSteps
    {
        [Given(@"there are Generate Corresponding Voucher database rows for batch number (.*) in DipsQueue database")]
        public void Given1(string batchNumber, Table table)
        {
            var batch = batchNumber.PadRight(8, ' ');
            using (var context = GenerateCorrespondingVoucherBus.CreateContext())
            {
                context.Database.ExecuteSqlCommand("DELETE FROM [Queue] WHERE [S_BATCH] = @p0", batch);
                context.Database.ExecuteSqlCommand("DELETE FROM [NabChq] WHERE [S_BATCH] = @p0", batch);
                context.Database.ExecuteSqlCommand("DELETE FROM [DB_INDEX] WHERE [BATCH] = @p0", batch);

                var queue = table.CreateInstance<DipsQueue>();

                context.Queues.Add(queue);

                context.SaveChanges();
            }
        }

        [Given(@"there are Generate Corresponding Voucher database rows for batch number (.*) in DipsNabChq database")]
        public void Given2(string batchNumber, Table table)
        {
            using (var context = GenerateCorrespondingVoucherBus.CreateContext())
            {
                var dbRows = table.CreateSet<DipsNabChq>();

                foreach (var dbRow in dbRows)
                {
                    context.NabChqPods.Add(dbRow);
                }

                context.SaveChanges();
            }
        }

        [When(@"wait for (.*) seconds until GenerateCorrespondingVoucherResponsePollingJob executed")]
        public void When1(int waitSeconds)
        {
            Thread.Sleep(waitSeconds * 1000);
        }

        private GenerateCorrespondingVoucherResponse response;

        [Then(@"a GenerateCorrespondingVoucherResponse with batch number (.*) is added to the exchange and contains these generated vouchers:")]
        public void Then1(string batchNumber, Table table)
        {
            var task = GenerateCorrespondingVoucherBus.GetSingleResponseAsync(5);
            task.Wait();

            response = task.Result;

            Assert.IsNotNull(response, "No response received");
            Assert.IsFalse(string.IsNullOrEmpty(response.generatedVoucher.First().voucherBatch.scannedBatchNumber));
            Assert.AreEqual(batchNumber, response.generatedVoucher.First().voucherBatch.scannedBatchNumber);

            table.CompareToSet(response.generatedVoucher.Select(c => new
            {
                documentReferenceNumber = c.voucher.documentReferenceNumber.Trim(),
                bsbNumber = c.voucher.bsbNumber.Trim(),
                accountNumber = c.voucher.accountNumber.Trim(),
                auxDom = c.voucher.auxDom.Trim(),
                extraAuxDom = c.voucher.extraAuxDom.Trim(),
                transactionCode = c.voucher.transactionCode.Trim(),
                amount = c.voucher.amount.Trim()
            }));
        }

        [Then(@"a GenerateCorrespondingVoucherResponse with batch number (.*) is added to the exchange and contains these non-generated vouchers:")]
        public void Then2(string batchNumber, Table table)
        {
            var task = GenerateCorrespondingVoucherBus.GetSingleResponseAsync(5);
            task.Wait();

            response = task.Result;

            Assert.IsNotNull(response, "No response received");
            Assert.IsFalse(string.IsNullOrEmpty(response.generatedVoucher.First().voucherBatch.scannedBatchNumber));
            Assert.AreEqual(batchNumber, response.generatedVoucher.First().voucherBatch.scannedBatchNumber);

            table.CompareToSet(response.updateVoucher.Select(c => new
            {
                documentReferenceNumber = c.voucher.documentReferenceNumber.Trim(),
                bsbNumber = c.voucher.bsbNumber.Trim(),
                accountNumber = c.voucher.accountNumber.Trim(),
                auxDom = c.voucher.auxDom.Trim(),
                extraAuxDom = c.voucher.extraAuxDom.Trim(),
                transactionCode = c.voucher.transactionCode.Trim(),
                amount = c.voucher.amount.Trim()
            }));
        }

        [Then(@"a GenerateCorrespondingVoucherResponse with batch number (.*) contains this voucher batch:")]
        public void Then3(string batchNumber, Table table)
        {
            table.CompareToInstance(response.generatedVoucher.First().voucherBatch);
        }
    }
}
