using CmdLine;

namespace BpmHistoryExtractor
{
    [CommandLineArguments(Program = "BpmHistoryExtractor", Title = "Bpm History Extractor", Description = "Extract information from Camunda Bpm for Voucher Processing and Image Exchange")]
    public class BpmHistoryExtractorArguments
    {
        [CommandLineParameter(Command = "?", Default = false, Description = "Show Help", Name = "Help", IsHelp = true)]
        public bool Help { get; set; }

        [CommandLineParameter(Name = "processDefinitionKey", ParameterIndex = 1, Required = true, Description = "Specify the Camunda process definition key to extract data for.")]
        public string ProcessDefinitionKey { get; set; }

        [CommandLineParameter(Name = "jsonFilename", ParameterIndex = 2, Required = true, Description = "Specify the json file path to store the run result to. This result is also use next time the script is run to filter data already sent to indexer")]
        public string JsonFilename { get; set; }
    }
}
