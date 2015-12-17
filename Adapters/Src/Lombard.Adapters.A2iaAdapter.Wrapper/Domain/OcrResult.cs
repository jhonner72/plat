using System.Drawing;

namespace Lombard.Adapters.A2iaAdapter.Wrapper.Domain
{
    public class OcrResult
    {
        public string Result { get; set; }
        public string Score { get; set; }
        public Rectangle Location { get; set; }
    }
}
