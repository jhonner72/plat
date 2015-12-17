using Castle.Components.DictionaryAdapter;
using Lombard.Common.Configuration;

namespace Lombard.AdjustmentLetters.Configuration
{
    [KeyPrefix("AdjustmentLetter:")]
    [AppSettingWrapper]
    public interface IAdjustmentLettersConfiguration
    {
        string BitLockerLocation { get; set; }
        string PdfLetterTemplate { get; set; }
        string Environment { get; set; }
        string ImageFileNameTemplate { get; set; }
        string PdfZipFilename { get; set; }
    }
}
