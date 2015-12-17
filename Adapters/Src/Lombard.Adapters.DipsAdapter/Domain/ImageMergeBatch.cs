using System;
using System.Collections.Generic;

namespace Lombard.Adapters.DipsAdapter.Domain
{
    [Serializable]
    public class ImageMergeBatch
    {
        public string BatchNumber { get; set; }
        public List<ImageMergeVoucher> Vouchers { get; set; }

        public ImageMergeBatch()
        {
            Vouchers = new List<ImageMergeVoucher>();
        }
    }
}
