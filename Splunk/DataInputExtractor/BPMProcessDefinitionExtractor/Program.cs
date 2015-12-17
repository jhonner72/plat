using CmdLine;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.IO;
using System.Text;
using System.Configuration;

namespace BPMProcessDefinitionExtractor
{
    /// <summary>
    /// Extract the data from camunda bpm process definition table and output it to console which 
    /// splunk's data input service will use to index the data
    /// </summary>
    public class Program
    {
        public static void Main(string[] args)
        {
            BPMProcessDefinitionExtractorArguments arguments = GetArguments();

            if (arguments == null)
            {
                return;
            }

            using (var connection = new SqlConnection(ConfigurationManager.ConnectionStrings["camundaDB"].ToString()))
            {
                var currentRunResult = new RunResult();
                string jsonContent = string.Empty;

                try
                {
                    jsonContent = File.ReadAllText(arguments.JsonFilename);
                }
                catch(IOException)
                {
                }

                var runResult = JsonConvert.DeserializeObject<RunResult>(jsonContent);
                if (runResult == null)
                {
                    runResult = new RunResult();
                }

                StringBuilder sbCommandText = new StringBuilder(@"SELECT DISTINCT [ID_]
                        ,[NAME_]
                        ,[KEY_]
                        ,[VERSION_]
                    FROM [ACT_RE_PROCDEF]");

                var command = new SqlCommand(sbCommandText.ToString(), connection);

                connection.Open();
                
                var reader = command.ExecuteReader();

                if (reader.HasRows)
                {
                    while (reader.Read())
                    {
                        var process = new BpmProcessDefinition();

                        process.ProcessInstanceId = reader.GetString(reader.GetOrdinal("ID_"));
                        process.Name = reader.GetNullableValue<string>(reader.GetOrdinal("NAME_"));
                        process.BusinessKey = reader.GetNullableValue<string>(reader.GetOrdinal("KEY_"));
                        process.Version = reader.GetInt32(reader.GetOrdinal("VERSION_"));

                        if (!runResult.Processes.Contains(process))
                        {                            
                            Console.WriteLine(JsonConvert.SerializeObject(process));
                        }

                        currentRunResult.Processes.Add(process);
                    }
                }
                reader.Close();

                jsonContent = JsonConvert.SerializeObject(currentRunResult);
                File.WriteAllText(arguments.JsonFilename, jsonContent);
            }
        }

        private static BPMProcessDefinitionExtractorArguments GetArguments()
        {
            try
            {
                var arguments = CommandLine.Parse<BPMProcessDefinitionExtractorArguments>();
                return arguments;
            }
            catch (CommandLineException exception)
            {
                Console.WriteLine(exception.ArgumentHelp.Message);
                Console.WriteLine(exception.ArgumentHelp.GetHelpText(Console.BufferWidth));
            }
            return null;
        }
    }
}
