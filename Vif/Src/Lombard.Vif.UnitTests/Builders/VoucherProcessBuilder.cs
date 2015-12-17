using Lombard.Vif.Service.Messages.XsdImports;

namespace Lombard.Vif.UnitTests.Builders
{
    public class VoucherProcessBuilder
    {
        private bool highValueFlag;
        private string alternateExAuxDom;
        private string alternateAuxDom;

        public VoucherProcessBuilder WithHighValueFlag()
        {
            this.highValueFlag = true;
            return this;
        }

        internal VoucherProcessBuilder WithAlternateExAuxDom(string alternateExAuxDom)
        {
            this.alternateExAuxDom = alternateExAuxDom;
            return this;
        }

        internal VoucherProcessBuilder WithAlternateAuxDom(string alternateAuxDom)
        {
            this.alternateAuxDom = alternateAuxDom;
            return this;
        }

        public VoucherProcess Build()
        {
            return new VoucherProcess()
            {
                highValueFlag = this.highValueFlag,
                alternateExAuxDom = this.alternateExAuxDom,
                alternateAuxDom = this.alternateAuxDom
            };
        }
    }
}
