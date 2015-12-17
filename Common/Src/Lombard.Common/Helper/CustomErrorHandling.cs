using EasyNetQ;
using Lombard.Common.Messages;
using System;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;
using System.IO;
using System.Linq;

namespace Lombard.Common.Helper
{
    public enum RoutingKey
    {
        System,
        Integration,
        Service,
        External
    }

    public class CustomErrorHandling : ICustomErrorHandling
    {
        public Error MapMessageToErrorSchema(AggregateException exception, string correlationId, string messageType, string payload, MessageProperties properties, IDictionary<string, object> headers, MessageReceivedInfo info)
        {
            return new Error
            {
                errorDateTime = DateTime.Now,
                errorType = ErrorTypeEnum.ApplicationError,
                component = exception.InnerException.Source,
                server = Environment.MachineName,
                serviceName = messageType,
                summary = exception.InnerException.Message,
                detail = exception.InnerException.StackTrace,
                original = new OriginalDetails
                {
                    jobId = correlationId,
                    exchangeName = (info == null) ? string.Empty : info.Exchange,
                    queueName = (info == null) ? string.Empty : info.Queue,
                    payload = payload,
                    correlationId = correlationId,
                    routingKey = (info == null) ? string.Empty : info.RoutingKey,
                    deliveryMode = properties.DeliveryMode.ToString(),
                    headers = ConvertDictionaryToHeaderDetails(headers),
                    headerProperties = ConvertMessagePropertiesToHeaderDetails(properties)
                }
            };
        }

        private static HeaderDetails[] ConvertDictionaryToHeaderDetails(IDictionary<string, object> headers)
        {
            return headers.Select(header => new HeaderDetails
            {
                key = header.Key,
                value = ParseHeaderValue(header.Value)
            }).ToArray();
        }

        private static HeaderDetails[] ConvertMessagePropertiesToHeaderDetails(MessageProperties properties)
        {
            return properties.GetType().GetProperties()
                .Where(_ => _.Name != "Headers" && properties.GetType().GetProperty(_.Name).GetValue(properties, null) != null)
                .Select(property => new HeaderDetails
            {
                key = property.Name,
                value = properties.GetType().GetProperty(property.Name).GetValue(properties, null).ToString()
            }).ToArray();
        }

        private static string ParseHeaderValue(object value)
        {
            var valueInBytes = value as byte[];
            if (valueInBytes != null) return System.Text.Encoding.UTF8.GetString(valueInBytes);

            if (value is int) return value.ToString();

            return value.ToString();
        }

        public RoutingKey GetRoutingKey(AggregateException exception)
        {
            if (exception.InnerException is SqlException ||
                exception.InnerException is IOException ||
                exception.InnerException is OutOfMemoryException ||
                exception.InnerException is DataException)
            {
                return RoutingKey.System;
            }

            return RoutingKey.Service;
        }
    }
}
