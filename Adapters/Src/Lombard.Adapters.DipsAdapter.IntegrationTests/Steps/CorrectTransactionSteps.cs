using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Threading;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.IntegrationTests.Hooks;
using Lombard.Adapters.DipsAdapter.Jobs;
using Lombard.Adapters.DipsAdapter.Messages;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using TechTalk.SpecFlow;
using TechTalk.SpecFlow.Assist;
using Lombard.Adapters.DipsAdapter.Helpers;

namespace Lombard.Adapters.DipsAdapter.IntegrationTests.Steps
{
    [SuppressMessage("ReSharper", "InconsistentNaming")]
    public class CorrectTransactionRequestData
    {
        public string transactionCode { get; set; }
        public string documentReferenceNumber { get; set; }
        public string bsbNumber { get; set; }
        public string accountNumber { get; set; }
        public DocumentTypeEnum documentType { get; set; }
        public string reasonCode { get; set; }
        public DateTime processingDate { get; set; }
        public string amount { get; set; }
        public string extraAuxDom { get; set; }
        public string auxDom { get; set; }
        public bool unprocessable { get; set; }
        public bool isGeneratedVoucher { get; set; }
        public bool thirdPartyCheckRequired { get; set; }
        public bool thirdPartyMixedDepositReturnFlag { get; set; }
    }

    [Binding]
    public class CorrectTransactionSteps
    {
        [Given(@"there are no CorrectTransaction rows for batch number (.*)")]
        public void Given1(string batchNumber)
        {
            var batch = batchNumber.PadRight(8, ' ');
            using (var context = CorrectTransactionBus.CreateContext())
            {
                context.Database.ExecuteSqlCommand("DELETE FROM [Queue] WHERE [S_BATCH] = @p0", batch);
                context.Database.ExecuteSqlCommand("DELETE FROM [NabChq] WHERE [S_BATCH] = @p0", batch);
                context.Database.ExecuteSqlCommand("DELETE FROM [DB_INDEX] WHERE [BATCH] = @p0", batch);
            }
        }

        private readonly CorrectBatchTransactionRequest message = new CorrectBatchTransactionRequest();

        [Given(@"a CorrectBatchTransactionRequest with batch number (.*) is added to the queue for the following CorrectTransaction vouchers:")]
        public void Given2(string batchNumber, Table table)
        {
            var vouchers = table.CreateSet<CorrectTransactionRequestData>();

            message.voucher = vouchers.Select(v => new CorrectTransactionRequest
            {
                codelineFieldsStatus = new CodelineStatus(),
                reasonCode = ResponseHelper.GetExpertBalanceReason(v.reasonCode),
                unprocessable = v.unprocessable,
                isGeneratedVoucher = v.isGeneratedVoucher,
                thirdPartyCheckRequired = v.thirdPartyCheckRequired,
                thirdPartyMixedDepositReturnFlag = v.thirdPartyMixedDepositReturnFlag,
                voucher = new Voucher
                {
                    accountNumber = v.accountNumber,
                    amount = v.amount,
                    auxDom = v.auxDom,
                    bsbNumber = v.bsbNumber,
                    documentReferenceNumber = v.documentReferenceNumber,
                    documentType = v.documentType,
                    extraAuxDom = v.extraAuxDom,
                    processingDate = v.processingDate,
                    transactionCode = v.transactionCode,
                }
            }).ToArray();
        }

        [Given(@"a CorrectBatchTransactionRequest with batch number (.*) contains this voucher batch:")]
        public void Given3(string batchNumber, Table table)
        {
            message.voucherBatch = table.CreateInstance<VoucherBatch>();
        }

        [When(@"the message is published to the queue and CorrectBatchTransactionRequest process the message with this information:")]
        public void When1(Table table)
        {
            var publishInfo = table.CreateInstance<PublishInformation>();

            // synchronous Publish will wait until confirmed publish or timeout exception thrown
            CorrectTransactionBus.Publish(message, publishInfo.CorrelationId, publishInfo.RoutingKey);

            Thread.Sleep(publishInfo.PublishTimeOutSeconds * 1000);
        }

        [Then(@"a DipsQueue table row will exist with the following CorrectTransaction values")]
        public void Then1(Table table)
        {
            var expected = table.CreateInstance<DipsQueue>();

            DipsQueue actual;

            using (var context = CorrectTransactionBus.CreateContext())
            {
                actual = context.Queues.SingleOrDefault(q => q.S_BATCH == expected.S_BATCH);
            }

            Assert.IsNotNull(actual, "Could not find queue row with batch {0} in the database", expected.S_BATCH);

            TrimAllProperties(actual);

            table.CompareToInstance(actual);
        }

        [Then(@"DipsNabChq table rows will exist with the following CorrectTransaction values")]
        public void Then2(Table table)
        {
            var expected = table.CreateSet<DipsNabChq>();

            var dbRows = new List<DipsNabChq>();

            using (var context = CorrectTransactionBus.CreateContext())
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

        [Then(@"DipsDbIndex table rows will exist with the following CorrectTransaction values")]
        public void Then3(Table table)
        {
            var expected = table.CreateSet<DipsDbIndex>();

            var dbRows = new List<DipsDbIndex>();

            using (var context = CorrectTransactionBus.CreateContext())
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
