using System.Configuration;
using System.ServiceProcess;
using Autofac.Core;
using FujiXerox.Adapters.DipsAdapter.Configuration;
using FujiXerox.Adapters.DipsAdapter.Processing;
using Serilog;

namespace FujiXerox.Adapters.DipsAdapter
{
    public partial class DipsService : ServiceBase
    {
        private ValidateCodelineProcessingService ValidateCodeline { get; set; }
        private ValidateTransctionRequestProcessingService ValidateTransaction { get; set; }
        private CheckThirdPartyProcessingService CheckThirdParty { get; set; }
        private CorrectCodelineProcessingService CorrectCodeline { get; set; }
        private CorrectTransactionProcessingService CorrectTransaction { get; set; }
        private GenerateCorrespondingVoucherProcessingService GenerateVoucher { get; set; }
        private GetVouchersInformationProcessingService GetVoucher { get; set; }

        public DipsService()
        {
            var log = new LoggerConfiguration().Destructure.UsingAttributes().ReadAppSettings().CreateLogger();
            Log.Logger = log;
            InitializeComponent();
            var configuration = SetConfiguration();
            ValidateCodeline = new ValidateCodelineProcessingService(configuration, log);
            ValidateTransaction = new ValidateTransctionRequestProcessingService(configuration, log);
            CheckThirdParty = new CheckThirdPartyProcessingService(configuration, log);
            CorrectCodeline = new CorrectCodelineProcessingService(configuration, log);
            CorrectTransaction = new CorrectTransactionProcessingService(configuration, log);
            GenerateVoucher = new GenerateCorrespondingVoucherProcessingService(configuration, log);
            GetVoucher = new GetVouchersInformationProcessingService(configuration, log);
            log.Information("Dips Service Initialised");
        }

        private DipsConfiguration SetConfiguration()
        {
            return new DipsConfiguration
            {
                SqlConnectionString = ConfigurationManager.ConnectionStrings["dips"].ConnectionString,
                PollingIntervalSecs = int.Parse(ConfigurationManager.AppSettings["quartz:PollingIntervalSecs"]),
                HandleValidateCodelineRequest =
                    bool.Parse(ConfigurationManager.AppSettings["adapter:HandleValidateCodelineRequest"]),
                HandleValidateCodelineResponse =
                    bool.Parse(ConfigurationManager.AppSettings["adapter:HandleValidateCodelineResponse"]),
                HandleCorrectCodelineRequest =
                    bool.Parse(ConfigurationManager.AppSettings["adapter:HandleCorrectCodelineRequest"]),
                HandleCorrectCodelineResponse =
                    bool.Parse(ConfigurationManager.AppSettings["adapter:HandleCorrectCodelineResponse"]),
                HandleValidateTransactionRequest =
                    bool.Parse(ConfigurationManager.AppSettings["adapter:HandleValidateTransactionRequest"]),
                HandleValidateTransactionResponse =
                    bool.Parse(ConfigurationManager.AppSettings["adapter:HandleValidateTransactionResponse"]),
                HandleCorrectTransactionRequest =
                    bool.Parse(ConfigurationManager.AppSettings["adapter:HandleCorrectTransactionRequest"]),
                HandleCorrectTransactionResponse =
                    bool.Parse(ConfigurationManager.AppSettings["adapter:HandleCorrectTransactionResponse"]),
                HandleCheckThirdPartyRequest =
                    bool.Parse(ConfigurationManager.AppSettings["adapter:HandleCheckThirdPartyRequest"]),
                HandleCheckThirdPartyResponse =
                    bool.Parse(ConfigurationManager.AppSettings["adapter:HandleCheckThirdPartyResponse"]),
                HandleGenerateCorrespondingVoucherRequest =
                    bool.Parse(ConfigurationManager.AppSettings["adapter:HandleGenerateCorrespondingVoucherRequest"]),
                HandleGenerateCorrespondingVoucherResponse =
                    bool.Parse(ConfigurationManager.AppSettings["adapter:HandleGenerateCorrespondingVoucherResponse"]),
                HandleGetVoucherInformationRequest =
                    bool.Parse(ConfigurationManager.AppSettings["adapter:HandleGetVoucherInformationRequest"]),
                HandleGetVoucherInformationResponse =
                    bool.Parse(ConfigurationManager.AppSettings["adapter:HandleGetVoucherInformationResponse"]),
                ValidateCodelineQueueName = ConfigurationManager.AppSettings["adapter:ValidateCodelineQueueName"],
                ValidateCodelineExchangeName = ConfigurationManager.AppSettings["adapter:ValidateCodelineExchangeName"],
                CorrectCodelineQueueName = ConfigurationManager.AppSettings["adapter:CorrectCodelineQueueName"],
                CorrectCodelineExchangeName = ConfigurationManager.AppSettings["adapter:CorrectCodelineExchangeName"],
                ValidateTransactionQueueName = ConfigurationManager.AppSettings["adapter:ValidateTransactionQueueName"],
                ValidateTransactionExchangeName =
                    ConfigurationManager.AppSettings["adapter:ValidateTransactionExchangeName"],
                CorrectTransactionQueueName = ConfigurationManager.AppSettings["adapter:CorrectTransactionQueueName"],
                CorrectTransactionExchangeName =
                    ConfigurationManager.AppSettings["adapter:CorrectTransactionExchangeName"],
                CheckThirdPartyQueueName = ConfigurationManager.AppSettings["adapter:CheckThirdPartyQueueName"],
                CheckThirdPartyExchangeName = ConfigurationManager.AppSettings["adapter:CheckThirdPartyExchangeName"],
                GenerateCorrespondingVoucherQueueName =
                    ConfigurationManager.AppSettings["adapter:GenerateCorrespondingVoucherQueueName"],
                GenerateCorrespondingVoucherExchangeName =
                    ConfigurationManager.AppSettings["adapter:GenerateCorrespondingVoucherExchangeName"],
                GetPoolVouchersQueueName = ConfigurationManager.AppSettings["adapter:GetPoolVouchersQueueName"],
                GetPoolVouchersExchangeName = ConfigurationManager.AppSettings["adapter:GetPoolVouchersExchangeName"],
                ImagePath = ConfigurationManager.AppSettings["adapter:ImagePath"],
                DipsPriority = ConfigurationManager.AppSettings["adapter:DipsPriority"],
                Dbioff32Version = ConfigurationManager.AppSettings["adapter:Dbioff32Version"],
                DeleteDatabaseRows = bool.Parse(ConfigurationManager.AppSettings["adapter:DeleteDatabaseRows"]),
                PackageSourceDirectory = ConfigurationManager.AppSettings["adapter:PackageSourceDirectory"],
                ImageMergeFrontFilename = ConfigurationManager.AppSettings["adapter:ImageMergeFrontFilename"],
                ImageMergeRearFilename = ConfigurationManager.AppSettings["adapter:ImageMergeRearFilename"],
                ImageMergeFrontFileRegex = ConfigurationManager.AppSettings["adapter:ImageMergeFrontFileRegex"],
                ImageMergeRearFileFormat = ConfigurationManager.AppSettings["adapter:ImageMergeRearFileFormat"],
                Timeout = int.Parse(ConfigurationManager.AppSettings["queue:Timeout"]),
                HostName = ConfigurationManager.AppSettings["queue:HostName"],
                UserName = ConfigurationManager.AppSettings["queue:UserName"],
                Password = ConfigurationManager.AppSettings["queue:Password"],
                HeartbeatSeconds = int.Parse(ConfigurationManager.AppSettings["queue:HeartbeatSeconds"]),
                InvalidExchangeName = ConfigurationManager.AppSettings["queue:InvalidExchangeName"],
                InvalidRoutingKey = ConfigurationManager.AppSettings["queue:InvalidRoutingKey"],
                RecoverableRoutingKey = ConfigurationManager.AppSettings["queue:RecoverableRoutingKey"]
            };
        }

        protected override void OnStart(string[] args)
        {
            Log.Information("Dips Service Started");

            ValidateCodeline.Start();
            ValidateTransaction.Start();
            CheckThirdParty.Start();
            CorrectCodeline.Start();
            CorrectTransaction.Start();
            GenerateVoucher.Start();
            GetVoucher.Start();
        }

        protected override void OnStop()
        {
            ValidateCodeline.Stop();
            ValidateTransaction.Stop();
            CheckThirdParty.Stop();
            CorrectCodeline.Stop();
            CorrectTransaction.Stop();
            GenerateVoucher.Stop();
            GetVoucher.Stop();
        }
    }
}
