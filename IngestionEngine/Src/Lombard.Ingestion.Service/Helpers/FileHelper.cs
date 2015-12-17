using System.IO;

namespace Lombard.Ingestion.Service.Helpers
{
    public class FileHelper
    {
        public virtual string[] GetEclFiles(string eclBitLockerLocation)
        {
            return Directory.GetFiles(eclBitLockerLocation, "MO.AFT.MO536.ECL.SRTED.???");
        }

        public virtual void DeleteFile(string file)
        {
            if (File.Exists(file))
            {
                File.Delete(file);
            }
        }

        public virtual string GetFileName(string filePath)
        {
            return Path.GetFileName(filePath);
        }

        public virtual string[] ReadAllLines(string filePath)
        {
            return File.ReadAllLines(filePath);
        }

        public virtual string[] GetBatchAuditFiles(string batchAuditBitLockerLocation)
        {
            return Directory.GetFiles(batchAuditBitLockerLocation, "*.rec");
        }
    }
}
