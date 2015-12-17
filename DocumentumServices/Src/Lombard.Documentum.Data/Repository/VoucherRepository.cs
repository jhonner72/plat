using System;
using System.Collections.Generic;
using System.IO;
using Emc.Documentum.FS.DataModel.Core;
using Emc.Documentum.FS.DataModel.Core.Query;
using Lombard.Documentum.Data.Exceptions;
using Lombard.Documentum.Data.Extensions;
using Serilog;
using Lombard.Documentum.Data.Constants;
using Lombard.Common.Domain;

namespace Lombard.Documentum.Data.Repository
{
    public class VoucherRepository : IVoucherRepository
    {
        private readonly IDataObjectRepository dataAccess;

        public VoucherRepository(IDataObjectRepository dataAccess)
        {
            this.dataAccess = dataAccess;
        }

        public void Update(Voucher voucher)
        {
            var dataObject = dataAccess.Checkout(voucher.Id);

            try
            {
                // TODO: could check whether each property changes before updating...

                dataObject.Properties.Set(
                    VoucherAttributes.Dishonoured,
                    voucher.Dishonoured.ToString());

                // TODO: could also avoid update if nothing changes

                dataAccess.Checkin(dataObject);

                Log.Information("Repository updated.");
            }
            catch (Exception)
            {
                dataAccess.CancelCheckout(voucher.Id);
                throw;
            }
        }

        public Voucher GetVoucher(string auxDom, string bsb, string accountNumber, string amount,  bool includeFile, DateTime earliestDate)
        {
            Log.Debug("VoucherRepository GetVoucher {@auxDom} {@bsb} {@amount}", auxDom, bsb, amount);

            var dataObject = GetVoucherDataObject(auxDom, bsb, accountNumber, amount, includeFile, earliestDate);

            if (dataObject == null)
            {
                return null;
            }

            Log.Information("Retrieved voucher: {@objectId}", dataObject.Identity.GetValueAsString());

            return GetVoucherFromDataObject(dataObject, includeFile);
        }

        public Voucher GetVoucher(string objectId, bool includeFile)
        {
            Log.Debug("VoucherRepository GetVoucher {@objectId}", objectId);

            DataObject voucherObject = dataAccess.GetDataObjectByObjectId(objectId);
            if (voucherObject == null)
            {
                return null;
            }

            Log.Information("Retrieved voucher: {@objectId}", objectId);
            return GetVoucherFromDataObject(voucherObject, includeFile);
        }

        private DataObject GetVoucherDataObject(string auxDom, string bsb, string accountNumber, string amount, bool includeFile, DateTime earliestDate)
        {
            Log.Debug(
                "VoucherRepository GetVoucher {@auxDom} {@bsb} {@accountNumber} {@amount} cut-off {@earliestDate}",
                auxDom,
                bsb,
                accountNumber,
                amount,
                earliestDate);

            var searchCriteria = new List<PropertyExpression>
            {
                new PropertyExpression(VoucherAttributes.AuxiliaryDomestic, Condition.EQUAL, auxDom ),
                new PropertyExpression( VoucherAttributes.Bsb, Condition.EQUAL, bsb ),
                new PropertyExpression( VoucherAttributes.AccountNumber, Condition.EQUAL, accountNumber ),
                new PropertyExpression( VoucherAttributes.Amount, Condition.EQUAL, amount ),
                new PropertyExpression( VoucherAttributes.ProcessingDate, Condition.GREATER_EQUAL, earliestDate.ToString("dd-MMM-yyyy")) 
            };


            var voucherObjects = dataAccess.FindDataObjects(DocumentumTypes.FxaVoucher, searchCriteria, includeFile);
            if (voucherObjects == null || voucherObjects.Count == 0)
            {
                Log.Error(
                    "Found no vouchers matching {@auxDom}:{@bsb}:{@account}:{@amount} cut-off {@earliestDate}",
                    auxDom,
                    bsb,
                    accountNumber,
                    amount,
                    earliestDate.ToString("yyyy.MM.dd"));
                return null;
            }
            if (voucherObjects.Count != 1)
            {
                Log.Error(
                    "Found {0} vouchers matching {@auxDom}:{@bsb}:{@account}:{@amount} cut-off {@earliestDate}",
                    voucherObjects.Count,
                    auxDom,
                    bsb,
                    accountNumber,
                    amount,
                    earliestDate);
                return null;
            }

            Log.Information("Retrieved voucher: {@objectId}", voucherObjects[0].Identity.GetValueAsString());

            return voucherObjects[0];
        }

        private Voucher GetVoucherFromDataObject(DataObject dataObject, bool includeFile)
        {
            var voucherId = dataObject.Identity.GetValueAsString();

            var ad = GetAttribute(dataObject, VoucherAttributes.AuxiliaryDomestic);
            var bsb = GetAttribute(dataObject, VoucherAttributes.Bsb);
            var accnt = GetAttribute(dataObject, VoucherAttributes.AccountNumber);
            var amount = GetAttribute(dataObject, VoucherAttributes.Amount);

            var procdate = GetDateTimeAttribute(dataObject, VoucherAttributes.ProcessingDate);

            // TODO: think about handling/parsing of other fields too

            var name = GetAttribute(dataObject, DocumentumAttributes.ObjectName);

            var dishonoured = GetBooleanlAttribute(dataObject, VoucherAttributes.Dishonoured);
            var delayedImage = GetBooleanlAttribute(dataObject, VoucherAttributes.DelayedImage);

            var voucher = new Voucher
            {
                Id = voucherId,
                Name = name,
                AuxiliaryDomestic = ad,
                Bsb = bsb,
                AccountNumber = accnt,
                Amount = amount,
                Dishonoured = dishonoured,
                DelayedImage = delayedImage,
                ProcessingDate = procdate
            };

            if (includeFile && dataObject.Contents != null && dataObject.Contents.Count > 0)
            {
                var resultContent = dataObject.Contents[0];
                if (resultContent != null && resultContent.CanGetAsFile())
                {
                    var fileInfo = resultContent.GetAsFile();

                    using (var reader = new BinaryReader(fileInfo.OpenRead()))
                    {
                        voucher.File = reader.ReadBytes((int)fileInfo.Length);
                    }
                }
            }

            return voucher;
        }

        private static string GetAttribute(DataObject dataObject, string attributeName)
        {
            try
            {
                return dataObject.GetProperty(attributeName).GetValueAsString();
            }
            catch (PropertyNotFoundException ex)
            {
                Log.Warning(ex.Message);
                return null;
            }
        }

        // TODO: think about error handling/parsing of voucher fields
        private static decimal GetDecimalAttribute(DataObject dataObject, string attributeName)
        {
            var fldString = GetAttribute(dataObject, attributeName);

            decimal fldAsDecimal;
            if (!Decimal.TryParse(fldString, out fldAsDecimal))
            {
                Log.Error("Cannot parse voucher decimal field name:{0} value:{1}.", attributeName, fldString);
            }

            return fldAsDecimal;
        }

        private static bool GetBooleanlAttribute(DataObject dataObject, string attributeName)
        {
            var fldString = GetAttribute(dataObject, attributeName);

            bool fldAsBool;
            if (!Boolean.TryParse(fldString, out fldAsBool))
            {
                Log.Warning("Cannot parse voucher boolean field name:{0} value:{1}.", attributeName, fldString);
            }

            return fldAsBool;
        }

        private static DateTime GetDateTimeAttribute(DataObject dataObject, string attributeName)
        {
            var fldString = GetAttribute(dataObject, attributeName);

            DateTime fldAsDateTime;
            if (!DateTime.TryParse(fldString, out fldAsDateTime))
            {
                Log.Error("Cannot parse voucher date time field name:{0} value:{1}.", attributeName, fldString);
            }

            return fldAsDateTime;
        }

    }
}