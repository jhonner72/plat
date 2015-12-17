namespace Lombard.AdjustmentLetters.Service.Mappers
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.DataAnnotations;
    using System.Globalization;
    using System.IO.Abstractions;
    using System.Linq;
    using Lombard.AdjustmentLetters.Service.Configuration;
    using Lombard.AdjustmentLetters.Service.Domain;
    using Lombard.AdjustmentLetters.Service.Utils;
    using Lombard.Common.FileProcessors;
    using Lombard.Vif.Service.Messages.XsdImports;
    using Serilog;

    public interface IRequestSplitter : IMapper<CreateBatchAdjustmentLettersRequest, ValidatedResponse<IAdjustmentLetters>>
    {
    }

    public class RequestSplitter 
    {
        private readonly IFileSystem fileSystem;
        private readonly string bitLockerLocation;

        public RequestSplitter(IAdjustmentLettersConfiguration adjLettersConfiguration, IFileSystem fileSystem)
        {
            this.fileSystem = fileSystem;
            this.bitLockerLocation = adjLettersConfiguration.BitLockerLocation;
        }

        //public ValidatedResponse<IAdjustmentLetters> Map(CreateBatchAdjustmentLettersRequest request)
        //{
        //    var result = new IAdjustmentLetters();
        //    return ValidatedResponse<IAdjustmentLetters>.Success(result);
        //}

        private ValidatedResponse<IAdjustmentLetters> Failure(string failureMessage)
        {
            return ValidatedResponse<IAdjustmentLetters>.Failure(new List<ValidationResult> 
            { 
                    new ValidationResult(failureMessage)
            });
        }
    }
}
