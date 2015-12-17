using System;
using Lombard.Common;

namespace Lombard.ImageExchange.Nab.OutboundService.Mappers
{
    public class BytesToBase64Parser : IMapper<byte[], string>
    {
        public string Map(byte[] input)
        {
            Guard.IsNotNull(input, "input");

            return Convert.ToBase64String(input);
        }
    }
}