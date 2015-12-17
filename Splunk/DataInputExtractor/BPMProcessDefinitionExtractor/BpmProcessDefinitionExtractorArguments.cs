using CmdLine;

namespace BPMProcessDefinitionExtractor
{
    [CommandLineArguments(Program = "BPMProcessDefinitionExtractor", Title = "Bpm Process Definition Extractor", Description = "Extract information from Camunda Bpm regarding business process definitions")]
    public class BPMProcessDefinitionExtractorArguments
    {
        [CommandLineParameter(Command = "?", Default = false, Description = "Show Help", Name = "Help", IsHelp = true)]
        public bool Help { get; set; }

        [CommandLineParameter(Name = "jsonFilename", ParameterIndex = 1, Required = true, Description = "Specify the json file path to store the run result to. This result is also use next time the script is run to filter data already sent to indexer")]
        public string JsonFilename { get; set; }
    }
}
