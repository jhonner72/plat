﻿//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//     Runtime Version:4.0.30319.18408
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

// 
// This source code was auto-generated by xsd, Version=4.0.30319.33440.
// 
namespace Lombard.Common.Messages {
    using System.Xml.Serialization;
    
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("xsd", "4.0.30319.33440")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="http://lombard.aus.fujixerox.com/common/Error")]
    [System.Xml.Serialization.XmlRootAttribute(Namespace="http://lombard.aus.fujixerox.com/common/Error", IsNullable=false)]
    public partial class HeaderDetails {
        
        private string keyField;
        
        private string valueField;
        
        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Form=System.Xml.Schema.XmlSchemaForm.Unqualified)]
        public string key {
            get {
                return this.keyField;
            }
            set {
                this.keyField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Form=System.Xml.Schema.XmlSchemaForm.Unqualified)]
        public string value {
            get {
                return this.valueField;
            }
            set {
                this.valueField = value;
            }
        }
    }
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("xsd", "4.0.30319.33440")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="http://lombard.aus.fujixerox.com/common/Error")]
    [System.Xml.Serialization.XmlRootAttribute(Namespace="http://lombard.aus.fujixerox.com/common/Error", IsNullable=false)]
    public partial class Error {
        
        private System.DateTime errorDateTimeField;
        
        private string summaryField;
        
        private string detailField;
        
        private ErrorTypeEnum errorTypeField;
        
        private OriginalDetails originalField;
        
        private string componentField;
        
        private string serviceNameField;
        
        private string serverField;
        
        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Form=System.Xml.Schema.XmlSchemaForm.Unqualified)]
        public System.DateTime errorDateTime {
            get {
                return this.errorDateTimeField;
            }
            set {
                this.errorDateTimeField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Form=System.Xml.Schema.XmlSchemaForm.Unqualified)]
        public string summary {
            get {
                return this.summaryField;
            }
            set {
                this.summaryField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Form=System.Xml.Schema.XmlSchemaForm.Unqualified)]
        public string detail {
            get {
                return this.detailField;
            }
            set {
                this.detailField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Form=System.Xml.Schema.XmlSchemaForm.Unqualified)]
        public ErrorTypeEnum errorType {
            get {
                return this.errorTypeField;
            }
            set {
                this.errorTypeField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Form=System.Xml.Schema.XmlSchemaForm.Unqualified)]
        public OriginalDetails original {
            get {
                return this.originalField;
            }
            set {
                this.originalField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Form=System.Xml.Schema.XmlSchemaForm.Unqualified)]
        public string component {
            get {
                return this.componentField;
            }
            set {
                this.componentField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Form=System.Xml.Schema.XmlSchemaForm.Unqualified)]
        public string serviceName {
            get {
                return this.serviceNameField;
            }
            set {
                this.serviceNameField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Form=System.Xml.Schema.XmlSchemaForm.Unqualified)]
        public string server {
            get {
                return this.serverField;
            }
            set {
                this.serverField = value;
            }
        }
    }
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("xsd", "4.0.30319.33440")]
    [System.SerializableAttribute()]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="http://lombard.aus.fujixerox.com/common/Error")]
    public enum ErrorTypeEnum {
        
        /// <remarks/>
        ApplicationError,
        
        /// <remarks/>
        BadDataError,
        
        /// <remarks/>
        ConnectionError,
        
        /// <remarks/>
        OtherError,
    }
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("xsd", "4.0.30319.33440")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="http://lombard.aus.fujixerox.com/common/Error")]
    [System.Xml.Serialization.XmlRootAttribute(Namespace="http://lombard.aus.fujixerox.com/common/Error", IsNullable=false)]
    public partial class OriginalDetails {
        
        private string jobIdField;
        
        private string exchangeNameField;
        
        private string queueNameField;
        
        private string payloadField;
        
        private string routingKeyField;
        
        private HeaderDetails[] headerPropertiesField;
        
        private HeaderDetails[] headersField;
        
        private string deliveryModeField;
        
        private string correlationIdField;
        
        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Form=System.Xml.Schema.XmlSchemaForm.Unqualified)]
        public string jobId {
            get {
                return this.jobIdField;
            }
            set {
                this.jobIdField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Form=System.Xml.Schema.XmlSchemaForm.Unqualified)]
        public string exchangeName {
            get {
                return this.exchangeNameField;
            }
            set {
                this.exchangeNameField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Form=System.Xml.Schema.XmlSchemaForm.Unqualified)]
        public string queueName {
            get {
                return this.queueNameField;
            }
            set {
                this.queueNameField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Form=System.Xml.Schema.XmlSchemaForm.Unqualified)]
        public string payload {
            get {
                return this.payloadField;
            }
            set {
                this.payloadField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Form=System.Xml.Schema.XmlSchemaForm.Unqualified)]
        public string routingKey {
            get {
                return this.routingKeyField;
            }
            set {
                this.routingKeyField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("headerProperties", Form=System.Xml.Schema.XmlSchemaForm.Unqualified)]
        public HeaderDetails[] headerProperties {
            get {
                return this.headerPropertiesField;
            }
            set {
                this.headerPropertiesField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("headers", Form=System.Xml.Schema.XmlSchemaForm.Unqualified)]
        public HeaderDetails[] headers {
            get {
                return this.headersField;
            }
            set {
                this.headersField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Form=System.Xml.Schema.XmlSchemaForm.Unqualified)]
        public string deliveryMode {
            get {
                return this.deliveryModeField;
            }
            set {
                this.deliveryModeField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Form=System.Xml.Schema.XmlSchemaForm.Unqualified)]
        public string correlationId {
            get {
                return this.correlationIdField;
            }
            set {
                this.correlationIdField = value;
            }
        }
    }
}
