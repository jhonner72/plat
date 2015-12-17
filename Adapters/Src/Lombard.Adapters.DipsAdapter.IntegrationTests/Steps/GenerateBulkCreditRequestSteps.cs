using Lombard.Adapters.DipsAdapter.IntegrationTests.Hooks;
using Lombard.Adapters.DipsAdapter.Messages;
using System.Threading;
using System.Linq;
using TechTalk.SpecFlow;
using TechTalk.SpecFlow.Assist;

namespace Lombard.Adapters.DipsAdapter.IntegrationTests.Steps
{
    [Binding]
    public class GenerateBulkCreditRequestSteps
    {
        private readonly GenerateBatchBulkCreditRequest message = new GenerateBatchBulkCreditRequest();

        [Given(@"there are no GenerateBatchBulkCreditRequest rows for batch number (.*)")]
        public void Given1(string batchNumber)
        {
            var batch = batchNumber.PadRight(8, ' ');
            using (var context = GenerateCorrespondingVoucherBus.CreateContext())
            {
                context.Database.ExecuteSqlCommand("DELETE FROM [Queue] WHERE [S_BATCH] = @p0", batch);
                context.Database.ExecuteSqlCommand("DELETE FROM [NabChq] WHERE [S_BATCH] = @p0", batch);
                context.Database.ExecuteSqlCommand("DELETE FROM [DB_INDEX] WHERE [BATCH] = @p0", batch);
            }
        }

        [Given(@"a GenerateBatchBulkCreditRequest is added to the queue for the following BulkCreditRequest vouchers:")]
        public void Given2(Table table)
        {
            var vouchers = table.CreateSet<VoucherGroupCriteria>();
            message.maxDebitVouchers = 10;
            message.vouchers = vouchers.ToArray();
        }

        [When(@"the message is published to the queue and GenerateBatchBulkCreditRequest process the message with this information:")]
        public void When1(Table table)
        {
            var publishInfo = table.CreateInstance<PublishInformation>();

            message.jobIdentifier = publishInfo.CorrelationId;

            // synchronous Publish will wait until confirmed publish or timeout exception thrown
            GenerateBulkCreditBus.Publish(message, publishInfo.CorrelationId, publishInfo.RoutingKey);

            Thread.Sleep(publishInfo.PublishTimeOutSeconds * 1000);
        }
    }
}
