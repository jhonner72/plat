using System;
using System.Collections.Generic;
using FujiXerox.Adapters.DipsAdapter.Domain;

namespace FujiXerox.Adapters.DipsAdapter
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
