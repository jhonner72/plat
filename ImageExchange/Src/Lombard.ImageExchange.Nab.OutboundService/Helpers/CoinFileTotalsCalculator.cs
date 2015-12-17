using System;
using System.Linq;
using System.Collections.Generic;
using Lombard.ImageExchange.Nab.OutboundService.Domain;

namespace Lombard.ImageExchange.Nab.OutboundService.Helpers
{
    public interface ICoinFileTotalsCalculator
    {
        CoinFileTrailerTotals Process(IEnumerable<OutboundVoucher> items);
    }

    public class CoinFileTotalsCalculator : ICoinFileTotalsCalculator
    {
        public CoinFileTrailerTotals Process(IEnumerable<OutboundVoucher> voucherItems)
        {
            var result = new CoinFileTrailerTotals();

            foreach (var item in voucherItems)
            {
                if (item.VoucherIndicator == VoucherIndicator.ImageBeingSentForPreviouslyDelayed)
                {
                    result.NonValueCount++;
                }
                else
                {
                    if (item.DebitCreditType == DebitCreditType.Debit)
                    {
                        result.TotalDebits += item.Amount;
                        result.DebitCount++;
                    }
                    else
                    {
                        if (item.DebitCreditType == DebitCreditType.Credit)
                        {
                            result.TotalCredits += item.Amount;
                            result.CreditCount++;
                        }
                        else
                        {
                            throw new ArgumentOutOfRangeException("voucherItems",
                                string.Format("item.DebitCreditType = '{0}'  was not expected.", item.DebitCreditType.DisplayName));
                        }
                    }
                }
            }

            return result;
        }
    }
}
