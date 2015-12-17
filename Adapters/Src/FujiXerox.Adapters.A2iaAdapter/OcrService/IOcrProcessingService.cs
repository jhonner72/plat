using System;
using FujiXerox.Adapters.A2iaAdapter.Model;

namespace FujiXerox.Adapters.A2iaAdapter.OcrService
{
    public interface IOcrProcessingService : IDisposable
    {
        void Initialise();
        bool ProcessBatch(OcrBatch batch);
        void Shutdown();
        event BatchCompleteDelegate BatchComplete;
    }
}
