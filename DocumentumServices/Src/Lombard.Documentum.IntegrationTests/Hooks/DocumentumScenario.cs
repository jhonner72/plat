using System;
using System.Collections.Generic;
using System.Diagnostics;
using EasyNetQ;
using Lombard.Documentum.Service.Messages;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using TechTalk.SpecFlow;

namespace Lombard.Documentum.IntegrationTests.Hooks
{
    [Binding]
    public class DocumentumScenario
    {
        private static readonly IList<DocumentumResponseMessage> ReceivedMessages = new List<DocumentumResponseMessage>();

        ProcessStartInfo ProcessStartInfo { get; set; }
        ISubscriptionResult TestConsumer { get; set; }

        public DocumentumScenario()
        {
            // TODO: move to config or use CurrentlyExecutingAssembly to find path...
            ProcessStartInfo = new ProcessStartInfo
            {
                WorkingDirectory = @"C:\code\LombardRoot\DocumentumServices\Src\Lombard.Documentum.Service\bin\Debug",
                WindowStyle = ProcessWindowStyle.Normal,
                FileName = @"C:\code\LombardRoot\DocumentumServices\Src\Lombard.Documentum.Service\bin\Debug\Lombard.Documentum.Service.exe"
            };
        }

        public static IList<DocumentumResponseMessage> ResponseMessages
        {
            get
            {
                return ReceivedMessages;
            }
        }

        [BeforeScenario("documentum")]
        public void Before()
        {
            // Be aware that if multiple Subscribes() are invoked with 
            // the same subscriptionId, then only one of the consumers/handlers will
            // be invoked.  If multiple consumers are desired for the same
            // type, then use different subscriptionId's

            TestConsumer = MessageBus.Instance.Subscribe<DocumentumResponseMessage>("documentumIntegrationTest",
                msg => ReceivedMessages.Add(msg));

            RunTopshelfCommand("install --autostart");

            RunTopshelfCommand("start");
        }

        [AfterScenario("documentum")]
        public void After()
        {
            RunTopshelfCommand("stop");

            RunTopshelfCommand("uninstall");

            if (TestConsumer != null)
            {
                TestConsumer.Dispose();
            }
        }

        private void RunTopshelfCommand(string installAutostart)
        {
            ProcessStartInfo.Arguments = installAutostart;

            try
            {
                using (var process = Process.Start(ProcessStartInfo))
                {
                    process.WaitForExit();
                }
            }
            catch (Exception ex)
            {
                Assert.Fail("Could not start documentum service: '{0}'", ex);
            }
        }
    }
}
