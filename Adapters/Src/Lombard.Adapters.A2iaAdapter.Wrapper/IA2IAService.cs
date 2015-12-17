using System;
using System.Threading.Tasks;
using Lombard.Adapters.A2iaAdapter.Wrapper.Domain;

namespace Lombard.Adapters.A2iaAdapter.Wrapper
{
    /// <summary>
    /// IA2IAService
    /// </summary>
    // ReSharper disable once InconsistentNaming
    public interface IA2IAService : IDisposable
    {
        /// <summary>
        /// Initialises the specified parameter file path.
        /// </summary>
        /// <param name="paramFilePath">The parameter file path.</param>
        /// <param name="tblFilePath">The table file path.</param>
        /// <param name="cpuCores">The cpu cores.</param>
        /// <param name="extractAmount">if set to <c>true</c> [extract amount].</param>
        /// <param name="extractDate">if set to <c>true</c> [extract date].</param>
        /// <param name="extractCodeline">if set to <c>true</c> [extract codeline].</param>
        void Initialise(string paramFilePath, string tblFilePath, string[] cpuCores, bool extractAmount, bool extractDate, bool extractCodeline);

        /// <summary>
        /// Perform OCR processing on a batch
        /// </summary>
        /// <param name="batch">The batch to process</param>
        /// <returns></returns>
        Task ProcessBatchAsync(OcrBatch batch);

    }
}
