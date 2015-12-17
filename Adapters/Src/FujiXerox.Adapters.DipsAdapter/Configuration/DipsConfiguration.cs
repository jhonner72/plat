namespace FujiXerox.Adapters.DipsAdapter.Configuration
{
    public class DipsConfiguration : IAdapterConfiguration, IQuartzConfiguration, IQueueConfiguration
    {
        public string SqlConnectionString { get; set; }
        public bool HandleValidateCodelineRequest { get; set; }
        public bool HandleValidateCodelineResponse { get; set; }
        public bool HandleCorrectCodelineRequest { get; set; }
        public bool HandleCorrectCodelineResponse { get; set; }
        public bool HandleValidateTransactionRequest { get; set; }
        public bool HandleValidateTransactionResponse { get; set; }
        public bool HandleCorrectTransactionRequest { get; set; }
        public bool HandleCorrectTransactionResponse { get; set; }
        public bool HandleCheckThirdPartyRequest { get; set; }
        public bool HandleCheckThirdPartyResponse { get; set; }
        public bool HandleGenerateCorrespondingVoucherRequest { get; set; }
        public bool HandleGenerateCorrespondingVoucherResponse { get; set; }
        public bool HandleGetVoucherInformationRequest { get; set; }
        public bool HandleGetVoucherInformationResponse { get; set; }
        public string ValidateCodelineQueueName { get; set; }
        public string ValidateCodelineExchangeName { get; set; }
        public string CorrectCodelineQueueName { get; set; }
        public string CorrectCodelineExchangeName { get; set; }
        public string ValidateTransactionQueueName { get; set; }
        public string ValidateTransactionExchangeName { get; set; }
        public string CorrectTransactionQueueName { get; set; }
        public string CorrectTransactionExchangeName { get; set; }
        public string CheckThirdPartyQueueName { get; set; }
        public string CheckThirdPartyExchangeName { get; set; }
        public string GenerateCorrespondingVoucherQueueName { get; set; }
        public string GenerateCorrespondingVoucherExchangeName { get; set; }
        public string GetPoolVouchersQueueName { get; set; }
        public string GetPoolVouchersExchangeName { get; set; }
        public string ImagePath { get; set; }
        public string DipsPriority { get; set; }
        public string Dbioff32Version { get; set; }
        public bool DeleteDatabaseRows { get; set; }
        public string PackageSourceDirectory { get; set; }
        public string ImageMergeFrontFilename { get; set; }
        public string ImageMergeRearFilename { get; set; }
        public string ImageMergeFrontFileRegex { get; set; }
        public string ImageMergeRearFileFormat { get; set; }
        public int PollingIntervalSecs { get; set; }
        public string HostName { get; set; }
        public string UserName { get; set; }
        public string Password { get; set; }
        public int Timeout { get; set; }
        public int HeartbeatSeconds { get; set; }
        public string ConsumerName { get; set; }
        public string ExchangeName { get; set; }
        public string InvalidExchangeName { get; set; }
        public string InvalidRoutingKey { get; set; }
        public string RecoverableRoutingKey { get; set; }
    }
}
