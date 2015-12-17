using System;
using System.CodeDom.Compiler;
using System.ComponentModel;
using System.Diagnostics;
using System.Xml.Schema;
using System.Xml.Serialization;

namespace Lombard.Adapters.A2iaAdapter.Messages
{
    /// <remarks/>
    [GeneratedCode("xsd", "4.0.30319.18020")]
    [Serializable()]
    [DebuggerStepThrough()]
    [DesignerCategory("code")]
    [XmlType(Namespace = "http://lombard.aus.fujixerox.com/outclearings/RecogniseCourtesyAmount")]
    [XmlRoot(Namespace = "http://lombard.aus.fujixerox.com/outclearings/RecogniseCourtesyAmount", IsNullable = false)]
    public class RecogniseBatchCourtesyAmountRequest
    {

        private RecogniseCourtesyAmountRequest[] voucherField;

        /// <remarks/>
        [XmlElement("voucher", Form = XmlSchemaForm.Unqualified)]
        public RecogniseCourtesyAmountRequest[] Voucher
        {
            get
            {
                return voucherField;
            }
            set
            {
                voucherField = value;
            }
        }
    }
}
