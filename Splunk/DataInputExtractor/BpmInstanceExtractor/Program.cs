using CmdLine;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.IO;
using System.Text;
using System.Configuration;

namespace BpmInstanceExtractor
{
    /// <summary>
    /// Extract the data from camunda bpm history table and output it to console which 
    /// splunk's data input service will use to index the data
    /// </summary>
    public class Program
    {
        public static void Main(string[] args)
        {
            BpmInstanceExtractorArguments arguments = GetArguments();

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

                currentRunResult.PreviousMaxEndTime = runResult.PreviousMaxEndTime;

                StringBuilder sbCommandText = new StringBuilder(@"SELECT i.PROC_INST_ID_ AS [ProcessInstanceId],
                        pd.ID_ AS [ProcessDefinitionId],
                        i.BUSINESS_KEY_ AS [BusinessKey],
                        i.START_TIME_ AS [StartTime], i.END_TIME_ AS [EndTime]
                    FROM ACT_RE_PROCDEF pd
                    INNER JOIN ACT_HI_PROCINST i ON i.PROC_DEF_ID_ = pd.ID_
                    WHERE pd.KEY_ = @ProcessDefinitionKey");

                var parameters = new List<SqlParameter>()
                {
                    new SqlParameter("@ProcessDefinitionKey", arguments.ProcessDefinitionKey)
                };

                if (runResult.PreviousMaxEndTime.HasValue)
                {
                    sbCommandText.AppendLine(" AND (i.[END_TIME_] IS NULL OR i.[END_TIME_] >= @EndTime)");
                    parameters.Add(new SqlParameter("@EndTime", runResult.PreviousMaxEndTime));
                }

                sbCommandText.AppendLine(" ORDER BY i.[END_TIME_]");

                var command = new SqlCommand(sbCommandText.ToString(), connection);
                command.Parameters.AddRange(parameters.ToArray());

                connection.Open();
                
                var reader = command.ExecuteReader();

                if (reader.HasRows)
                {
                    while (reader.Read())
                    {
                        var process = new BpmInstance();

                        process.ProcessInstanceId = reader.GetString(reader.GetOrdinal("ProcessInstanceId"));
                        process.ProcessDefinitionId = reader.GetString(reader.GetOrdinal("ProcessDefinitionId"));
                        process.BusinessKey = reader.GetNullableValue<string>(reader.GetOrdinal("BusinessKey"));
                        process.StartTime = reader.GetDateTime(reader.GetOrdinal("StartTime"));
                        process.EndTime = reader.GetNullableDateTime(reader.GetOrdinal("EndTime"));

                        if (!runResult.Instances.Contains(process))
                        {
                            if (process.EndTime.HasValue)
                            {
                                currentRunResult.PreviousMaxEndTime = process.EndTime;
                            }

                            Console.WriteLine(JsonConvert.SerializeObject(process));
                        }

                        currentRunResult.Instances.Add(process);
                    }
                }
                reader.Close();

                jsonContent = JsonConvert.SerializeObject(currentRunResult);
                File.WriteAllText(arguments.JsonFilename, jsonContent);
            }
        }

        private static BpmInstanceExtractorArguments GetArguments()
        {
            try
            {
                var arguments = CommandLine.Parse<BpmInstanceExtractorArguments>();
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
