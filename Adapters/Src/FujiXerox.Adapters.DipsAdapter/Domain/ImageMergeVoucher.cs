using System;
using System.Xml.Serialization;

namespace FujiXerox.Adapters.DipsAdapter.Domain
{
    [Serializable]
    public class ImageMergeVoucher
    {
        public string TraceNumber { get; set; }
        public long FrontOffset { get; set; }
        public long  FrontLength { get; set; }
        public long RearOffset { get; set; }
        public long  RearLength { get; set; }

        [XmlIgnore]
        public string FrontImageFilename { get; set; }
        [XmlIgnore]
        public string RearImageFilename { get; set; }

    }
}
