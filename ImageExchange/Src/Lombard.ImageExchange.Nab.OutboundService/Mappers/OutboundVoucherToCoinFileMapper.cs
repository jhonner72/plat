using System.Collections.Generic;
using System.Linq;
using Lombard.Common;
using Lombard.ImageExchange.Nab.OutboundService.Domain;

namespace Lombard.ImageExchange.Nab.OutboundService.Mappers
{
    public class OutboundVoucherToCoinFileMapper : IMapper<OutboundVoucherFile, CoinFile>
    {
        private readonly IMapper<OutboundVoucherFile, CoinHeader> headerMapper;
        private readonly IMapper<OutboundVoucherFile, IEnumerable<CoinItem>> itemMapper;
        private readonly IMapper<OutboundVoucherFile, CoinTrailer> trailerMapper;

        public OutboundVoucherToCoinFileMapper(
            IMapper<OutboundVoucherFile, CoinHeader> headerMapper,
            IMapper<OutboundVoucherFile, IEnumerable<CoinItem>> itemMapper, 
            IMapper<OutboundVoucherFile, CoinTrailer> trailerMapper)
        {
            this.headerMapper = headerMapper;
            this.itemMapper = itemMapper;
            this.trailerMapper = trailerMapper;
        }

        public CoinFile Map(OutboundVoucherFile input)
        {
            Guard.IsNotNull(input, "input");
            
            var header = headerMapper.Map(input);
            var items = itemMapper.Map(input);
            var trailer = trailerMapper.Map(input);
            return new CoinFile(header, trailer, items);
        }
    }
}