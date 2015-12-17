using CmdLine;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.IO;
using System.Text;
using System.Configuration;

namespace BpmActivityExtractor
{
    /// <summary>
    /// Extract the data from camunda bpm history table and output it to console which 
    /// splunk's data input service will use to index the data
    /// </summary>
    public class Program
    {
        public static void Main(string[] args)
        {
            BpmActivityExtractorArguments arguments = GetArguments();

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

                StringBuilder sbCommandText = 
                    new StringBuilder(@"SELECT i.PROC_INST_ID_ AS [ProcessInstanceId],
                        pd.ID_ AS [ProcessDefinitionId],
                        i.BUSINESS_KEY_ AS [BusinessKey], ai.ACT_ID_ AS [ActivityId],
                        ai.ACT_NAME_ AS [ActivityName], ai.ACT_INST_STATE_ AS [ActivityState], 
						ai.START_TIME_ AS [ActivityStartTime], ai.END_TIME_ AS [ActivityEndTime], ai.DURATION_ AS [ActivityDuration]
                    FROM ACT_RE_PROCDEF pd
                    INNER JOIN ACT_HI_PROCINST i ON i.PROC_DEF_ID_ = pd.ID_
                    INNER JOIN ACT_HI_ACTINST ai ON ai.PROC_INST_ID_ = i.PROC_INST_ID_
                    WHERE pd.KEY_ = @ProcessDefinitionKey AND ai.ACT_NAME_ IS NOT NULL");

                var parameters = new List<SqlParameter>()
                {
                    new SqlParameter("@ProcessDefinitionKey", arguments.ProcessDefinitionKey)
                };

                if (runResult.PreviousMaxEndTime.HasValue)
                {
                    sbCommandText.AppendLine(" AND (ai.[END_TIME_] IS NULL OR ai.[END_TIME_] >= @EndTime)");
                    parameters.Add(new SqlParameter("@EndTime", runResult.PreviousMaxEndTime));
                }
                else
                {
                    sbCommandText.AppendLine(" AND ai.[END_TIME_] IS NOT NULL");
                }

                sbCommandText.AppendLine(" ORDER BY ai.[END_TIME_]");

                var command = new SqlCommand(sbCommandText.ToString(), connection);
                command.Parameters.AddRange(parameters.ToArray());

                connection.Open();
                
                var reader = command.ExecuteReader();

                if (reader.HasRows)
                {
                    while (reader.Read())
                    {
                        var process = new BpmActivity();

                        process.ProcessInstanceId = reader.GetString(reader.GetOrdinal("ProcessInstanceId"));
                        process.ProcessDefinitionId = reader.GetString(reader.GetOrdinal("ProcessDefinitionId"));
                        process.BusinessKey = reader.GetNullableValue<string>(reader.GetOrdinal("BusinessKey"));
                        process.Id = reader.GetString(reader.GetOrdinal("ActivityId"));
                        process.Name = reader.GetNullableValue<string>(reader.GetOrdinal("ActivityName"));
                        process.State = reader.GetByte(reader.GetOrdinal("ActivityState"));
                        process.StartTime = reader.GetDateTime(reader.GetOrdinal("ActivityStartTime"));
                        process.EndTime = reader.GetNullableDateTime(reader.GetOrdinal("ActivityEndTime"));
                        if (!reader.IsDBNull(reader.GetOrdinal("ActivityDuration")))
                            process.Duration = reader["ActivityDuration"].ToString();
                       
                        if (!runResult.Activities.Contains(process))
                        {
                            if (process.EndTime.HasValue)
                            {
                                currentRunResult.PreviousMaxEndTime = process.EndTime;
                            }

                            Console.WriteLine(JsonConvert.SerializeObject(process));
                        }

                        currentRunResult.Activities.Add(process);
                    }
                }
                reader.Close();

                jsonContent = JsonConvert.SerializeObject(currentRunResult);
                File.WriteAllText(arguments.JsonFilename, jsonContent);
            }
        }

        private static BpmActivityExtractorArguments GetArguments()
        {
            try
            {
                var arguments = CommandLine.Parse<BpmActivityExtractorArguments>();
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
