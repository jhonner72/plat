using System.Diagnostics.CodeAnalysis;
using Lombard.Common;

namespace Lombard.ImageExchange.Nab.OutboundService.Domain
{
    // Voucher Indicator Codes as set out in the APCS Procedures, Schedule 11, Rule 1.14
    public class VoucherIndicator : Enumeration<VoucherIndicator, string>
    {
        // Default
        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static VoucherIndicator ImageIsPresent = new VoucherIndicator(" ");

        // D - Indicator that image is delayed (used when image cannot be exchanged today)
        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static VoucherIndicator ImageIsDelayed = new VoucherIndicator("D");
        
        // N – Indicator that image has been sent on a delayed basis
        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static VoucherIndicator ImageBeingSentForPreviouslyDelayed = new VoucherIndicator("N");

        // In this case, value & displayName are the same...
        public VoucherIndicator(string value) : base(value, value)
        {
        }

    }
}