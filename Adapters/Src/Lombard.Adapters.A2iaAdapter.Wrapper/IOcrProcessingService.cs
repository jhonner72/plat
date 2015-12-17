using System;
using Lombard.Adapters.A2iaAdapter.Wrapper.Domain;

namespace Lombard.Adapters.A2iaAdapter.Wrapper
{
    public interface IOcrProcessingService : IDisposable
    {
        void Initialise();
        void ProcessBatch(OcrBatch batch);
        void Shutdown();
        event BatchCompleteDelegate BatchComplete;
    }
}
