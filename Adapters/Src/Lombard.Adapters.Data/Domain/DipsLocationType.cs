using System.Diagnostics.CodeAnalysis;
using Lombard.Common;

namespace Lombard.Adapters.Data.Domain
{
    [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
    public class DipsLocationType : Enumeration<DipsLocationType, string>
    {
        public static DipsLocationType AmountEntry = new DipsLocationType("AmountEntry");
        public static DipsLocationType AmountEntryDone = new DipsLocationType("AmountEntryDone");

        public static DipsLocationType CodelineValidation = new DipsLocationType("CodelineValidation");
        public static DipsLocationType CodelineValidationDone = new DipsLocationType("CodelineValidationDone");

        public static DipsLocationType CodelineCorrection = new DipsLocationType("CodelineCorrect");
        public static DipsLocationType CodelineCorrectionDone = new DipsLocationType("CodelineCorrectDone");

        public static DipsLocationType TransactionValidation = new DipsLocationType("AutoBalancing");
        public static DipsLocationType TransactionValidationDone = new DipsLocationType("AutoBalancingDone");

        public static DipsLocationType TransactionCorrection = new DipsLocationType("ExpertBalance");
        public static DipsLocationType TransactionCorrectionDone = new DipsLocationType("ExpertBalanceDone");

        public static DipsLocationType CheckThirdParty = new DipsLocationType("ThirdPartyCheck");
        public static DipsLocationType CheckThirdPartyDone = new DipsLocationType("ThirdPartyCheckDone");

        public static DipsLocationType GenerateCorrespondingVoucher = new DipsLocationType("GenerateCorrespondingVoucher");
        public static DipsLocationType GenerateCorrespondingVoucherDone = new DipsLocationType("GenerateCorrespondingVoucherDone");

        public static DipsLocationType GenerateBulkCreditVoucher = new DipsLocationType("BulkCredit");
        public static DipsLocationType GenerateBulkCreditVoucherDone = new DipsLocationType("BulkCreditDone");

        // In this case, value & displayName are the same...
        public DipsLocationType(string value) : base(value, value)
        {

        }
    }
}
