using System;

namespace Lombard.Adapters.A2iaAdapter.Messages
{
    [Serializable]
    public class RecogniseCourtesyAmountRequest
    {
        public string DocumentReferenceNumber { get; set; }
        public string FrontImageIdentifier { get; set; }
    }
}
