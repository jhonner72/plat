using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.IntegrationTests.Hooks;
using Lombard.Adapters.DipsAdapter.Messages;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Collections.Generic;
using System.Threading;
using System.Linq;
using TechTalk.SpecFlow;
using TechTalk.SpecFlow.Assist;
using System.Diagnostics.CodeAnalysis;

namespace Lombard.Adapters.DipsAdapter.IntegrationTests.Steps
{
    [SuppressMessage("ReSharper", "InconsistentNaming")]
    public class CheckThirdPartyRequestData
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

        public string preAdjustmentAmount { get; set; }
        public bool adjustedFlag { get; set; }
        public bool thirdPartyCheckFailed { get; set; }
        public bool thirdPartyPoolFlag { get; set; }
        public bool highValueFlag { get; set; }
        public string voucherDelayedIndicator { get; set; }
        public bool thirdPartyMixedDepositReturnFlag { get; set; }
        public string dipsSequenceNumber { get; set; }
    }

    [Binding]
    public class CheckThirdPartySteps
    {
        [Given(@"there are no CheckThirdParty rows for batch number (.*)")]
        public void Given1(string batchNumber)
        {
            var batch = batchNumber.PadRight(8, ' ');

            using (var context = CheckThirdPartyBus.CreateContext())
            {
                context.Database.ExecuteSqlCommand("DELETE FROM [Queue] WHERE [S_BATCH] = @p0", batch);
                context.Database.ExecuteSqlCommand("DELETE FROM [NabChq] WHERE [S_BATCH] = @p0", batch);
                context.Database.ExecuteSqlCommand("DELETE FROM [DB_INDEX] WHERE [BATCH] = @p0", batch);
            }
        }

        private readonly CheckThirdPartyBatchRequest message = new CheckThirdPartyBatchRequest();

        [Given(@"a CheckThirdPartyBatchRequest with batch number (.*) is added to the queue for the following CheckThirdParty vouchers:")]
        public void Given2(string batchNumber, Table table)
        {
            var vouchers = table.CreateSet<CheckThirdPartyRequestData>();

            message.voucher = vouchers.Select(v => new CheckThirdPartyRequest
                {
                    thirdPartyCheckRequired = true,
                    dipsTraceNumber = v.documentReferenceNumber,
                    dipsSequenceNumber = v.dipsSequenceNumber,
                    voucherProcess = new VoucherProcess
                    {
                        preAdjustmentAmount = v.preAdjustmentAmount,
                        adjustedFlag = v.adjustedFlag,
                        thirdPartyCheckFailed = v.thirdPartyCheckFailed,
                        thirdPartyPoolFlag = v.thirdPartyPoolFlag,
                        highValueFlag = v.highValueFlag,
                        voucherDelayedIndicator = v.voucherDelayedIndicator,
                        thirdPartyMixedDepositReturnFlag = v.thirdPartyMixedDepositReturnFlag,
                    },
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

        [Given(@"a CheckThirdPartyBatchRequest with batch number (.*) contains this voucher batch:")]
        public void Given3(string batchNumber, Table table)
        {
            message.voucherBatch = table.CreateInstance<VoucherBatch>();
        }

        [When(@"the message is published to the queue and CheckThirdPartyBatchRequest process the message with this information:")]
        public void When1(Table table)
        {
            var publishInfo = table.CreateInstance<PublishInformation>();

            // synchronous Publish will wait until confirmed publish or timeout exception thrown
            CheckThirdPartyBus.Publish(message, publishInfo.CorrelationId, publishInfo.RoutingKey);

            Thread.Sleep(publishInfo.PublishTimeOutSeconds * 1000);
        }

        [Then(@"a DipsQueue table row will exist with the following CheckThirdParty values")]
        public void Then1(Table table)
        {
            var expected = table.CreateInstance<DipsQueue>();

            DipsQueue actual;

            using (var context = CheckThirdPartyBus.CreateContext())
            {
                actual = context.Queues.SingleOrDefault(q => q.S_BATCH == expected.S_BATCH);
            }

            Assert.IsNotNull(actual, "Could not find queue row with batch {0} in the database", expected.S_BATCH);

            TrimAllProperties(actual);

            table.CompareToInstance(actual);
        }

        [Then(@"DipsNabChq table rows will exist with the following CheckThirdParty values")]
        public void Then2(Table table)
        {
            var expected = table.CreateSet<DipsNabChq>();

            var dbRows = new List<DipsNabChq>();

            using (var context = CheckThirdPartyBus.CreateContext())
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

        [Then(@"DipsDbIndex table rows will exist with the following CheckThirdParty values")]
        public void Then3(Table table)
        {
            var expected = table.CreateSet<DipsDbIndex>();

            var dbRows = new List<DipsDbIndex>();

            using (var context = CheckThirdPartyBus.CreateContext())
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
