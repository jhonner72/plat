using EasyNetQ;
using Lombard.Common.Messages;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lombard.Common.Helper
{
    public interface ICustomErrorHandling
    {
        Error MapMessageToErrorSchema(AggregateException exception, string correlationId, string messageType, string payload, MessageProperties properties, IDictionary<string, object> headers, MessageReceivedInfo info);
        RoutingKey GetRoutingKey(AggregateException exception);
    }
}
