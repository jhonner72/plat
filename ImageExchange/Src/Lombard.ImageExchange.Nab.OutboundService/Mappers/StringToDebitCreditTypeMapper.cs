using System;
using Lombard.ImageExchange.Nab.OutboundService.Constants;
using Lombard.ImageExchange.Nab.OutboundService.Domain;

namespace Lombard.ImageExchange.Nab.OutboundService.Mappers
{
    public class StringToDebitCreditTypeMapper : IMapper<string, DebitCreditType>
    {
        public DebitCreditType Map(string transactionCode)
        {
            var debitCreditType = DebitCreditType.Debit;

            if (!string.IsNullOrEmpty(transactionCode))
            {
                int tranCode;
                if (Int32.TryParse(transactionCode, out tranCode))
                {
                    debitCreditType = (CoinValueConstants.CreditLower1 <= tranCode && tranCode <= CoinValueConstants.CreditHigher1) ||
                                      (CoinValueConstants.CreditLower2 <= tranCode && tranCode <= CoinValueConstants.CreditHigher2)
                        ? DebitCreditType.Credit
                        : DebitCreditType.Debit;
                }
            }

            return debitCreditType;
        }
    }
}