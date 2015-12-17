using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lombard.ImageExchange.Nab.OutboundService.Domain
{
    public class CuscalFile
    {
        public IEnumerable<string> VoucherItems { get; private set; }
        public string XmlFilename { get; set; }
        public string ZipFilename { get; set; }

        public CuscalFile(IEnumerable<string> items)
        {
            VoucherItems = items;
        }
    }
}
