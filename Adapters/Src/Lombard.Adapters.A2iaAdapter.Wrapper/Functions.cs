using System;
using System.Drawing;
using System.IO;
using A2iACheckReaderLib;
using Lombard.Adapters.A2iaAdapter.Wrapper.Domain;
using Serilog;

namespace Lombard.Adapters.A2iaAdapter.Wrapper
{
    public static class Functions
    {
        public static string GetImageFormat(string filename)
        {
            var extension = Path.GetExtension(filename);
            if (extension == null) throw new FileNotFoundException();
            switch (extension.ToUpper())
            {
                case ".JPG":
                case ".JPEG":
                    return "JPEG";
                case ".TIF":
                case ".TIFF":
                    return "TIFF";
                default:
                    throw new NotSupportedException("Unsupported cheque image format");
            }
        }

        /// <summary>
        /// Populate the OCR results into the metadata associated with the image
        /// </summary>
        /// <param name="a2IaEngine"></param>
        /// <param name="resultId">OCR result Id</param>
        /// <param name="voucher">Metadata of the image to be populated</param>
        public static void GetResult(API a2IaEngine, int resultId, OcrVoucher voucher)
        {
            // A2iA amount recognition
            if (a2IaEngine.GetStringProperty(resultId, Constants.EngineFields.Amount) == Constants.Enabled)
            {
                switch (voucher.VoucherType)
                {
                    case VoucherType.Credit:
                        voucher.AmountResult.Result = a2IaEngine.GetStringProperty(resultId, string.Concat(Constants.ResultFields.CreditBase, "Amount"));
                        voucher.AmountResult.Score = a2IaEngine.GetStringProperty(resultId, string.Concat(Constants.ResultFields.CreditBase, "Score"));
                        break;
                    default:
                        voucher.AmountResult.Result = a2IaEngine.GetStringProperty(resultId, string.Concat(Constants.ResultFields.CheckBase, "Amount"));
                        voucher.AmountResult.Score = a2IaEngine.GetStringProperty(resultId, string.Concat(Constants.ResultFields.CheckBase, "Score"));
                        break;
                }

                var x1 = (int)((float)a2IaEngine.ObjectProperty[resultId, Constants.ResultFields.AmountLocationX1]);
                var y1 = (int)((float)a2IaEngine.ObjectProperty[resultId, Constants.ResultFields.AmountLocationY1]);
                var x2 = (int)((float)a2IaEngine.ObjectProperty[resultId, Constants.ResultFields.AmountLocationX2]);
                var y2 = (int)((float)a2IaEngine.ObjectProperty[resultId, Constants.ResultFields.AmountLocationY2]);

                voucher.AmountResult.Location = new Rectangle(x1, y1, x2 - x1, y2 - y1);
            }

            // A2iA codeline recognition
            if (a2IaEngine.GetStringProperty(resultId, Constants.EngineFields.CodeLine) == Constants.Enabled)
            {
                voucher.CodelineResult.Result = a2IaEngine.GetStringProperty(resultId, Constants.ResultFields.CodelineRecognition);
                voucher.CodelineResult.Score = a2IaEngine.GetStringProperty(resultId, Constants.ResultFields.CodelineConfidence);

                var x1 = (int)((float)a2IaEngine.ObjectProperty[resultId, Constants.ResultFields.CodelineLocationX1]);
                var y1 = (int)((float)a2IaEngine.ObjectProperty[resultId, Constants.ResultFields.CodelineLocationY1]);
                var x2 = (int)((float)a2IaEngine.ObjectProperty[resultId, Constants.ResultFields.CodelineLocationX2]);
                var y2 = (int)((float)a2IaEngine.ObjectProperty[resultId, Constants.ResultFields.CodelineLocationY2]);

                voucher.CodelineResult.Location = new Rectangle(x1, y1, x2 - x1, y2 - y1);
            }

            // A2iA date recognition
            if (a2IaEngine.GetStringProperty(resultId, Constants.EngineFields.Date) == Constants.Enabled)
            {
                var day = (uint)a2IaEngine.ObjectProperty[resultId, Constants.ResultFields.DateRecognitionDay];
                var month = (uint)a2IaEngine.ObjectProperty[resultId, Constants.ResultFields.DateRecognitionMonth];
                var year = (uint)a2IaEngine.ObjectProperty[resultId, Constants.ResultFields.DateRecognitionYear];

                voucher.DateResult.Result = string.Format("{0}/{1}/{2}", day, month, year);
                voucher.DateResult.Score = a2IaEngine.GetStringProperty(resultId, Constants.ResultFields.DateConfidence);

                var x1 = (int)((float)a2IaEngine.ObjectProperty[resultId, Constants.ResultFields.DateLocationX1]);
                var y1 = (int)((float)a2IaEngine.ObjectProperty[resultId, Constants.ResultFields.DateLocationY1]);
                var x2 = (int)((float)a2IaEngine.ObjectProperty[resultId, Constants.ResultFields.DateLocationX2]);
                var y2 = (int)((float)a2IaEngine.ObjectProperty[resultId, Constants.ResultFields.DateLocationY2]);

                voucher.DateResult.Location = new Rectangle(x1, y1, x2 - x1, y2 - y1);
            }

            // Image Rotation
            voucher.ImageRotation = (int)(float)a2IaEngine.ObjectProperty[resultId, Constants.ResultFields.OrientationCorrection];

            Log.Verbose("v {0} - amt {1} score {2} - micr {3} score {4}", voucher.Id, voucher.AmountResult.Result, voucher.AmountResult.Score, voucher.CodelineResult.Result, voucher.CodelineResult.Score);
        }
    }
}
