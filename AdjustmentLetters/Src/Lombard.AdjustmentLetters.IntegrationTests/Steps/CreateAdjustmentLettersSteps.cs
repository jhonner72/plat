using System.Linq;
using System.Threading;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Lombard.AdjustmentLetters.IntegrationTests.Hooks;
using Lombard.Vif.Service.Messages.XsdImports;
using TechTalk.SpecFlow;
using TechTalk.SpecFlow.Assist;
using System.IO;
using System.Configuration;
using System;

namespace Lombard.AdjustmentLetters.IntegrationTests.Steps
{
    public class CreateBatchAdjustmentLettersRequestData
    {
        public string ScannedBatchNumber { get; set; }
        public string DocumentReferenceNumber { get; set; }
        public bool AdjustedFlag { get; set; }
        public string TransactionLinkNumber { get; set; }
        public string AccountNumber { get; set; }
        public string BsbNumber { get; set; }
        public string OutputFilenamePrefix { get; set; }
        public string CollectingBank { get; set; }
    }

    [Binding]
    public class CreateAdjustmentLettersSteps
    {
        private CreateBatchAdjustmentLettersRequest request;

        [Given(@"a valid AdjustmentLetters request is available in the request queue with this information")]
        public void GivenAValidAdjustmentLettersRequestIsAvailableInTheRequestQueueWithThisInformation(Table table)
        {
            request = table.CreateInstance<CreateBatchAdjustmentLettersRequest>();

            string jobFolder = Path.Combine(ConfigurationManager.AppSettings["AdjustmentLetter:BitLockerLocation"].ToString(), request.jobIdentifier);

            if (Directory.Exists(jobFolder))
            {
                Directory.Delete(jobFolder, true);
            }

            Directory.CreateDirectory(jobFolder);

            string testDataLocation = Path.Combine("TestData", request.jobIdentifier);

            foreach (var file in Directory.GetFiles(testDataLocation))
            {
                File.Copy(file, file.Replace(testDataLocation, jobFolder));
            }
        }

        [Given(@"AdjustmentLetters payload contains these voucherProcess")]
        public void GivenAdjustmentLettersPayloadContainsTheseVoucherProcess(Table table)
        {
            var requestData = table.CreateSet<CreateBatchAdjustmentLettersRequestData>().ToList();

            request.outputMetadata = requestData.Select(_ => new AdjustmentLettersDetails
            {
                customer = new[]
                {
                    new CustomerDetails
                    {
                        accountNumber = _.AccountNumber,
                        bsb = _.BsbNumber
                    }
                },
                outputFilenamePrefix = _.OutputFilenamePrefix
            }).ToArray();

            request.voucherInformation = requestData.Select(_ => new VoucherInformation
            {
                voucher = new Voucher
                {
                    accountNumber = _.AccountNumber,
                    bsbNumber = _.BsbNumber,
                    documentReferenceNumber = _.DocumentReferenceNumber
                },
                voucherBatch = new VoucherBatch
                {
                    scannedBatchNumber = _.ScannedBatchNumber,
                    collectingBank = _.CollectingBank
                },
                voucherProcess = new VoucherProcess
                {
                    adjustedFlag = _.AdjustedFlag,
                    transactionLinkNumber = _.TransactionLinkNumber
                }
            }).ToArray();
        }

        [When(@"create AdjustmentLetters process run")]
        public void WhenCreateAdjustmentLettersProcessRun()
        {
            CreateAdjustmentLettersBus.Publish(request, request.jobIdentifier);

            Thread.Sleep(3000);
        }

        [Then(@"these AdjustmentLetters files will be created")]
        public void ThenTheseAdjustmentLettersFilesWillBeCreated(Table table)
        {
            var fileNames = table.Rows.Select(r => r["FileName"]).ToList();

            string jobFolder = Path.Combine(ConfigurationManager.AppSettings["AdjustmentLetter:BitLockerLocation"].ToString(), 
                request.jobIdentifier);

            foreach (var fileName in fileNames)
            {
                Assert.IsTrue(File.Exists(Path.Combine(jobFolder, fileName)));
            }
        }

        [Then(@"AdjustmentLetters response will be created")]
        public void ThenAdjustmentLettersResponseWillBeCreated(Table table)
        {
            var task = CreateAdjustmentLettersBus.GetSingleResponseAsync(5);
            task.Wait();

            var response = task.Result;

            Assert.IsNotNull(response, "No response received");

            table.CompareToSet(response.adjustmentLetters);
        }
    }
}
