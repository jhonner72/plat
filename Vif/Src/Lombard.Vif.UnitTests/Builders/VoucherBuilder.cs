using Lombard.Vif.Service.Messages.XsdImports;

namespace Lombard.Vif.UnitTests.Builders
{
    public class VoucherBuilder
    {
        private DocumentTypeEnum documentType;
        private string extraAuxDom;
        private string auxDom;

        public VoucherBuilder()
        {
            this.documentType = DocumentTypeEnum.Dr;
        }

        public VoucherBuilder WithCreditDocumentType()
        {
            this.documentType = DocumentTypeEnum.Cr;
            return this;
        }

        public VoucherBuilder WithEAD(string ead)
        {
            this.extraAuxDom = ead;
            return this;
        }

        public VoucherBuilder WithAD(string ad)
        {
            this.auxDom = ad;
            return this;
        }

        public Voucher Build()
        {
            return new Voucher()
            {
                documentType = this.documentType,
                extraAuxDom = this.extraAuxDom,
                auxDom = this.auxDom
            };
        }
    }
}
