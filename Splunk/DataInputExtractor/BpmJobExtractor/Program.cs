using CmdLine;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data.SqlClient;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BpmJobExtractor
{
    /// <summary>
    /// Extract the data from camunda bpm history table and output it to console which 
    /// splunk's data input service will use to index the data
    /// </summary>
    public class Program
    {
        public static void Main(string[] args)
        {
            BpmJobExtractorArguments arguments = GetArguments();

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
                catch (IOException)
                {
                }

                var runResult = JsonConvert.DeserializeObject<RunResult>(jsonContent);
                if (runResult == null)
                {
                    runResult = new RunResult();
                }

                currentRunResult.PreviousMaxEndTime = runResult.PreviousMaxEndTime;

                StringBuilder sbCommandText =
                    new StringBuilder(@"SELECT [PROCESS_INSTANCE_ID_] as ProcessInstanceId
                                              ,[PROCESS_DEF_ID_] as ProcessDefinitionId
	                                          ,i.[BUSINESS_KEY_] as BusinessKey
	                                          ,pd.[NAME_] as ProcessName
	                                          ,ai.[ACT_NAME_] as ActivityName
	                                          ,ai.[ACT_ID_] as ActivityId
                                              ,jd.[JOB_CONFIGURATION_] as JobConfigruation
	                                          ,[TYPE_] as JobType
	                                          ,[RETRIES_] as Retries
                                              ,[EXCEPTION_MSG_] as ExceptionMessage
	                                          ,j.[DUEDATE_] as EndDate
                                       FROM [dbo].[ACT_RU_JOB] j 
                                          inner join [dbo].[ACT_RU_JOBDEF] jd on j.JOB_DEF_ID_ = jd.ID_
                                          inner join [dbo].[ACT_HI_ACTINST] ai on ai.ACT_ID_ = jd.ACT_ID_ and j.EXECUTION_ID_ = ai.EXECUTION_ID_ 
                                          inner join [dbo].[ACT_HI_PROCINST] i on ai.PROC_INST_ID_ = i.PROC_INST_ID_
                                          inner join [dbo].[ACT_RE_PROCDEF] pd on i.PROC_DEF_ID_ = pd.ID_
                                       WHERE pd.KEY_ = @ProcessDefinitionKey");

                var parameters = new List<SqlParameter>()
                {
                    new SqlParameter("@ProcessDefinitionKey", arguments.ProcessDefinitionKey)
                };

                if (runResult.PreviousMaxEndTime.HasValue)
                {
                    sbCommandText.AppendLine(" AND (j.[DUEDATE_] >= @EndTime)");
                    parameters.Add(new SqlParameter("@EndTime", runResult.PreviousMaxEndTime));
                }

                sbCommandText.AppendLine(" ORDER BY j.[DUEDATE_]");

                var command = new SqlCommand(sbCommandText.ToString(), connection);
                command.Parameters.AddRange(parameters.ToArray());

                connection.Open();

                var reader = command.ExecuteReader();

                if (reader.HasRows)
                {
                    while (reader.Read())
                    {
                        var process = new BpmJob();

                        process.ProcessInstanceId = reader.GetString(reader.GetOrdinal("ProcessInstanceId"));
                        process.ProcessDefinitionId = reader.GetString(reader.GetOrdinal("ProcessDefinitionId"));
                        process.BusinessKey = reader.GetNullableValue<string>(reader.GetOrdinal("BusinessKey"));
                        process.ProcessName = reader.GetNullableValue<string>(reader.GetOrdinal("ProcessName"));
                        process.ActivityId = reader.GetNullableValue<string>(reader.GetOrdinal("ActivityId"));
                        process.ActivityName = reader.GetNullableValue<string>(reader.GetOrdinal("ActivityName"));
                        process.JobConfiguration = reader.GetNullableValue<string>(reader.GetOrdinal("JobConfigruation"));
                        process.JobType = reader.GetNullableValue<string>(reader.GetOrdinal("JobType"));
                        process.EndTime = reader.GetDateTime(reader.GetOrdinal("EndDate"));
                        process.Retries = reader.GetInt32(reader.GetOrdinal("Retries"));
                        process.ExceptionMessage = reader.GetNullableValue<string>(reader.GetOrdinal("ExceptionMessage"));

                        if (!runResult.Activities.Contains(process))
                        {
                            currentRunResult.PreviousMaxEndTime = process.EndTime;

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

        private static BpmJobExtractorArguments GetArguments()
        {
            try
            {
                var arguments = CommandLine.Parse<BpmJobExtractorArguments>();
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
