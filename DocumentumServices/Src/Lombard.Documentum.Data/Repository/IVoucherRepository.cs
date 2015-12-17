using System;

using Lombard.Common.Domain;

namespace Lombard.Documentum.Data.Repository
{
    public interface IVoucherRepository
    {
        Voucher GetVoucher(string auxDom, string bsb, string accountNumber, string amount, bool includeFile, DateTime earliestDate);

        Voucher GetVoucher(string objectId, bool includeFile);

        void Update(Voucher voucher);
    }
}
