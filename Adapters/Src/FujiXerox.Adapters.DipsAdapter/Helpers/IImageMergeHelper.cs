using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Lombard.Adapters.Data.Domain;

namespace FujiXerox.Adapters.DipsAdapter.Helpers
{
    public interface IImageMergeHelper
    {
        Task EnsureMergedImageFilesExistAsync(string jobIdentifier, string batchNumber, DateTime processingDate);
        void EnsureMergedImageFilesExist(string jobIdentifier, string batchNumber, DateTime processingDate);
        void PopulateMergedImageInfo(string jobIdentifier, string batchNumber, IEnumerable<DipsNabChq> vouchers);
    }
}