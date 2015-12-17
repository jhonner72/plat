using System;
using System.Configuration;
using System.ServiceProcess;
using FujiXerox.Adapters.A2iaAdapter.Configuration;
using FujiXerox.Adapters.A2iaAdapter.Enums;
using FujiXerox.Adapters.A2iaAdapter.MessageQueue;
using FujiXerox.Adapters.A2iaAdapter.OcrService;
using RabbitMQ.Client;
using Serilog;

//using EasyNetQ;

namespace FujiXerox.Adapters.A2iaAdapter
{
    public partial class CarService : ServiceBase
    {
        private IOcrProcessingService ocrService;
        private readonly IA2iaConfiguration ocrConfiguration;
        //private readonly RabbitMq _queue;
        private RabbitMqConsumer consumer;
        private RabbitMqExchange exchange;
        private RabbitMqExchange invalidExchange;
        private readonly ILogger log;

        //private readonly IBus messageBus;

        public CarService()
        {
            InitializeComponent();
            log = new LoggerConfiguration().Destructure.UsingAttributes().ReadAppSettings().CreateLogger();
            Log.Logger = log;
            ProcessingService.Log = log;

            var maxProcessorCount = int.Parse(ConfigurationManager.AppSettings["a2ia:MaxProcessorCount"]);
            var tablePath = ConfigurationManager.AppSettings["adapter:TablePath"];
            var parameterPath = ConfigurationManager.AppSettings["a2ia:ParameterPath"];
            LoadMethod loadMethod;
            Enum.TryParse(ConfigurationManager.AppSettings["adapter:LoadMethod"], out loadMethod);
            var invalidRoutingKey = ConfigurationManager.AppSettings["adapter:InvalidRoutingKey"];

            var fileType = ConfigurationManager.AppSettings["adapter:FileType"];
            var channelTimeout = int.Parse(ConfigurationManager.AppSettings["adapter:ChannelTimeout"]);

            ProcessingService.InvalidRoutingKey = invalidRoutingKey;
            //var connectionString = ConfigurationManager.ConnectionStrings["rabbitMQ"].ConnectionString;
            InitializeQueueConnection();
            ocrConfiguration = new A2iaConfiguration
            {
                MaxProcessorCount = maxProcessorCount,
                TablePath = tablePath,
                ParameterPath = parameterPath,
                LoadMethod = loadMethod,
                FileType = fileType,
                ChannelTimeout = channelTimeout
            };
        }

        void Connection_ConnectionShutdown(object source, ShutdownEventArgs reason)
        {
            consumer.ConnectionLost -= Connection_ConnectionShutdown;
            log.Debug("CarService: Connection lost to RabbitMQ Server due to {0}", reason.Cause);
            try
            {
                consumer.StopConsuming();
            }
            catch (Exception ex)
            {
                log.Warning(ex, "CarService: Shutting down old connection to allow new connection to replace it");
            }
            InitializeQueueConnection();
            ProcessingService.StartConsumer();
        }

        private void InitializeQueueConnection()
        {
            log.Debug("CarService: InitializeQueueConnection");
            var hostName = ConfigurationManager.AppSettings["queue:HostName"];
            var userName = ConfigurationManager.AppSettings["queue:UserName"];
            var password = ConfigurationManager.AppSettings["queue:Password"];
            var queueTimeout = int.Parse(ConfigurationManager.AppSettings["queue:Timeout"]);
            var heartbeatSeconds = int.Parse(ConfigurationManager.AppSettings["queue:HeartbeatSeconds"]);
            var inboundQueueName = ConfigurationManager.AppSettings["adapter:InboundQueueName"];
            var outboundExchangeName = ConfigurationManager.AppSettings["adapter:OutboundExchangeName"];
            var invalidQueueName = ConfigurationManager.AppSettings["adapter:InvalidQueueName"];
            var automaticRecoveryEnabled = bool.Parse(ConfigurationManager.AppSettings["queue:AutomaticRecoveryEnabled"]);
            consumer = new RabbitMqConsumer(inboundQueueName, hostName, userName, password, queueTimeout, heartbeatSeconds, automaticRecoveryEnabled);
            exchange = new RabbitMqExchange(outboundExchangeName, hostName, userName, password, queueTimeout, heartbeatSeconds, automaticRecoveryEnabled);
            invalidExchange = new RabbitMqExchange(invalidQueueName, hostName, userName, password, queueTimeout, heartbeatSeconds, automaticRecoveryEnabled);
            consumer.ConnectionLost += Connection_ConnectionShutdown;
            consumer.ConsumerFailed += Consumer_ConsumerFailed;
            ProcessingService.Consumer = consumer;
            ProcessingService.Exchange = exchange;
            ProcessingService.InvalidExchange = invalidExchange;
        }

        void Consumer_ConsumerFailed(object sender, EventArgs e)
        {
            consumer.ConsumerFailed -= Consumer_ConsumerFailed;
            consumer.Dispose();
            exchange.Dispose();
            invalidExchange.Dispose();
            InitializeQueueConnection();
        }

        protected override void OnStart(string[] args)
        {
            log.Information("Car Service Started");
            var imageFileFolder = ConfigurationManager.AppSettings["adapter:ImageFileFolder"];
            var imageFilenameTemplate = ConfigurationManager.AppSettings["adapter:ImageFilenameTemplate"];

            ocrService = new A2iACombinedTableService(ocrConfiguration);

            ProcessingService.OcrService = ocrService;
            ProcessingService.ImageFilePath = imageFileFolder;
            ProcessingService.ImageFileNameTemplate = imageFilenameTemplate;
            ProcessingService.Run();
        }

        protected override void OnStop()
        {
            ProcessingService.Stop();
            ocrService.Dispose();
            Log.Information("Car Service Stopped");
        }
    }
}
