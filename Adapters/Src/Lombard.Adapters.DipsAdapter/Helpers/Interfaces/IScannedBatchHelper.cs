using System;
using Lombard.Adapters.DipsAdapter.Messages;

namespace Lombard.Adapters.DipsAdapter.Helpers.Interfaces
{
    public interface IScannedBatchHelper
    {
        VoucherInformation[] ReadScannedBatch(GenerateBatchBulkCreditRequest request, string jobIdentifier,
            DateTime processingDate);
    }
}