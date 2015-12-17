using System.Globalization;
using System.IO;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using System.Xml.Serialization;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Configuration;
using Lombard.Adapters.DipsAdapter.Domain;
using Lombard.Common;
using Serilog;
using System;
using System.Collections.Generic;
using System.IO.Abstractions;
using System.Linq;
using Lombard.Adapters.DipsAdapter.Helpers.Interfaces;

namespace Lombard.Adapters.DipsAdapter.Helpers
{
    public class ImageMergeHelper : IImageMergeHelper
    {
        private readonly IFileSystem fileSystem;
        private readonly IAdapterConfiguration adapterConfiguration;

        public ImageMergeHelper(
            IFileSystem fileSystem,
            IAdapterConfiguration adapterConfiguration)
        {
            this.fileSystem = fileSystem;
            this.adapterConfiguration = adapterConfiguration;
        }

        public async Task EnsureMergedImageFilesExistAsync(string jobIdentifier, string batchNumber, DateTime processingDate)
        {
            Guard.IsNotNull(jobIdentifier, "jobIdentifier");
            Guard.IsNotNull(batchNumber, "batchNumber");

            Log.Debug("GenerateMergedImageFiles jobId:{@jobIdentifier}, batch: {@batchNumber}, processingDate: {@processingDate}", jobIdentifier, batchNumber, processingDate);

            try
            {
                var jobImageDirectory = GetJobImageDirectory(jobIdentifier);
                var targetDirectory = GetDestinationDirectory(batchNumber);

                var metadataFilename = GetMetadataFilename(targetDirectory, batchNumber);

                if (fileSystem.File.Exists(metadataFilename))
                {
                    Log.Debug("Merged image files already exist for job '{@jobIdentifier}'. Skipping file generation.", jobIdentifier);
                }

                var batch = ReadBatchImageInfo(batchNumber, jobImageDirectory);

                await WriteMergedImageFilesAsync(batchNumber, targetDirectory, batch);

                //write metadata file
                WriteToXmlFile(metadataFilename, batch);

                Log.Information("Merged {0} images for job '{@jobIdentifier}'", batch.Vouchers.Count, jobIdentifier);
            }
            catch (Exception ex)
            {
                Log.Error(ex, "Could not create merged images for job '{@jobIdentifier}'", jobIdentifier);
                throw;
            }
        }

        // ReSharper disable PossibleMultipleEnumeration
        public void PopulateMergedImageInfo(string jobIdentifier, string batchNumber, IEnumerable<DipsNabChq> vouchers)
        {
            Guard.IsNotNull(jobIdentifier, "jobIdentifier");
            Guard.IsNotNullOrEmpty(vouchers, "vouchers");

            Log.Debug("PopulateMergedImageInfo jobId:{@jobIdentifier}, batch: {@batchNumber}", jobIdentifier, batchNumber);

            var imagesFolder = GetDestinationDirectory(batchNumber);

            var metadataFilename = GetMetadataFilename(imagesFolder, batchNumber);

            try
            {
                if (!fileSystem.File.Exists(metadataFilename))
                {
                    throw new FileNotFoundException(string.Format("Merged image files for job '{0}' do not exist. Could not find '{1}'.", jobIdentifier, metadataFilename));
                }

                var batch = ReadFromXmlFile<ImageMergeBatch>(metadataFilename);

                //validate that we have images for all vouchers
                var traceNumbers = batch.Vouchers.Select(v => v.TraceNumber.ToString().PadLeft(9, '0'));
                
                var originalVouchers = vouchers.Where(x => x.isGeneratedVoucher != "1");

                if (originalVouchers.Any(x => !traceNumbers.Contains(x.S_TRACE)))
                {
                    throw new FileNotFoundException(string.Format("Could not find images for all vouchers for job '{0}'", jobIdentifier));
                }

                //populate
                foreach (var voucher in originalVouchers)
                {
                    var voucherImage = batch.Vouchers.Single(x => x.TraceNumber.PadLeft(9, '0') == voucher.S_TRACE);
                    //var voucherImage = batch.Vouchers.Single(x => x.TraceNumber == voucher.doc_ref_num);
                    voucher.S_IMG1_OFF = voucherImage.FrontOffset.ToString(CultureInfo.InvariantCulture).PadLeft(10);
                    voucher.S_IMG1_LEN = voucherImage.FrontLength.ToString(CultureInfo.InvariantCulture).PadLeft(10);
                    voucher.S_IMG1_TYP = "0".PadLeft(5);
                    voucher.S_IMG2_OFF = voucherImage.RearOffset.ToString(CultureInfo.InvariantCulture).PadLeft(10);
                    voucher.S_IMG2_LEN = voucherImage.RearLength.ToString(CultureInfo.InvariantCulture).PadLeft(10);
                    voucher.S_IMG2_TYP = "0".PadLeft(5);
                }

                Log.Information("Populated merged image info for {0} vouchers for job '{@jobIdentifier}'", originalVouchers.Count(), jobIdentifier);

            }
            catch (Exception ex)
            {
                Log.Error(ex, "Could not populate merged image info for job '{@jobIdentifier}'", jobIdentifier);
                throw;
            }
        }
        // ReSharper restore PossibleMultipleEnumeration

        private string GetDestinationDirectory(string batchNumber)
        {
            var directory = string.Format(@"{0}\{1}", adapterConfiguration.ImagePath, batchNumber.Substring(0, 5));

            if (!fileSystem.Directory.Exists(directory))
            {
                fileSystem.Directory.CreateDirectory(directory);
            }

            return directory;
        }

        private async Task WriteMergedImageFilesAsync(string batchNumber, string targetDirectory, ImageMergeBatch batch)
        {
            var frontFileName = string.Format(@"{0}\{1}", targetDirectory, string.Format(adapterConfiguration.ImageMergeFrontFilename, batchNumber));
            var rearFileName = string.Format(@"{0}\{1}", targetDirectory, string.Format(adapterConfiguration.ImageMergeRearFilename, batchNumber));

            using (var mergedFrontFile = fileSystem.File.Create(frontFileName))
            using (var mergedRearFile = fileSystem.File.Create(rearFileName))
            {
                long frontOffset = 0;
                long rearOffset = 0;

                //read and copy all images async
                foreach (var voucher in batch.Vouchers)
                {
                    using (var frontImageFileStream = fileSystem.File.OpenRead(voucher.FrontImageFilename))
                    using (var rearImageFileStream = fileSystem.File.OpenRead(voucher.RearImageFilename))
                    {

                        voucher.FrontLength = frontImageFileStream.Length;
                        voucher.RearLength = rearImageFileStream.Length;

                        voucher.FrontOffset = frontOffset;
                        voucher.RearOffset = rearOffset;

                        frontOffset += voucher.FrontLength;
                        rearOffset += voucher.RearLength;

                        var frontTask = frontImageFileStream.CopyToAsync(mergedFrontFile);
                        var rearTask = rearImageFileStream.CopyToAsync(mergedRearFile);

                        await Task.WhenAll(frontTask, rearTask);
                    }
                }
            }
        }

        private static string GetMetadataFilename(string targetDirectory, string batchNumber)
        {
            return string.Format(@"{0}\IM{1}.xml", targetDirectory, batchNumber);
        }

        private string GetJobImageDirectory(string jobIdentifier)
        {
            var directory = string.Format(adapterConfiguration.PackageSourceDirectory, jobIdentifier);

            if (!fileSystem.Directory.Exists(directory))
            {
                throw new DirectoryNotFoundException(string.Format("Could not find the image folder '{0}'", directory));
            }

            return directory;
        }

        private ImageMergeBatch ReadBatchImageInfo(string batchNumber, string jobFolder)
        {
            var batch = new ImageMergeBatch { BatchNumber = batchNumber };

            var frontRegex = new Regex(string.Format(adapterConfiguration.ImageMergeFrontFileRegex),
                RegexOptions.Compiled | RegexOptions.IgnoreCase);

            //get all front filenames that match the regex, and also extract the trace number using the regex
            var frontFiles = fileSystem.Directory
                .GetFiles(jobFolder)
                .Select(filename => new { filename, match = frontRegex.Match(filename) })
                .Where(x => x.match.Success)
                .Select(x => new { x.filename, traceNumber = x.match.Groups["trace"].Value })
                .ToList();

            if (!frontFiles.Any())
            {
                throw new InvalidOperationException("No image files found for this job");
            }

            //read all images
            foreach (var frontFile in frontFiles)
            {
                Log.Verbose("Adding front file {@frontFile} to the merge result", frontFile);

                string traceNumber = frontFile.traceNumber;

                var voucher = new ImageMergeVoucher
                {
                    TraceNumber = traceNumber,
                    FrontImageFilename = frontFile.filename,
                    RearImageFilename = frontFile.filename.Replace("FRONT", "REAR"),
                };

                batch.Vouchers.Add(voucher);
            }

            return batch;
        }

        private void WriteToXmlFile<T>(string filePath, T objectToWrite) where T : new()
        {
            using (var writer = fileSystem.File.CreateText(filePath))
            {
                var serializer = new XmlSerializer(typeof(T));
                serializer.Serialize(writer, objectToWrite);
            }
        }

        private T ReadFromXmlFile<T>(string filePath) where T : new()
        {
            using (var reader = fileSystem.File.OpenText(filePath))
            {
                var serializer = new XmlSerializer(typeof (T));
                return (T) serializer.Deserialize(reader);
            }
        }
    }
}