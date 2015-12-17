using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Text;
using EasyNetQ;
using Lombard.Common.EasyNetQ;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using RabbitMQ.Client;
using RabbitMQ.Client.Framing;
// ReSharper disable InconsistentNaming

namespace Lombard.Common.UnitTests.EasyNetQ
{
    [SuppressMessage("Microsoft.StyleCop.CSharp.MaintainabilityRules", "SA1402:FileMayOnlyContainASingleClass", Justification = "Test Class")]
    public class MyMessage
    {
        public string Text { get; set; }
    }

    [SuppressMessage("Microsoft.StyleCop.CSharp.MaintainabilityRules", "SA1402:FileMayOnlyContainASingleClass", Justification = "Test Class")]
    [TestClass]
    public class CustomJsonSerializerTests
    {
        private ISerializer serializer;

        [TestInitialize]
        public void TestInitialize()
        {
            serializer = new CustomJsonSerializer(new TypeNameSerializer());
        }

        [TestMethod]
        public void Should_be_able_to_serialize_and_deserialize_a_message_using_generics()
        {
            var message = new MyMessage { Text = "Hello World" };

            var binaryMessage = serializer.MessageToBytes(message);
            var deseralizedMessage = serializer.BytesToMessage<MyMessage>(binaryMessage);

            Assert.AreEqual(message.Text, deseralizedMessage.Text);
        }

        [TestMethod]
        public void Should_be_able_to_serialize_and_deserialize_a_message_using_types()
        {
            var message = new MyMessage { Text = "Hello World" };

            var binaryMessage = serializer.MessageToBytes(message);
            var type = typeof(MyMessage);

            var deseralizedMessage = serializer.BytesToMessage(string.Format("{0}:{1}", type.FullName, type.Assembly.GetName().Name), binaryMessage);

            Assert.IsInstanceOfType(deseralizedMessage, typeof(MyMessage));
            Assert.AreEqual(message.Text, ((MyMessage)deseralizedMessage).Text);
        }

        [TestMethod]
        public void Should_be_able_to_serialize_basic_properties()
        {
            var originalProperties = new BasicProperties
            {
                AppId = "some app id",
                ClusterId = "cluster id",
                ContentEncoding = "content encoding",
                CorrelationId = "correlation id",
                DeliveryMode = 4,
                Expiration = "expiration",
                MessageId = "message id",
                Priority = 1,
                ReplyTo = "abc",
                Timestamp = new AmqpTimestamp(123344044),
                Type = "Type",
                UserId = "user id",
                Headers = new Dictionary<string, object>
                {
                    {"one", "header one"},
                    {"two", "header two"}
                }
            };

            var messageBasicProperties = new MessageProperties(originalProperties);
            var binaryMessage = serializer.MessageToBytes(messageBasicProperties);
            var deserializedMessageBasicProperties = serializer.BytesToMessage<MessageProperties>(binaryMessage);

            var newProperties = new BasicProperties();
            deserializedMessageBasicProperties.CopyTo(newProperties);

            Func<BasicProperties, string> getPropertiesString = p =>
            {
                var builder = new StringBuilder();
                p.AppendPropertyDebugStringTo(builder);
                return builder.ToString();
            };

            Assert.AreEqual(getPropertiesString(originalProperties), getPropertiesString(newProperties));
        }
    }
}

