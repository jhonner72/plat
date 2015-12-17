namespace Lombard.Reporting.AdapterService.Utils
{
    using System.Diagnostics.CodeAnalysis;
    using Lombard.Common;

    public class RenderFormat : Enumeration<RenderFormat, string>
    {
        // See: https://msdn.microsoft.com/en-us/library/reportexecution2005.reportexecutionservice.render.aspx

        // Format : The format in which to render the report. This argument maps to a rendering extension. 
        // Supported extensions include XML, NULL, CSV, IMAGE, PDF, HTML4.0, HTML3.2, MHTML, EXCEL, and Word. 
        // A list of supported extensions may be obtained by calling the ListRenderingExtensions method.
        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static RenderFormat Xml = new RenderFormat("XML", "XML file with report data", "XML");

        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static RenderFormat Null = new RenderFormat("NULL", "NullRenderer", "NULL"); // not sure the actual format

        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static RenderFormat Csv = new RenderFormat("CSV", "CSV (comma delimited)", "CSV");

        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static RenderFormat Atom = new RenderFormat("ATOM", "Data Feed", "ATOM");

        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static RenderFormat Pdf = new RenderFormat("PDF", "PDF", "PDF");

        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static RenderFormat Rgdi = new RenderFormat("RGDI", "Remote GDI+ file", "RGDI");

        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static RenderFormat Html4 = new RenderFormat("HTML4", "HTML 4.0", "HTML4.0");

        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static RenderFormat Mhtml = new RenderFormat("MHTML", "MHTML (web archive)", "MHTML");

        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static RenderFormat Excel = new RenderFormat("EXCEL", "Excel 2003", "EXCEL");

        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static RenderFormat ExcelOpenXml = new RenderFormat("XLSX", "Excel", "EXCELOPENXML");

        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static RenderFormat Rpl = new RenderFormat("RPL", "RPL Renderer", "RPL");

        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static RenderFormat Image = new RenderFormat("IMAGE", "TIFF file", "IMAGE");

        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static RenderFormat Word = new RenderFormat("DOC", "Word 2003", "WORD");

        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static RenderFormat WordOpenXml = new RenderFormat("DOCX", "Word", "WORDOPENXML");

        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static RenderFormat Text = new RenderFormat("TXT", "Text", "TXT");

        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static RenderFormat Data = new RenderFormat("DAT", "Data", "TXT");

        public RenderFormat(string value, string displayName, string ssrsValue) : base(value, displayName)
        {
            this.SSRSValue = ssrsValue;
        }

        public string SSRSValue { get; set; }
    }
}