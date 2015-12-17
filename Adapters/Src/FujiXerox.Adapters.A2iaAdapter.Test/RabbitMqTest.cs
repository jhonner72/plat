//using System;
//using System.Threading;
//using FujiXerox.Adapters.A2iaAdapter.MessageQueue;
//using Microsoft.VisualStudio.TestTools.UnitTesting;
//using RabbitMQ.Client;

//namespace FujiXerox.Adapters.A2iaAdapter.Test
//{
//    [TestClass]
//    [Ignore]
//    public class RabbitMqTest
//    {
//        public class RabbitMqTestClass : RabbitMq
//        {
//            public RabbitMqTestClass(string hostNames, string userName, string password, int timeout = 5000) : base(hostNames, userName, password, timeout)
//            {
//            }

//            //public int RetryCounter { get { return retryCounter;}  set { retryCounter = value; }}
//            public bool WaitingToConnect { get { return waitingToConnect; } }
//            public IConnection Connection { get { return connection; } }
//            public void TryCreateConnectionFactory(object value)
//            {
//                base.TryCreateConnectionFactory(value);
//            }
//        }

//        private RabbitMqTestClass Rabbit { get; set; }

//        [TestInitialize]
//        public void Initialize()
//        {
//            Rabbit = new RabbitMqTestClass("First,Second,Third", "Username", "Password");    
//        }

//        [TestMethod]
//        public void TryCreateTest()
//        {
//            Rabbit.TryCreateConnectionFactory(null);
            
//        }

//        [TestMethod]
//        public void RetryTest()
//        {
//            //Rabbit.RetryCounter = 0;
//            var count = 0;
//            while (Rabbit.Connection == null)
//            {
//                if (Rabbit.WaitingToConnect) continue;
//                Rabbit.TryCreateConnectionFactory(null);
//                if (count++ == 3) break;
//                //if (Rabbit.RetryCounter == 3) break;
//            }
//        }
//    }
//}
