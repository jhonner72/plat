using System.Diagnostics.CodeAnalysis;

namespace Lombard.Adapters.A2iaAdapter.Wrapper.Enums
{
    ///<summary>The list of OCR Types</summary>
    /// <remarks>From A2iACheckReader.h</remarks>
    [SuppressMessage("ReSharper", "InconsistentNaming")]
    public enum OcrFonts : uint
    {
        Generic = 1,
        CMC7 = 4,
        E13B = 5,
        OCRA = 6,
        OCRB = 7
    }
}