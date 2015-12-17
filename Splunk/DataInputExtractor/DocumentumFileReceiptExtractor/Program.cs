using CmdLine;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.IO;
using System.Linq;
using System.Text;
using System.Configuration;

namespace DocumentumFileReceiptExtractor
{
    /// <summary>
    /// Extract the data from documentum file receipt table and output it to 
    /// console which splunk's data input service will use to index the data
    /// </summary>
    public class Program
    {
        public static void Main(string[] args)
        {
            DocumentumFileReceiptExtractorArgument arguments;
            try
            {
                arguments = CommandLine.Parse<DocumentumFileReceiptExtractorArgument>();
            }
            catch (CommandLineException exception)
            {
                Console.WriteLine(exception.ArgumentHelp.Message);
                Console.WriteLine(exception.ArgumentHelp.GetHelpText(Console.BufferWidth));

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

                currentRunResult.PreviousMaxReceivedDateTime = runResult.PreviousMaxReceivedDateTime;
                
                string query = "SELECT [file_id], [filename], [received_datetime], [transmission_datetime], [exchange] FROM "
                    + ConfigurationManager.AppSettings["documentumDB"].ToString() + ".[dbo].[fxa_file_receipt]";                
                StringBuilder sbCommandText = new StringBuilder(@query);

                var parameters = new List<SqlParameter>();

                if (runResult.PreviousMaxReceivedDateTime.HasValue)
                {
                    sbCommandText.AppendLine(" WHERE [received_datetime] >= @ReceivedDateTime");
                    parameters.Add(new SqlParameter("@ReceivedDateTime", runResult.PreviousMaxReceivedDateTime));
                }

                sbCommandText.AppendLine(" ORDER BY [received_datetime]");

                var command = new SqlCommand(sbCommandText.ToString(), connection);
                command.Parameters.AddRange(parameters.ToArray());

                connection.Open();

                var reader = command.ExecuteReader();

                if (reader.HasRows)
                {
                    while (reader.Read())
                    {
                        var fileReceipt = new DocumentumFileReceipt();

                        string fileId = reader.GetString(reader.GetOrdinal("file_id"));

                        if (!runResult.FileIds.Contains(fileId, StringComparer.InvariantCultureIgnoreCase))
                        {
                            fileReceipt.FileId = fileId;
                            fileReceipt.Filename = reader.GetString(reader.GetOrdinal("filename"));
                            fileReceipt.ReceivedDateTime = reader.GetDateTime(reader.GetOrdinal("received_datetime"));
                            fileReceipt.TransmissionDateTime = reader.GetDateTime(reader.GetOrdinal("transmission_datetime"));
                            fileReceipt.Exchange = reader.GetString(reader.GetOrdinal("exchange"));

                            currentRunResult.PreviousMaxReceivedDateTime = fileReceipt.ReceivedDateTime;

                            Console.WriteLine(JsonConvert.SerializeObject(fileReceipt));
                        }

                        currentRunResult.FileIds.Add(fileId);
                    }
                }
                reader.Close();

                jsonContent = JsonConvert.SerializeObject(currentRunResult);
                File.WriteAllText(arguments.JsonFilename, jsonContent);
            }
        }
    }
}
