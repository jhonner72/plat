using System;

namespace Lombard.Adapters.A2iaAdapter.Messages
{
    [Serializable]
    public class RecogniseCourtesyAmountResponse
    {
        public string DocumentReferenceNumber { get; set; }
        public string CapturedAmount { get; set; }
        public string ConfidenceLevel { get; set; }
        public int ImageRotation { get; set; }

        public bool Success { get; set; }
        public string ErrorMessage { get; set; }


        //public string FileName { get; set; }
        //public string FileType { get; set; }

        //public string DateResult { get; set; }
        //public string CodelineResult { get; set; }
        //public string DateScore { get; set; }
        //public string CodelineScore { get; set; }
    }
}
