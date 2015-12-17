namespace Lombard.Adapters.A2iaAdapter.Wrapper
{
    public class Constants
    {
        /// <summary>Indicates that the corresponding field should be / was recognised.</summary>
        public const string Enabled = "Yes";
        
        /// <summary>Arbitrary strings that the A2iA engine expects in order to enable recognition of the various form fields.</summary>
        public class Config
        {
            public const string Documenttype = "input.documentType";
            public const string Country = "input.DocumentTypeInfo.CaseCheck.Check.CheckInput.Country";
            public const string Currency = "input.DocumentTypeInfo.CaseCheck.Check.CheckInput.Currency";
            public const string Optimisation = "input.DocumentTypeInfo.CaseCheck.Check.CheckInput.Optimisation";
            public const string IncludeDetails = "input.verbose";
            public const string ImageResolution = "Image.resolution_Dpi";
        } 
        
        /// <summary>Arbitrary strings that the A2iA engine expects in order to enable recognition of the various form fields.</summary>
        public class EngineFields
        {
            public const string Amount = "input.DocumentTypeInfo.CaseCheck.Check.AmountRecognition";
            public const string Date = "input.DocumentTypeInfo.CaseCheck.Check.fields.date";
            public const string CodeLine = "input.DocumentTypeInfo.CaseCheck.Check.fields.codeline";
            public const string Signature = "input.documentTypeInfo.CaseCheck.Check.fields.signature";
        } 

        /// <summary>Arbitrary strings that the A2iA engine expects in order to return the OCR results.</summary>
        public class ResultFields
        {
            public const string CheckBase = "documentTypeInfo.CaseCheck.Check.result.";
            public const string CreditBase = "documentTypeInfo.CaseCheck.custom.fields[1].fieldTypeInfo.CaseAmount.result.";

            public const string AmountLocationX1 = "documentTypeInfo.CaseCheck.Check.CARResult.CARImage.location.x_tl";
            public const string AmountLocationY1 = "documentTypeInfo.CaseCheck.Check.CARResult.CARImage.location.y_tl";
            public const string AmountLocationX2 = "documentTypeInfo.CaseCheck.Check.CARResult.CARImage.location.x_br";
            public const string AmountLocationY2 = "documentTypeInfo.CaseCheck.Check.CARResult.CARImage.location.y_br";

            public const string CodelineRecognition = "documentTypeInfo.CaseCheck.Check.codeline.result.reco";
            public const string CodelineConfidence = "documentTypeInfo.CaseCheck.Check.codeline.result.score";
            public const string CodelineLocationX1 = "documentTypeInfo.CaseCheck.Check.fieldsResult.codelineImage.location.x_tl";
            public const string CodelineLocationY1 = "documentTypeInfo.CaseCheck.Check.fieldsResult.codelineImage.location.y_tl";
            public const string CodelineLocationX2 = "documentTypeInfo.CaseCheck.Check.fieldsResult.codelineImage.location.x_br";
            public const string CodelineLocationY2 = "documentTypeInfo.CaseCheck.Check.fieldsResult.codelineImage.location.y_br";
            
            public const string DateConfidence = "documentTypeInfo.CaseCheck.Check.date.result.score";
            public const string DateRecognitionYear = "documentTypeInfo.CaseCheck.Check.date.result.reco.year";
            public const string DateRecognitionMonth = "documentTypeInfo.CaseCheck.Check.date.result.reco.month";
            public const string DateRecognitionDay = "documentTypeInfo.CaseCheck.Check.date.result.reco.day";
            public const string DateLocationX1 = "documentTypeInfo.CaseCheck.Check.fieldsResult.dateImage.location.x_tl";
            public const string DateLocationY1 = "documentTypeInfo.CaseCheck.Check.fieldsResult.dateImage.location.y_tl";
            public const string DateLocationX2 = "documentTypeInfo.CaseCheck.Check.fieldsResult.dateImage.location.x_br";
            public const string DateLocationY2 = "documentTypeInfo.CaseCheck.Check.fieldsResult.dateImage.location.y_br";

            public const string Signature = "documentTypeInfo.CaseCheck.Check.signature.answer";
            public const string SignatureConfidence = "documentTypeInfo.CaseCheck.Check.signature.score";

            public const string OrientationCorrection = "Output.orientationCorrection";
        } 
    } 
}
