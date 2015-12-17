using Lombard.Adapters.Data.Domain;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Lombard.Adapters.DipsAdapter.UnitTests.Domain
{
    [TestClass]
    public class DipsStatus1BitmaskTests
    {
        [TestMethod]
        public void Test_DipsStatus1Bitmasks()
        {
            //const int ExtraAuxDom = 16;
            //const int AuxDom = 32;
            //const int BsbNumber = 64;
            //const int AccountNumber = 128;
            //const int TransactionCode = 256;
            //const int Amount = 512;

            // NOTE: False == Invalid
            Assert.IsFalse(DipsStatus1Bitmask.GetAccountNumberStatusMasked(128));
            Assert.IsFalse(DipsStatus1Bitmask.GetAmountStatusMasked(512));
            Assert.IsFalse(DipsStatus1Bitmask.GetAuxDomStatusMasked(32));
            Assert.IsFalse(DipsStatus1Bitmask.GetBsbNumberStatusMasked(64));
            Assert.IsFalse(DipsStatus1Bitmask.GetExtraAuxDomStatusMasked(16));
            Assert.IsFalse(DipsStatus1Bitmask.GetTransactionCodeStatusMasked(256));

            // NOTE: True == Valid
            Assert.IsTrue(DipsStatus1Bitmask.GetAccountNumberStatusMasked(0));
            Assert.IsTrue(DipsStatus1Bitmask.GetAmountStatusMasked(0));
            Assert.IsTrue(DipsStatus1Bitmask.GetAuxDomStatusMasked(0));
            Assert.IsTrue(DipsStatus1Bitmask.GetBsbNumberStatusMasked(0));
            Assert.IsTrue(DipsStatus1Bitmask.GetExtraAuxDomStatusMasked(0));
            Assert.IsTrue(DipsStatus1Bitmask.GetTransactionCodeStatusMasked(0));

            // NOTE: Redundant tests!
            Assert.IsFalse(DipsStatus1Bitmask.GetAccountNumberStatusMasked(1 << 7));
            Assert.IsFalse(DipsStatus1Bitmask.GetAmountStatusMasked(1 << 9));
            Assert.IsFalse(DipsStatus1Bitmask.GetAuxDomStatusMasked(1 << 5));
            Assert.IsFalse(DipsStatus1Bitmask.GetBsbNumberStatusMasked(1 << 6));
            Assert.IsFalse(DipsStatus1Bitmask.GetExtraAuxDomStatusMasked(1 << 4));
            Assert.IsFalse(DipsStatus1Bitmask.GetTransactionCodeStatusMasked(1 << 8));
        }

        [TestMethod]
        public void Test_GetStatusMask()
        {
            Assert.AreEqual(0, DipsStatus1Bitmask.GetStatusMask(true, true, true, true, true, true));
            Assert.AreEqual(16, DipsStatus1Bitmask.GetStatusMask(false, true, true, true, true, true)); // bad extraauxdom
            Assert.AreEqual(32, DipsStatus1Bitmask.GetStatusMask(true, false, true, true, true, true)); // bad auxdom
            Assert.AreEqual(128, DipsStatus1Bitmask.GetStatusMask(true, true, false, true, true, true)); // bad account
            Assert.AreEqual(512, DipsStatus1Bitmask.GetStatusMask(true, true, true, false, true, true)); // bad amount
            Assert.AreEqual(64, DipsStatus1Bitmask.GetStatusMask(true, true, true, true, false, true)); // bad bsb
            Assert.AreEqual(256, DipsStatus1Bitmask.GetStatusMask(true, true, true, true, true, false)); // bad tc
            Assert.AreEqual(272, DipsStatus1Bitmask.GetStatusMask(false, true, true, true, true, false)); // bad ead & tc
            Assert.AreEqual(16 + 32 + 64 + 128 + 256 + 512, DipsStatus1Bitmask.GetStatusMask(false, false, false, false, false, false)); // all bad
        }
    }
}
