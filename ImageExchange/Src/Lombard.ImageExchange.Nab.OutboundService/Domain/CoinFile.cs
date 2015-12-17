using System.Collections.Generic;

namespace Lombard.ImageExchange.Nab.OutboundService.Domain
{
    public class CoinFile
    {
        public CoinHeader Header { get; private set; }
        public CoinTrailer Trailer { get; private set; }
        public IEnumerable<CoinItem> Items { get; private set; }

        public CoinFile(CoinHeader header, CoinTrailer trailer, IEnumerable<CoinItem> items)
        {
            Header = header;
            Trailer = trailer;
            Items = items;
        }
    }
}