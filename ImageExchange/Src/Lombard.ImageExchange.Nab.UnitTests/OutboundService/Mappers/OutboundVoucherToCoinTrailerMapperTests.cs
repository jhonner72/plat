using System.Collections.Generic;
using System.Linq;
using System;
using Lombard.ImageExchange.Nab.OutboundService.Constants;
using Lombard.ImageExchange.Nab.OutboundService.Domain;
using Lombard.ImageExchange.Nab.OutboundService.Helpers;
using Lombard.ImageExchange.Nab.OutboundService.Mappers;
using Lombard.ImageExchange.Nab.OutboundService.Messages.XsdImports;
using Lombard.Common.DateAndTime;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Lombard.ImageExchange.Nab.UnitTests.OutboundService.Mappers
{
    [TestClass]
    public class VoucherItemsToCoinTrailerMapperTests
    {
        private IMapper<OutboundVoucherFile, CoinTrailer> mapper;
        private CoinTrailer coinTrailer;

        [TestInitialize]
        public void TestInitialize()
        {
            var coinFileTotalsCalculator = new CoinFileTotalsCalculator();
            mapper = new OutboundVoucherToCoinTrailerMapper(coinFileTotalsCalculator);
        }

        [TestMethod]
        public void CreatesValidCoinTrailerFromVoucherItems()
        {          
            var someVoucherItems = new List<OutboundVoucher> { 
                new OutboundVoucher { Amount = 12, DebitCreditType = DebitCreditType.Credit, VoucherIndicatorValue = " " }, 
                new OutboundVoucher { Amount = 34, DebitCreditType = DebitCreditType.Credit, VoucherIndicatorValue = " " },
                new OutboundVoucher { Amount = 56, DebitCreditType = DebitCreditType.Debit, VoucherIndicatorValue = " " }, 
                new OutboundVoucher { Amount = 78, DebitCreditType = DebitCreditType.Debit, VoucherIndicatorValue = " " },
                new OutboundVoucher { Amount = 90, DebitCreditType = DebitCreditType.Debit, VoucherIndicatorValue = "D", VoucherIndicator = VoucherIndicator.ImageIsDelayed },
            };
            OutboundVoucherFile ovfile = GetOutboundVoucherFile();
            ovfile.Vouchers = someVoucherItems;
            coinTrailer = mapper.Map(ovfile);

            Assert.AreEqual(7, coinTrailer.Metadata.Keys.Count());

            Assert.IsTrue(coinTrailer.Metadata.ContainsKey(CoinFieldNames.RecordTypeIdentifier));
            Assert.IsTrue(coinTrailer.Metadata.ContainsKey(CoinFieldNames.Version));
            Assert.IsTrue(coinTrailer.Metadata.ContainsKey(CoinFieldNames.FileCreditTotalAmount));
            Assert.IsTrue(coinTrailer.Metadata.ContainsKey(CoinFieldNames.FileDebitTotalAmount));
            Assert.IsTrue(coinTrailer.Metadata.ContainsKey(CoinFieldNames.FileCountNonValueItems));
            Assert.IsTrue(coinTrailer.Metadata.ContainsKey(CoinFieldNames.FileCountCreditItems));
            Assert.IsTrue(coinTrailer.Metadata.ContainsKey(CoinFieldNames.FileCountDebitItems));

            Assert.AreEqual(CoinValueConstants.TrailerRecordType, coinTrailer.Metadata[CoinFieldNames.RecordTypeIdentifier]);
            Assert.AreEqual(CoinValueConstants.TrailerVersion, coinTrailer.Metadata[CoinFieldNames.Version]);
            Assert.AreEqual("46", coinTrailer.Metadata[CoinFieldNames.FileCreditTotalAmount]);
            Assert.AreEqual("224", coinTrailer.Metadata[CoinFieldNames.FileDebitTotalAmount]);
            Assert.AreEqual("0", coinTrailer.Metadata[CoinFieldNames.FileCountNonValueItems]);
            Assert.AreEqual("2", coinTrailer.Metadata[CoinFieldNames.FileCountCreditItems]);
            Assert.AreEqual(coinTrailer.Metadata[CoinFieldNames.FileCountDebitItems], "3");
        }

        [TestMethod]
        private OutboundVoucherFile GetOutboundVoucherFile()
        {
            return new OutboundVoucherFile
            {
                BatchNumber = 12345678, // intentionally using more than 6 digits 
                ProcessingDate = new DateTime(2015, 03, 19),
                EndpointShortName = "FSV",
                 OperationType = ImageExchangeType.ImageExchange.ToString()
            };
        }

    }
}