using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Emc.Documentum.FS.DataModel.Core;
using Emc.Documentum.FS.DataModel.Core.Content;
using Lombard.Common.Configuration;
using Lombard.Common.Domain;
using Lombard.Documentum.Data.Constants;
using Lombard.Documentum.Data.Extensions;
using Serilog;

namespace Lombard.Documentum.Data.Repository
{
    public class DishonourLetterRepository : IDishonourLetterRepository
    {
        private const int MaxDishonourLetterFileSize = 1048576;

        private readonly IDataObjectRepository objectRepository;
        private readonly IDfsConfiguration dfsConfiguration;
        private readonly IVoucherRepository voucherRepository;

        public DishonourLetterRepository(
            IDataObjectRepository objectRepository,
            IDfsConfiguration dfsConfiguration,
            IVoucherRepository voucherRepository)
        {
            this.objectRepository = objectRepository;
            this.dfsConfiguration = dfsConfiguration;
            this.voucherRepository = voucherRepository;
        }


        public async Task<DishonourLetter> CreateLetterForVoucher(Voucher voucher, DateTime processingDate)
        {
            Log.Debug("Creating dishonours letter in Documentum");

            Guard.IsNotNull(voucher, "voucher");

            var fileName = DishonourLetter.GetFileName(                
                voucher.AuxiliaryDomestic,
                voucher.Bsb,
                voucher.AccountNumber,
                voucher.Amount,
                voucher.ProcessingDate);

            var letter = new DishonourLetter(
                fileName,
                DishonourLetterStatus.LetterRaised,
                processingDate);

            var letterPath = await Create(letter);

            LinkLetterToVoucher(letterPath, voucher, processingDate);

            return letter;
        }

        public async Task<DishonourLetter> Get(string fileName, DateTime processingDate)
        {
            Log.Debug("Retrieving dishonour letter from Documentum '{@fileName}' for date {@processingDate:ddMMyyyy}", fileName, processingDate);

            var dataObject = await GetLetterDataObject(fileName, processingDate);
            return GetLetterFromDataObject(dataObject, processingDate);
        }


        public async Task Update(DishonourLetter letter, bool updateFile = false)
        {
            Log.Debug("Updating dishonour letter '{@letter}', {@updateFile} in Documentum", letter, updateFile);

            var dataObject = await GetLetterDataObject(letter.Name, letter.ProcessingDate);

            dataObject.Properties.Set(DishonourLetterAttributes.DishonourStatus, letter.Status.Value);

            if (updateFile)
            {
                using (var stream = letter.FileInfo.OpenRead())
                {
                    if (stream.Length > MaxDishonourLetterFileSize)
                    {
                        throw new InvalidOperationException(
                            string.Format("File is bigger than maximum dishonours letter file size ({0}KB)",
                                MaxDishonourLetterFileSize/1024));
                    }

                    var buffer = new byte[stream.Length];
                    await stream.ReadAsync(buffer, 0, (int) stream.Length);

                    dataObject.Contents.Add(new BinaryContent(buffer, DocumentumFormats.Pdf));
                }
            }

            await Task.Run(() => objectRepository.Update(dataObject));
        }

        private async Task<string> Create(DishonourLetter letter)
        {
            Log.Debug("Creating dishonour letter {@letter} in Documentum", letter);

            var letterPath = GetLetterPath(letter.ProcessingDate);

            var path = string.Format("{0}/{1}", letterPath, letter.Name);

            var properties = new Dictionary<string, string>
            {
                {DocumentumAttributes.ObjectName, letter.Name},
                {DocumentumAttributes.Title, letter.Name},
                {DishonourLetterAttributes.DishonourStatus, DishonourLetterStatus.LetterRaised.Value}
            };

            await Task.Run(() => objectRepository.CreateDataObject(
                letterPath,
                letter.Name,
                DocumentumTypes.FxaDishonourLetter,
                DocumentumFormats.Pdf,
                properties, 
                false));

            Log.Debug("Dishonour letter created : '{0}'", path);

            return path;
        }

        private async Task<DataObject> GetLetterDataObject(string fileName, DateTime processingDate)
        {
            var path = string.Format("{0}/{1}", GetLetterPath(processingDate), fileName);

            return await Task.Run(() => objectRepository.GetDataObject(path));
        }

        private DishonourLetter GetLetterFromDataObject(DataObject dataObject, DateTime processingDate)
        {
            var name = dataObject.GetProperty(DocumentumAttributes.ObjectName).GetValueAsString();
            var status = dataObject.GetProperty(DishonourLetterAttributes.DishonourStatus).GetValueAsString();

            return new DishonourLetter(name, DishonourLetterStatus.FromValue(status), processingDate);
        }

        private void LinkLetterToVoucher(string letterPath, Voucher voucher, DateTime processingDate)
        {
            var cutOffDate = processingDate.AddDays(-dfsConfiguration.DishonourTimeWindowDays);

            var latestVoucher = voucherRepository.GetVoucher(voucher.AuxiliaryDomestic, voucher.Bsb, voucher.AccountNumber, voucher.Amount, false, cutOffDate);

            var voucherObj = objectRepository.GetDataObjectByObjectId(latestVoucher.Id);

            if (voucherObj == null)
            {
                throw new ArgumentException("Voucher not found", "voucher");
            }

            var letterObj = objectRepository.GetDataObject(letterPath);
            if (letterObj == null)
            {
                throw new ArgumentException("Dishonour Letter not found ", "letterPath");
            }

            objectRepository.CreateParentChildRelationship(voucherObj, letterObj, DocumentumTypes.DishonourVoucherRelation);
        }

        private string GetLetterPath(DateTime processingDate)
        {
            var letterPath = string.Format("{0}/{1:yyyy}/{1:MM}/{1:dd}", dfsConfiguration.DishonourLetterPath, processingDate);

            return letterPath;
        }
    }
}
