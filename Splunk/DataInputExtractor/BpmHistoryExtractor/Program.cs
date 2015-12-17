using CmdLine;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.IO;
using System.Linq;
using System.Text;
using System.Configuration;

namespace BpmHistoryExtractor
{
    /// <summary>
    /// Extract the data from camunda bpm history table and output it to console which 
    /// splunk's data input service will use to index the data
    /// </summary>
    public class Program
    {
        public static void Main(string[] args)
        {
            BpmHistoryExtractorArguments arguments = GetArguments();

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

                currentRunResult.PreviousMaxStartTime = runResult.PreviousMaxStartTime;

                StringBuilder sbCommandText = new StringBuilder(@"SELECT 
	                    d.ID_ AS [ProcessDefinitionId], 
	                    i.PROC_INST_ID_  AS [ProcessInstanceId], 
	                    i.BUSINESS_KEY_ AS [BusinessKey], 
	                    v.TEXT_ AS [FileName], 
	                    i.START_TIME_ AS [StartTime], 
	                    i.END_TIME_ AS [EndTime],
	                    i.DURATION_ AS [Duration]
                    FROM ACT_RE_PROCDEF d 
                    INNER JOIN ACT_HI_PROCINST i ON i.PROC_DEF_ID_ = d.ID_
                    LEFT JOIN ACT_HI_VARINST v ON i.PROC_INST_ID_ = v.PROC_INST_ID_ AND v.NAME_ = 'inboundFilename'
                    WHERE d.KEY_ = @ProcessDefinitionKey");

                var parameters = new List<SqlParameter>()
                {
                    new SqlParameter("@ProcessDefinitionKey", arguments.ProcessDefinitionKey)
                };

                if (runResult.PreviousMaxStartTime.HasValue)
                {
                    sbCommandText.AppendLine(" AND i.[START_TIME_] >= @StartTime");
                    parameters.Add(new SqlParameter("@StartTime", runResult.PreviousMaxStartTime));
                }

                sbCommandText.AppendLine(" ORDER BY i.[START_TIME_]");

                var command = new SqlCommand(sbCommandText.ToString(), connection);
                command.Parameters.AddRange(parameters.ToArray());

                connection.Open();
                
                var reader = command.ExecuteReader();

                if (reader.HasRows)
                {
                    while (reader.Read())
                    {
                        var process = new BpmHistory();

                        string processInstanceId = reader.GetString(reader.GetOrdinal("ProcessInstanceId"));

                        if (!runResult.ProcessInstanceIds.Contains(processInstanceId, StringComparer.InvariantCultureIgnoreCase))
                        {
                            process.ProcessInstanceId = processInstanceId;
                            process.ProcessDefinitionId = reader.GetString(reader.GetOrdinal("ProcessDefinitionId"));
                            process.BusinessKey = reader.GetString(reader.GetOrdinal("BusinessKey"));
                            process.Filename = reader.GetString(reader.GetOrdinal("FileName"));
                            process.StartTime = reader.GetDateTime(reader.GetOrdinal("StartTime"));
                            process.EndTime = reader.GetNullableDateTime(reader.GetOrdinal("EndTime"));
                            process.Duration = reader.GetNullableDecimal(reader.GetOrdinal("Duration"));

                            currentRunResult.PreviousMaxStartTime = process.StartTime;

                            Console.WriteLine(JsonConvert.SerializeObject(process));
                        }

                        currentRunResult.ProcessInstanceIds.Add(processInstanceId);
                    }
                }
                reader.Close();

                jsonContent = JsonConvert.SerializeObject(currentRunResult);
                File.WriteAllText(arguments.JsonFilename, jsonContent);
            }
        }

        private static BpmHistoryExtractorArguments GetArguments()
        {
            try
            {
                var arguments = CommandLine.Parse<BpmHistoryExtractorArguments>();
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
