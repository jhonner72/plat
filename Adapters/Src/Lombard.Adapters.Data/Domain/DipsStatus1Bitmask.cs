using System;

namespace Lombard.Adapters.Data.Domain
{
    public static class DipsStatus1Bitmask
    {
        private const int ExtraAuxDom = 16;
        private const int AuxDom = 32;
        private const int BsbNumber = 64;
        private const int AccountNumber = 128;
        private const int TransactionCode = 256;
        private const int Amount = 512;

        public const int CodelineValidationFlags = 1008;

        public static bool GetExtraAuxDomStatusMasked(int status1)
        {
            return Convert.ToBoolean((status1 & ExtraAuxDom) == 0);
        }

        public static bool GetAuxDomStatusMasked(int status1)
        {
            return Convert.ToBoolean((status1 & AuxDom) == 0);
        }

        public static bool GetAccountNumberStatusMasked(int status1)
        {
            return Convert.ToBoolean((status1 & AccountNumber) == 0);
        }

        public static bool GetAmountStatusMasked(int status1)
        {
            return Convert.ToBoolean((status1 & Amount) == 0);
        }

        public static bool GetBsbNumberStatusMasked(int status1)
        {
            return Convert.ToBoolean((status1 & BsbNumber) == 0);
        }

        public static bool GetTransactionCodeStatusMasked(int status1)
        {
            return Convert.ToBoolean((status1 & TransactionCode) == 0);
        }

        public static int GetStatusMask(
            bool extraAuxDomIsValid,
            bool auxDomIsValid,
            bool accountNumberIsValid,
            bool amountIsValid,
            bool bsbNumberIsValid,
            bool transactionCodeIsValid)
        {
            return
                (extraAuxDomIsValid ? 0 : ExtraAuxDom) +
                (auxDomIsValid ? 0 : AuxDom) +
                (accountNumberIsValid ? 0 : AccountNumber) +
                (amountIsValid ? 0 : Amount) +
                (bsbNumberIsValid ? 0 : BsbNumber) +
                (transactionCodeIsValid ? 0 : TransactionCode);
        }
    }
}
