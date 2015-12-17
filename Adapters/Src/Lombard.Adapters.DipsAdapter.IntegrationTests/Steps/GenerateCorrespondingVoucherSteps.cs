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
    public class GenerateCorrespondingVoucherRequestData
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
    }
    
    [Binding]
    public class GenerateCorrespondingVoucherSteps
    {
        [Given(@"there are no GenerateCorrespondingVoucher rows for trace number (.*) or (.*)")]
        public void Given1(string firstTraceNumber, string secondTraceNumber)
        {
            using (var context = GenerateCorrespondingVoucherBus.CreateContext())
            {
                context.Database.ExecuteSqlCommand("DELETE FROM [Queue] WHERE [S_TRACE] = @p0", firstTraceNumber);
                context.Database.ExecuteSqlCommand("DELETE FROM [NabChq] WHERE [S_TRACE] = @p0", firstTraceNumber);
                context.Database.ExecuteSqlCommand("DELETE FROM [DB_INDEX] WHERE [TRACE] = @p0", firstTraceNumber);
                context.Database.ExecuteSqlCommand("DELETE FROM [Queue] WHERE [S_TRACE] = @p0", secondTraceNumber);
                context.Database.ExecuteSqlCommand("DELETE FROM [NabChq] WHERE [S_TRACE] = @p0", secondTraceNumber);
                context.Database.ExecuteSqlCommand("DELETE FROM [DB_INDEX] WHERE [TRACE] = @p0", secondTraceNumber);
            }
        }

        private readonly GenerateCorrespondingVoucherRequest message = new GenerateCorrespondingVoucherRequest();

        [Given(@"a GenerateCorrespondingVoucherRequest with batch number (.*) is added to the queue for the following GenerateCorrespondingVoucher vouchers:")]
        public void Given2(string batchNumber, Table table)
        {
            var vouchers = table.CreateSet<GenerateCorrespondingVoucherRequestData>();

            message.generateVoucher = vouchers.Select(v => new VoucherInformation
            {
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

            //message.voucher = vouchers.ToArray();
        }

        [Given(@"a GenerateCorrespondingVoucherRequest with batch number (.*) contains this voucher batch:")]
        public void Given3(string batchNumber, Table table)
        {
            var vouchers = table.CreateInstance<VoucherBatch>();

            foreach (var voucher in message.generateVoucher)
            {
                voucher.voucherBatch = vouchers;
            }
        }

        [When(@"the message is published to the queue and GenerateCorrespondingVoucherRequest process the message with this information:")]
        public void When1(Table table)
        {
            var publishInfo = table.CreateInstance<PublishInformation>();

            message.jobIdentifier = publishInfo.CorrelationId;

            // synchronous Publish will wait until confirmed publish or timeout exception thrown
            GenerateCorrespondingVoucherBus.Publish(message, publishInfo.CorrelationId, publishInfo.RoutingKey);

            Thread.Sleep(publishInfo.PublishTimeOutSeconds * 1000);
        }

        [Then(@"a DipsQueue table row will exist with the following GenerateCorrespondingVoucher values")]
        public void Then1(Table table)
        {
            var expected = table.CreateInstance<DipsQueue>();

            DipsQueue actual;

            using (var context = GenerateCorrespondingVoucherBus.CreateContext())
            {
                actual = context.Queues.SingleOrDefault(q => q.S_TRACE == expected.S_TRACE);
            }

            Assert.IsNotNull(actual, "Could not find queue row with trace {0} in the database", expected.S_TRACE);

            TrimAllProperties(actual);

            table.CompareToInstance(actual);
        }

        [Then(@"DipsNabChq table rows will exist with the following GenerateCorrespondingVoucher values")]
        public void Then2(Table table)
        {
            var expected = table.CreateSet<DipsNabChq>();

            var dbRows = new List<DipsNabChq>();

            using (var context = GenerateCorrespondingVoucherBus.CreateContext())
            {
                foreach (var chqPod in expected)
                {
                    var actual = context.NabChqPods.SingleOrDefault(c => c.S_TRACE == chqPod.S_TRACE);

                    Assert.IsNotNull(actual, string.Format("Could not find NabChqScanPod row with trace {0} in the database", chqPod.S_TRACE));

                    TrimAllProperties(actual);

                    dbRows.Add(actual);
                }
            }

            table.CompareToSet(dbRows);
        }

        [Then(@"DipsDbIndex table rows will exist with the following GenerateCorrespondingVoucher values")]
        public void Then3(Table table)
        {
            var expected = table.CreateSet<DipsDbIndex>();

            var dbRows = new List<DipsDbIndex>();

            using (var context = GenerateCorrespondingVoucherBus.CreateContext())
            {
                foreach (var dbIndex in expected)
                {
                    var actual = context.DbIndexes.SingleOrDefault(i => i.TRACE == dbIndex.TRACE);

                    Assert.IsNotNull(actual, string.Format("Could not find DB_INDEX row with trace {0} in the database", dbIndex.TRACE));

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
