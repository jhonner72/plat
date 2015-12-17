
namespace FujiXerox.Adapters.DipsAdapter.Configuration
{
    //[KeyPrefix("adapter:")]
    //[AppSettingWrapper]
    public interface IAdapterConfiguration
    {
        string SqlConnectionString { get; set; }
        bool HandleValidateCodelineRequest { get; set; }
        bool HandleValidateCodelineResponse { get; set; }

        bool HandleCorrectCodelineRequest { get; set; }
        bool HandleCorrectCodelineResponse { get; set; }

        bool HandleValidateTransactionRequest { get; set; }
        bool HandleValidateTransactionResponse { get; set; }

        bool HandleCorrectTransactionRequest { get; set; }
        bool HandleCorrectTransactionResponse { get; set; }

        bool HandleCheckThirdPartyRequest { get; set; }
        bool HandleCheckThirdPartyResponse { get; set; }

        bool HandleGenerateCorrespondingVoucherRequest { get; set; }
        bool HandleGenerateCorrespondingVoucherResponse { get; set; }

        bool HandleGetVoucherInformationRequest { get; set; }
        bool HandleGetVoucherInformationResponse { get; set; }

        string ValidateCodelineQueueName { get; set; }
        string ValidateCodelineExchangeName { get; set; }

        string CorrectCodelineQueueName { get; set; }
        string CorrectCodelineExchangeName { get; set; }

        string ValidateTransactionQueueName { get; set; }
        string ValidateTransactionExchangeName { get; set; }

        string CorrectTransactionQueueName { get; set; }
        string CorrectTransactionExchangeName { get; set; }

        string CheckThirdPartyQueueName { get; set; }
        string CheckThirdPartyExchangeName { get; set; }

        string GenerateCorrespondingVoucherQueueName { get; set; }
        string GenerateCorrespondingVoucherExchangeName { get; set; }

        string GetPoolVouchersQueueName { get; set; }
        string GetPoolVouchersExchangeName { get; set; }
                
        string ImagePath { get; set; }
        string DipsPriority { get; set; }
        string Dbioff32Version { get; set; }

        bool DeleteDatabaseRows { get; set; }
        string PackageSourceDirectory {get;set;}
        string ImageMergeFrontFilename { get; set; }
        string ImageMergeRearFilename { get; set; }
        string ImageMergeFrontFileRegex { get; set; }
        string ImageMergeRearFileFormat { get; set; }
    }
}
