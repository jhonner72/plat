using System.Collections.Generic;
using Lombard.ImageExchange.Nab.OutboundService.Domain;
using Lombard.ImageExchange.Nab.OutboundService.Helpers;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Lombard.ImageExchange.Nab.UnitTests.OutboundService.Helpers
{
    [TestClass]
    public class CoinFileTotalsCalculatorTests
    {
        private readonly OutboundVoucher debitVoucherItem = new OutboundVoucher {DebitCreditType = DebitCreditType.Debit, Amount = 123, VoucherIndicator = VoucherIndicator.ImageIsPresent};
        private readonly OutboundVoucher creditVoucherItem = new OutboundVoucher { DebitCreditType = DebitCreditType.Credit, Amount = 456, VoucherIndicator = VoucherIndicator.ImageIsPresent };
        private readonly OutboundVoucher nonValueVoucherItem = new OutboundVoucher { DebitCreditType = DebitCreditType.Credit, VoucherIndicator = VoucherIndicator.ImageBeingSentForPreviouslyDelayed };

        [TestMethod]
        public void Process_GivenDebitVoucher_ReturnsSummaryWithOneDebitAndItsAmount()
        {
            var calculator = GetCoinFileTotalsCalculator();

            var coinFileTrailerTotals = calculator.Process(new List<OutboundVoucher> {debitVoucherItem});

            Assert.AreEqual(1, coinFileTrailerTotals.DebitCount);
            Assert.AreEqual(123, coinFileTrailerTotals.TotalDebits);

            Assert.AreEqual(0, coinFileTrailerTotals.CreditCount);
            Assert.AreEqual(0, coinFileTrailerTotals.TotalCredits);
            Assert.AreEqual(0, coinFileTrailerTotals.NonValueCount);
        }

        [TestMethod]
        public void Process_GivenCreditVoucher_ReturnsSummaryWithOneCrebitAndItsAmount()
        {
            var calculator = GetCoinFileTotalsCalculator();

            var coinFileTrailerTotals = calculator.Process(new List<OutboundVoucher> { creditVoucherItem });

            Assert.AreEqual(1, coinFileTrailerTotals.CreditCount);
            Assert.AreEqual(456, coinFileTrailerTotals.TotalCredits);
            
            Assert.AreEqual(0, coinFileTrailerTotals.DebitCount);
            Assert.AreEqual(0, coinFileTrailerTotals.TotalDebits);
            Assert.AreEqual(0, coinFileTrailerTotals.NonValueCount);
        }

        [TestMethod]
        public void Process_GivenNonValueVoucher_ReturnsSummaryWithOneNonValueItem()
        {
            var calculator = GetCoinFileTotalsCalculator();

            var coinFileTrailerTotals = calculator.Process(new List<OutboundVoucher> { nonValueVoucherItem });

            Assert.AreEqual(1, coinFileTrailerTotals.NonValueCount);

            Assert.AreEqual(0, coinFileTrailerTotals.DebitCount);
            Assert.AreEqual(0, coinFileTrailerTotals.TotalDebits);
            Assert.AreEqual(0, coinFileTrailerTotals.CreditCount);
            Assert.AreEqual(0, coinFileTrailerTotals.TotalCredits);
        }

        private CoinFileTotalsCalculator GetCoinFileTotalsCalculator()
        {
            return new CoinFileTotalsCalculator();
        }
    }
}
