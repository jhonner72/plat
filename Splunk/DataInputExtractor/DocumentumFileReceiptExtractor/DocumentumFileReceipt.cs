using System;

namespace DocumentumFileReceiptExtractor
{
    class DocumentumFileReceipt
    {
        public string FileId { get; set; }
        public string Filename { get; set; }
        public DateTime ReceivedDateTime { get; set; }
        public DateTime TransmissionDateTime { get; set; }
        public string Exchange { get; set; }
    }
}
