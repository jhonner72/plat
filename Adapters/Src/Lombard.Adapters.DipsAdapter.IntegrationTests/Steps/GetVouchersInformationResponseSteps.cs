using System.Linq;
using System.Threading;
using Lombard.Adapters.DipsAdapter.IntegrationTests.Hooks;
using Lombard.Adapters.DipsAdapter.Messages;
using TechTalk.SpecFlow;
using TechTalk.SpecFlow.Assist;
using Lombard.Adapters.Data.Domain;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Lombard.Adapters.DipsAdapter.IntegrationTests.Steps
{
    [Binding]
    public class GetVouchersInformationResponseSteps
    {
        private readonly GetVouchersInformationResponse message = new GetVouchersInformationResponse();

        [Given(@"there are no GetVouchersInformationResponse rows for guid (.*)")]
        public void Given1(string guidName)
        {
            using (var context = GetVouchersInformationBus.CreateContext())
            {
                context.Database.ExecuteSqlCommand("DELETE FROM [response_data] WHERE [guid_name] = @p0", guidName);
                context.Database.ExecuteSqlCommand("DELETE FROM [response_done] WHERE [guid_name] = @p0", guidName);
            }
        }

        [Given(@"a GetVouchersInformationResponse contains the following vouchers:")]
        public void Given2(Table table)
        {
            var voucherObject = table.CreateSet<Voucher>();

            message.voucherInformation = new[]
            {
                new VoucherInformation
                {
                    voucher = voucherObject.First(),
                    voucherImage = new[]
                        {
                            new Image
                            {
                                content = new byte[]{ 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20 }
                            },
                            new Image
                            {
                                content = new byte[]{ 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20 }
                            }
                        }
                    }
            };
        }

        [Given(@"a GetVouchersInformationResponse contains the following voucher batch:")]
        public void Given3(Table table)
        {
            var voucherBatchObject = table.CreateSet<VoucherBatch>();

            message.voucherInformation.First().voucherBatch = voucherBatchObject.First();

        }

        [Given(@"a GetVouchersInformationResponse contains the following voucher process:")]
        public void Given4(Table table)
        {
            var voucherProcessObject = table.CreateSet<VoucherProcess>();

            message.voucherInformation.First().voucherProcess = voucherProcessObject.First();
        }

        [When(@"the message is published to the queue and GetVouchersInformationResponse process the message with this information:")]
        public void When1(Table table)
        {
            var publishInfo = table.CreateInstance<PublishInformation>();

            // synchronous Publish will wait until confirmed publish or timeout exception thrown
            GetVouchersInformationBus.Publish(message, publishInfo.CorrelationId, publishInfo.RoutingKey);

            Thread.Sleep(publishInfo.PublishTimeOutSeconds * 1000);
        }

        [Then(@"a DipsResponseData table row will exist with the following values")]
        public void Then1(Table table)
        {
            var expected = table.CreateInstance<DipsResponseData>();

            DipsResponseData actual;

            using (var context = GetVouchersInformationBus.CreateContext())
            {
                actual = context.DipsResponseData.SingleOrDefault(d => d.guid_name == expected.guid_name);
            }

            Assert.IsNotNull(actual, "Could not find row with guid name {0} in the response_data queue", expected.guid_name);

            TrimAllProperties(actual);

            table.CompareToInstance(actual);
        }

        [Then(@"a DipsResponseDone table row will exist with the following values")]
        public void Then2(Table table)
        {
            var expected = table.CreateInstance<DipsResponseDone>();

            DipsResponseDone actual;

            using (var context = GetVouchersInformationBus.CreateContext())
            {
                actual = context.DipsResponseDone.SingleOrDefault(d => d.guid_name == expected.guid_name);
            }

            Assert.IsNotNull(actual, "Could not find row with guid name {0} in the response_done queue", expected.guid_name);

            TrimAllProperties(actual);

            table.CompareToInstance(actual);
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
