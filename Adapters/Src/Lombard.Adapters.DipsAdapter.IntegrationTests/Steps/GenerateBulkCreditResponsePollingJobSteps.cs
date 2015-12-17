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
    public class GenerateBulkCreditResponsePollingJobSteps
    {
        [Given(@"there are Generate Bulk Credit Voucher database rows for batch number (.*) in DipsQueue database")]
        public void Given1(string batchNumber, Table table)
        {
            var batch = batchNumber.PadRight(8, ' ');
            using (var context = GenerateBulkCreditBus.CreateContext())
            {
                context.Database.ExecuteSqlCommand("DELETE FROM [Queue] WHERE [S_BATCH] = @p0", batch);
                context.Database.ExecuteSqlCommand("DELETE FROM [NabChq] WHERE [S_BATCH] = @p0", batch);
                context.Database.ExecuteSqlCommand("DELETE FROM [DB_INDEX] WHERE [BATCH] = @p0", batch);

                var queue = table.CreateInstance<DipsQueue>();

                context.Queues.Add(queue);

                context.SaveChanges();
            }
        }

        [Given(@"there are Generate Bulk Credit database rows for batch number (.*) in DipsNabChq database")]
        public void Given2(string batchNumber, Table table)
        {
            using (var context = GenerateBulkCreditBus.CreateContext())
            {
                var dbRows = table.CreateSet<DipsNabChq>();

                foreach (var dbRow in dbRows)
                {
                    context.NabChqPods.Add(dbRow);
                }

                context.SaveChanges();
            }
        }

        [When(@"wait for (.*) seconds until GenerateBulkCreditResponsePollingJob executed")]
        public void When1(int waitSeconds)
        {
            Thread.Sleep(waitSeconds * 1000);
        }

        private GenerateBatchBulkCreditResponse response;

        [Then(@"a GenerateBulkCreditResponse with batch number (.*) is added to the exchange and contains these generated vouchers:")]
        public void Then1(string batchNumber, Table table)
        {
            var task = GenerateBulkCreditBus.GetSingleResponseAsync(5);
            task.Wait();

            response = task.Result;

            Assert.IsNotNull(response, "No response received");


            Assert.IsFalse(string.IsNullOrEmpty(response.transactions.First().bulkCreditVoucher.voucherBatch.scannedBatchNumber));
            Assert.AreEqual(batchNumber, response.transactions.First().bulkCreditVoucher.voucherBatch.scannedBatchNumber);

            table.CompareToSet(response.transactions.Select(c => new
            {
                documentReferenceNumber = c.associatedDebitVouchers.First().documentReferenceNumber.Trim(),
                bsbNumber = c.associatedDebitVouchers.First().bsbNumber.Trim(),
                accountNumber = c.associatedDebitVouchers.First().accountNumber.Trim(),
                auxDom = c.associatedDebitVouchers.First().auxDom.Trim(),
                extraAuxDom = c.associatedDebitVouchers.First().extraAuxDom.Trim(),
                transactionCode = c.associatedDebitVouchers.First().transactionCode.Trim(),
                amount = c.associatedDebitVouchers.First().amount.Trim()
            }));
        }

        [Then(@"a GenerateBulkCreditResponse with batch number (.*) is added to the exchange and contains these non-generated vouchers:")]
        public void Then2(string batchNumber, Table table)
        {
            var task = GenerateBulkCreditBus.GetSingleResponseAsync(5);
            task.Wait();

            response = task.Result;

            Assert.IsNotNull(response, "No response received");
            Assert.IsFalse(string.IsNullOrEmpty(response.transactions.First().bulkCreditVoucher.voucherBatch.scannedBatchNumber));
            Assert.AreEqual(batchNumber, response.transactions.First().bulkCreditVoucher.voucherBatch.scannedBatchNumber);

            table.CompareToSet(response.transactions.Select(c => new
            {
                documentReferenceNumber = c.bulkCreditVoucher.voucher.documentReferenceNumber.Trim(),
                bsbNumber = c.bulkCreditVoucher.voucher.bsbNumber.Trim(),
                accountNumber = c.bulkCreditVoucher.voucher.accountNumber.Trim(),
                auxDom = c.bulkCreditVoucher.voucher.auxDom.Trim(),
                extraAuxDom = c.bulkCreditVoucher.voucher.extraAuxDom.Trim(),
                transactionCode = c.bulkCreditVoucher.voucher.transactionCode.Trim(),
                amount = c.bulkCreditVoucher.voucher.amount.Trim()
            }));
        }

        [Then(@"a GenerateBulkCreditResponse with batch number (.*) contains this voucher batch:")]
        public void Then3(string batchNumber, Table table)
        {
            table.CompareToInstance(response.transactions.First().bulkCreditVoucher.voucherBatch);
        }
    }
}
