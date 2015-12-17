using System;
using Lombard.ImageExchange.Nab.IntegrationTests.Hooks;
using Lombard.ImageExchange.Nab.OutboundService.Messages.XsdImports;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using TechTalk.SpecFlow;

namespace Lombard.ImageExchange.Nab.IntegrationTests.Steps
{
    [Binding]
    public class CreateOutboundImageExchangeFileSteps
    {
        [When(@"a CreateImageExchangeFileRequest is added to the queue for the given job and targetEndPoint (.*) and sequenceNumber (.*) for today")]
        public void WhenACreateImageExchangeFileRequestIsAddedToTheQueueWithJobIdentifier(string targetEndPoint, int sequenceNumber)
        {
            var jobIdentifier = ScenarioContext.Current.Get<string>("jobIdentifier");

            var createImageExchangeFileRequest = new CreateImageExchangeFileRequest
            {
                businessDate = DateTime.Today,
                jobIdentifier = jobIdentifier,
                sequenceNumber = sequenceNumber,
                targetEndPoint = targetEndPoint
            };

            ScenarioContext.Current.Add("createImageExchangeFileRequests", createImageExchangeFileRequest);

            OutboundServiceBus.Publish(createImageExchangeFileRequest);
        }

        [Then(@"a CreateImageExchangeFileResponse will be added to the CreateImageExchangeFileResponse queue")]
        public void ThenACreateImageExchangeFileResponseWillBeAddedToTheCreateImageExchangeFileResponseQueue()
        {
            const int TimeoutInSeconds = 120;

            var task = OutboundServiceBus.GetSingleResponseAsync(TimeoutInSeconds);
            task.Wait();

            var response = task.Result;

            Assert.IsNotNull(response, "No response received");
            Assert.IsNotNull(response.imageExchangeFilename);

            Console.WriteLine("Created image exchange file: {0}", response.imageExchangeFilename);

            ScenarioContext.Current.Add("imageExchangeBatchFilename", response.imageExchangeFilename);
        }

        // TODO: ideally we can check the generated output file to ensure they match
        //[Then(@"a matching OutboundVoucherFile will exist in the database matching the response and the original request")]
        //public void ThenAMatchingOutboundVoucherFileWillExistInTheDatabaseMatchingTheResponse()
        //{
        //    var outboundVoucherFile = GetExpectedOutboundVoucherFileFromDatabase();

        //    var outboundVouchers = GetOutboundVouchers(outboundVoucherFile.Id);

        //    Assert.IsNotNull(outboundVouchers);

        //    var originalRequestItems = ScenarioContext.Current.Get<IEnumerable<CreateImageExchangeFileRequest>>("originalRequest").ToList();

        //    Assert.AreEqual(originalRequestItems.Count(), outboundVouchers.Count());

        //    foreach (var originalRequest in originalRequestItems)
        //    {
        //        // find the "matching" voucher in the result set by using [accountNumber]
        //        // ASSUMES each [accountNumber] is distinct
        //        Assert.IsNotNull(originalRequest.voucher.accountNumber);
        //        var matchingOutboundVoucher = outboundVouchers.SingleOrDefault(ov => ov.DrawerAccountNumber.Equals(originalRequest.voucher.accountNumber));
        //        Assert.IsNotNull(matchingOutboundVoucher, string.Format("Cannot find an OutboundVoucher matching accountNumber {0}", originalRequest.voucher.accountNumber));
                
        //        // Once we find the match, do a field level comparison
        //        Assert.AreEqual(originalRequest.voucher.bsbNumber, matchingOutboundVoucher.BsbLedgerFi);
        //        Assert.AreEqual(long.Parse(originalRequest.voucher.amount), matchingOutboundVoucher.Amount);
        //        Assert.AreEqual(originalRequest.voucher.auxDom, matchingOutboundVoucher.AuxiliaryDomestic);
        //        Assert.AreEqual(originalRequest.voucher.extraAuxDom, matchingOutboundVoucher.ExtraAuxiliaryDomestic);
        //        Assert.AreEqual(originalRequest.voucher.transactionCode, matchingOutboundVoucher.TransactionCode);
        //        Assert.AreEqual(VoucherIndicator.ImageIsPresent, matchingOutboundVoucher.VoucherIndicator);

        //        Assert.AreEqual(originalRequest.collectingBsb, matchingOutboundVoucher.BsbCollectingFi);
        //        Assert.AreEqual(originalRequest.captureBsb, matchingOutboundVoucher.BsbCapturingFi);
        //        Assert.AreEqual(originalRequest.voucher.processingDate, matchingOutboundVoucher.TransmissionDate);

        //        Assert.AreEqual(originalRequest.voucher.transactionCode, matchingOutboundVoucher.TransactionCode);
        //        Assert.AreEqual(originalRequest.voucher.documentReferenceNumber, matchingOutboundVoucher.TransactionIdentifier);
        //    }
        //}

        //private IList<OutboundVoucher> GetOutboundVouchers(long outboundVoucherFileId)
        //{
        //    IList<OutboundVoucher> outboundVouchers;

        //    using (var context = DatabaseHelper.CreateContext())
        //    {
        //        outboundVouchers = context.Vouchers.Where(v => v.OutboundVoucherFileId == outboundVoucherFileId).ToList();
        //    }

        //    Console.WriteLine("------------------------------------------------");
        //    Console.WriteLine("Contains the following OutboundVoucher(s):");
        //    Console.WriteLine("------------------------------------------------");

        //    foreach (var outboundVoucher in outboundVouchers)
        //    {
        //        Console.WriteLine("OutboundVoucher.Id: {0}", outboundVoucher.Id);
        //        Console.WriteLine("OutboundVoucher.Amount: {0}", outboundVoucher.Amount);
        //        Console.WriteLine("OutboundVoucher.AuxiliaryDomestic: {0}", outboundVoucher.AuxiliaryDomestic);
        //        Console.WriteLine("OutboundVoucher.BatchNumber: {0}", outboundVoucher.BatchNumber);
        //        Console.WriteLine("OutboundVoucher.BsbCapturingFi: {0}", outboundVoucher.BsbCapturingFi);
        //        Console.WriteLine("OutboundVoucher.BsbCollectingFi: {0}", outboundVoucher.BsbCollectingFi);
        //        Console.WriteLine("OutboundVoucher.BsbLedgerFi: {0}", outboundVoucher.BsbLedgerFi);
        //        Console.WriteLine("OutboundVoucher.DebitCreditType: {0}", outboundVoucher.DebitCreditType.Value);
        //        Console.WriteLine("OutboundVoucher.DipsBatchNumber: {0}", outboundVoucher.DipsBatchNumber);
        //        Console.WriteLine("OutboundVoucher.DrawerAccountNumber: {0}", outboundVoucher.DrawerAccountNumber);
        //        Console.WriteLine("OutboundVoucher.ExtraAuxiliaryDomestic: {0}", outboundVoucher.ExtraAuxiliaryDomestic);
        //        Console.WriteLine("OutboundVoucher.TransactionCode: {0}", outboundVoucher.TransactionCode);
        //        Console.WriteLine("OutboundVoucher.TransactionIdentifier: {0}", outboundVoucher.TransactionIdentifier);
        //        Console.WriteLine("OutboundVoucher.TransmissionDate: {0}", outboundVoucher.TransmissionDate);
        //        Console.WriteLine("OutboundVoucher.VoucherIndicator: '{0}'", outboundVoucher.VoucherIndicator.Value); // remember " " means ImageIsPresent
        //        Console.WriteLine("OutboundVoucher.FrontImage: {0}", (outboundVoucher.Image == null || outboundVoucher.Image.FrontImage == null) ? "Missing" : "Present");
        //        Console.WriteLine("OutboundVoucher.RearImage: {0}", (outboundVoucher.Image == null || outboundVoucher.Image.RearImage == null) ? "Missing" : "Present");
        //        Console.WriteLine("OutboundVoucher.FrontImagePath: {0}", outboundVoucher.FrontImagePath);
        //        Console.WriteLine("OutboundVoucher.RearImagePath: {0}", outboundVoucher.RearImagePath);
        //        Console.WriteLine("------------------------------------------------");
        //    }

        //    return outboundVouchers;
        //}

        //private static OutboundVoucherFile GetExpectedOutboundVoucherFileFromDatabase()
        //{
        //    var fileName = ScenarioContext.Current.Get<string>("imageExchangeBatchFilename");

        //    OutboundVoucherFile actual;

        //    using (var context = DatabaseHelper.CreateContext())
        //    {
        //        actual = context.OutboundVoucherFiles.SingleOrDefault(f => f.FileName == fileName);
        //    }

        //    Assert.IsNotNull(actual);

        //    Console.WriteLine("OutboundVoucherFile.Id: {0}", actual.Id);
        //    Console.WriteLine("OutboundVoucherFile.EndpointId: {0}", actual.EndpointId);
        //    Console.WriteLine("OutboundVoucherFile.FileLocation: {0}", actual.FileLocation);
        //    Console.WriteLine("OutboundVoucherFile.FileName: {0}", actual.FileName);
        //    Console.WriteLine("OutboundVoucherFile.ProcessingDate: {0}", actual.ProcessingDate);
        //    Console.WriteLine("OutboundVoucherFile.Status: {0}", actual.Status);

        //    return actual;
        //}
    }
}
