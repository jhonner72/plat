using System.IO;
using Aspose.Pdf;
using Aspose.Pdf.Text;

namespace Lombard.AdjustmentLetters.Domain
{
    public interface IAsposeWrapper
    {
        Aspose.Pdf.Facades.Form GetPdfFormTemplate(string filename);

        void FillAndFlattenField(Aspose.Pdf.Facades.Form template, string fieldname, string value);

        void FillField(Aspose.Pdf.Facades.Form template, string fieldname, string value);

        void SaveDocument(Document document, string filename);

        void AddPageNumbers(string filename);
    }

    public class AsposeWrapper : IAsposeWrapper
    {
        public Aspose.Pdf.Facades.Form GetPdfFormTemplate(string filename)
        {
            var template = new Aspose.Pdf.Facades.Form();

            template.BindPdf(filename);

            return template;
        }

        public void FillAndFlattenField(Aspose.Pdf.Facades.Form template, string fieldname, string value)
        {
            template.FillField(fieldname, value);
            template.FlattenField(fieldname);
        }

        public void FillField(Aspose.Pdf.Facades.Form template, string fieldname, string value)
        {
            template.FillField(fieldname, value);
        }

        public void SaveDocument(Document document, string filename)
        {
            document.OptimizeResources(new Document.OptimizationOptions
            {
                RemoveUnusedObjects = true,
                RemoveUnusedStreams = true,
                LinkDuplcateStreams = true,
                CompressImages = true,
                ImageQuality = 80,
                AllowReusePageContent = true
            });

            document.Save(filename);
        }

        public void AddPageNumbers(string filename)
        {
            var pdf = new Document(filename);
            var pageNumberStamp = new PageNumberStamp
            {
                Background = false,
                Format = "Page # of " + pdf.Pages.Count,
                BottomMargin = 10,
                HorizontalAlignment = HorizontalAlignment.Center,
                StartingNumber = 1
            };

            pageNumberStamp.TextState.FontSize = 8.0F;
            pageNumberStamp.TextState.FontStyle = FontStyles.Regular;

            for (var i = 2; i < pdf.Pages.Count + 1; i++)
            {
                pdf.Pages[i].AddStamp(pageNumberStamp);
            }

            this.SaveDocument(pdf, filename);
        }
    }
}
