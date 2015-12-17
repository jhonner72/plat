using System.Drawing;

namespace FujiXerox.Adapters.A2iaAdapter.Model
{
    public class OcrResult
    {
        public string Result { get; set; }
        public string Score { get; set; }
        public Rectangle Location { get; set; }
    }
}
