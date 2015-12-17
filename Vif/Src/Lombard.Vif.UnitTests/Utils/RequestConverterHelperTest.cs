using System.Collections.Generic;
using Lombard.Vif.Service.Messages.XsdImports;
using Lombard.Vif.Service.Utils;
using Lombard.Vif.UnitTests.Builders;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Lombard.Vif.UnitTests.Utils
{
    [TestClass]
    public class RequestConverterHelperTest
    {
        [TestMethod]
        public void Given1Cr1DrAndBothADandEADExist_WhenGetPrimeCreditIsCalled_ThenOnlyCrIsReturnedAsPrimeCredit()
        {
            var vouchers = new List<VoucherInformation>();

            var expectedPrimeCredit =
                new VoucherInformationBuilder().WithVoucher(
                    new VoucherBuilder().WithCreditDocumentType().Build()).Build();

            vouchers.Add(new VoucherInformationBuilder().Build());
            vouchers.Add(expectedPrimeCredit);

            var primeCreditHelper = new RequestConverterHelper();

            var actualPrimeCredit = primeCreditHelper.GetPrimeCredit(vouchers);

            Assert.AreEqual(expectedPrimeCredit, actualPrimeCredit);
        }

        [TestMethod]
        public void Given1Dr2CrAnd1stCrHasEad_WhenGetPrimeCreditIsCalled_Then1stCrIsReturnedAsPrimeCredit()
        {
            var vouchers = new List<VoucherInformation>();

            var expectedPrimeCredit =
                new VoucherInformationBuilder().WithVoucher(
                    new VoucherBuilder().WithCreditDocumentType().WithEAD("111").Build()).Build();

            vouchers.Add(new VoucherInformationBuilder().Build());
            vouchers.Add(expectedPrimeCredit);
            vouchers.Add(new VoucherInformationBuilder().WithVoucher(
                    new VoucherBuilder().WithCreditDocumentType().Build()).Build());

            var primeCreditHelper = new RequestConverterHelper();

            var actualPrimeCredit = primeCreditHelper.GetPrimeCredit(vouchers);

            Assert.AreEqual(expectedPrimeCredit, actualPrimeCredit);
        }

        [TestMethod]
        public void Given1Dr2CrAndBothCrHasEad_WhenGetPrimeCreditIsCalled_Then2ndCrIsReturnedAsPrimeCredit()
        {
            var vouchers = new List<VoucherInformation>();

            var expectedPrimeCredit =
                new VoucherInformationBuilder().WithVoucher(
                    new VoucherBuilder().WithCreditDocumentType().WithEAD("222").Build()).Build();

            vouchers.Add(new VoucherInformationBuilder().Build());
            vouchers.Add(new VoucherInformationBuilder().WithVoucher(
                    new VoucherBuilder().WithCreditDocumentType().WithEAD("111").Build()).Build());
            vouchers.Add(expectedPrimeCredit);

            var primeCreditHelper = new RequestConverterHelper();

            var actualPrimeCredit = primeCreditHelper.GetPrimeCredit(vouchers);

            Assert.AreEqual(expectedPrimeCredit, actualPrimeCredit);
        }

        [TestMethod]
        public void Given1Dr2CrAnd1stCrHasAd_WhenGetPrimeCreditIsCalled_Then1stCrIsReturnedAsPrimeCredit()
        {
            var vouchers = new List<VoucherInformation>();

            var expectedPrimeCredit =
                new VoucherInformationBuilder().WithVoucher(
                    new VoucherBuilder().WithCreditDocumentType().WithAD("111").Build()).Build();

            vouchers.Add(new VoucherInformationBuilder().Build());
            vouchers.Add(expectedPrimeCredit);
            vouchers.Add(new VoucherInformationBuilder().WithVoucher(
                    new VoucherBuilder().WithCreditDocumentType().Build()).Build());

            var primeCreditHelper = new RequestConverterHelper();

            var actualPrimeCredit = primeCreditHelper.GetPrimeCredit(vouchers);

            Assert.AreEqual(expectedPrimeCredit, actualPrimeCredit);
        }

        [TestMethod]
        public void Given1Dr2CrAndBothCrHasAd_WhenGetPrimeCreditIsCalled_Then2ndCrIsReturnedAsPrimeCredit()
        {
            var vouchers = new List<VoucherInformation>();

            var expectedPrimeCredit =
                new VoucherInformationBuilder().WithVoucher(
                    new VoucherBuilder().WithCreditDocumentType().WithAD("222").Build()).Build();

            vouchers.Add(new VoucherInformationBuilder().Build());
            vouchers.Add(new VoucherInformationBuilder().WithVoucher(
                    new VoucherBuilder().WithCreditDocumentType().WithAD("111").Build()).Build());
            vouchers.Add(expectedPrimeCredit);

            var primeCreditHelper = new RequestConverterHelper();

            var actualPrimeCredit = primeCreditHelper.GetPrimeCredit(vouchers);

            Assert.AreEqual(expectedPrimeCredit, actualPrimeCredit);
        }

        [TestMethod]
        public void Given1Dr2CrAnd1stCrHasEadAnd2ndCrHasAd_WhenGetPrimeCreditIsCalled_Then1stCrIsReturnedAsPrimeCredit()
        {
            var vouchers = new List<VoucherInformation>();

            var expectedPrimeCredit =
                new VoucherInformationBuilder().WithVoucher(
                    new VoucherBuilder().WithCreditDocumentType().WithEAD("222").Build()).Build();

            vouchers.Add(new VoucherInformationBuilder().Build());
            vouchers.Add(new VoucherInformationBuilder().WithVoucher(
                    new VoucherBuilder().WithCreditDocumentType().WithAD("111").Build()).Build());
            vouchers.Add(expectedPrimeCredit);

            var primeCreditHelper = new RequestConverterHelper();

            var actualPrimeCredit = primeCreditHelper.GetPrimeCredit(vouchers);

            Assert.AreEqual(expectedPrimeCredit, actualPrimeCredit);
        }

        [TestMethod]
        public void Given1Dr2CrAndNoCrHasEadNorAd_WhenGetPrimeCreditIsCalled_ThenLastCrIsReturnedAsPrimeCredit()
        {
            var vouchers = new List<VoucherInformation>();

            var expectedPrimeCredit =
                new VoucherInformationBuilder().WithVoucher(
                    new VoucherBuilder().WithCreditDocumentType().Build()).Build();

            vouchers.Add(new VoucherInformationBuilder().Build());
            vouchers.Add(new VoucherInformationBuilder().WithVoucher(
                    new VoucherBuilder().WithCreditDocumentType().Build()).Build());
            vouchers.Add(expectedPrimeCredit);

            var primeCreditHelper = new RequestConverterHelper();

            var actualPrimeCredit = primeCreditHelper.GetPrimeCredit(vouchers);

            Assert.AreEqual(expectedPrimeCredit, actualPrimeCredit);
        }

        [TestMethod]
        public void GivenHighValueCreditWithAltExAuxDomIsTheLastCredit_WhenGetPrimeCreditIsCalled_ThenLastCrIsReturnedAsPrimeCredit()
        {
            var vouchers = new List<VoucherInformation>();

            var expectedPrimeCredit =
                new VoucherInformationBuilder()
                    .WithVoucher(new VoucherBuilder().WithCreditDocumentType().Build())
                    .WithVoucherProcess(new VoucherProcessBuilder()
                        .WithHighValueFlag()
                        .WithAlternateExAuxDom("111111")
                        .Build())
                .Build();

            vouchers.Add(new VoucherInformationBuilder().Build());
            vouchers.Add(new VoucherInformationBuilder()
                    .WithVoucher(new VoucherBuilder().WithCreditDocumentType().Build()).Build());
            vouchers.Add(expectedPrimeCredit);

            var primeCreditHelper = new RequestConverterHelper();

            var actualPrimeCredit = primeCreditHelper.GetPrimeCredit(vouchers);

            Assert.AreEqual(expectedPrimeCredit, actualPrimeCredit);
        }

        [TestMethod]
        public void GivenHighValueCreditWithAltAuxDomIsTheLastCredit_WhenGetPrimeCreditIsCalled_ThenLastCrIsReturnedAsPrimeCredit()
        {
            var vouchers = new List<VoucherInformation>();

            var expectedPrimeCredit =
                new VoucherInformationBuilder()
                    .WithVoucher(new VoucherBuilder().WithCreditDocumentType().Build())
                    .WithVoucherProcess(new VoucherProcessBuilder()
                        .WithHighValueFlag()
                        .WithAlternateAuxDom("111111")
                        .Build())
                .Build();

            vouchers.Add(new VoucherInformationBuilder().Build());
            vouchers.Add(new VoucherInformationBuilder()
                    .WithVoucher(new VoucherBuilder().WithCreditDocumentType().Build()).Build());
            vouchers.Add(expectedPrimeCredit);

            var primeCreditHelper = new RequestConverterHelper();

            var actualPrimeCredit = primeCreditHelper.GetPrimeCredit(vouchers);

            Assert.AreEqual(expectedPrimeCredit, actualPrimeCredit);
        }
    }
}
