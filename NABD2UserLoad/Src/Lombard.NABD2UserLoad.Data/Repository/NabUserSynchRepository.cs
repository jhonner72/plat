namespace Lombard.NABD2UserLoad.Data
{
    using System;
    using Microsoft.Practices.EnterpriseLibrary.Data.Sql;
    using System.Data;
    using Serilog;
    using System.IO;
    using System.Data.SqlClient;
    using System.Data.Common;
    using Microsoft.Practices.EnterpriseLibrary.Data;
    public interface INabUserSynchRepository
    {
        void Execute(string filePath, string archiveFilePath);
    }

    public class NabUserSynchRepository : INabUserSynchRepository
    {
        private readonly Database database;
        public NabUserSynchRepository(Database database)
        {
            this.database = database;
        }

        public void Execute(string filePath, string archiveFilePath)
        {
            try
            {
                using (SqlConnection conn = database.CreateConnection() as SqlConnection)
                {
                    conn.Open();
                    bool refreshResult = RefreshDataYesterday(filePath, conn);

                    if (refreshResult)
                    {
                        try
                        {
                            Log.Information("Start archive file {0}", filePath);
                            if (File.Exists(archiveFilePath))
                            {
                                File.Delete(archiveFilePath);
                            }
                            File.Move(filePath, archiveFilePath);
                            Log.Information("Finish archive file {0}", filePath);
                        }
                        catch (Exception ex)
                        {
                            throw new Exception(string.Format("An error occurs while archiving file {0}", filePath), ex);
                        }
                        try
                        {
                            

                            Log.Information("Start update the status of the documentum users.");

                            using (var dbCommand = database.GetStoredProcCommand(@"[dbo].[sp_nabusersynch]"))
                            {
                                database.ExecuteNonQuery(dbCommand);
                            }
                            Log.Information("Finish update the status of the documentum users.");


                        }
                        catch (SqlException ex)
                        {
                            throw new Exception("An error occurs while updating the status of the documentum users", ex);
                        }
                    }

                    conn.Close();
                }
            }
            catch (Exception ex)
            {
                const string Message = "Connection error.";
                Log.Error(ex, Message);
            }
        }

        private bool RefreshDataYesterday(string filePath, SqlConnection conn)
        {
            bool refreshResult;
            using (SqlTransaction trans = conn.BeginTransaction())
            {
                try
                {
                    Log.Information("start refresh fx nab d2 user data.");

                    CleanYesterdayData(trans);

                    CopyTodayToYesterday(trans);

                    CleanTodayData(trans);

                    LoadTodayDataFromCSV(filePath, conn, trans);

                    // Commit the transaction.
                    trans.Commit();

                    refreshResult = true;

                    Log.Information("Finish refresh fx nab d2 user data.");
                }
                catch (Exception ex)
                {
                    Log.Error(ex, ex.Message);

                    // Roll back the transaction. 
                    trans.Rollback();
                    refreshResult = false;
                }
            }
            return refreshResult;
        }

        private static void LoadTodayDataFromCSV(string filePath, SqlConnection conn, SqlTransaction trans)
        {
            try
            {
                Log.Information("Start load file {0} to nabusersynch table.", filePath);
                //Insert CSV data    
                using (SqlBulkCopy objbulk = new SqlBulkCopy(conn, SqlBulkCopyOptions.Default, trans))
                {
                    DataTable nabUserTable = CreateNabUserDataTable(filePath);

                    //assigning Destination table name    
                    objbulk.DestinationTableName = "nabusersynch_today";
                    //Mapping Table column    
                    objbulk.ColumnMappings.Add("id", "user_id");
                    objbulk.ColumnMappings.Add("firstname", "first_name");
                    objbulk.ColumnMappings.Add("lastname", "last_name");
                    objbulk.ColumnMappings.Add("useremail", "user_email");
                    objbulk.ColumnMappings.Add("usergroup", "user_group");
                    objbulk.WriteToServer(nabUserTable);
                }
                Log.Information("Finish load file {0} to nabusersynch table.", filePath);
            }
            catch (SqlException ex)
            {
                throw new Exception(string.Format("An error occurs while loading file {0} to nabusersynch table.", filePath), ex);
            }
            
        }

        private void CleanTodayData(SqlTransaction trans)
        {
            try
            {
                Log.Information("Start clean up today data");

                string deleteStatement = @"DELETE FROM [dbo].[nabusersynch_today]";
                using (var dbCommand = database.GetSqlStringCommand(deleteStatement))
                {
                    database.ExecuteNonQuery(dbCommand, trans);
                }

                Log.Information("Finish clean up today data");
            }
            catch (SqlException ex)
            {
                throw new Exception("An error occurs while cleaning up today data", ex);
            }
            
        }

        private void CopyTodayToYesterday(SqlTransaction trans)
        {
            
            try
            {
                Log.Information("Start copy today data to yesterday data");

                string insertStatement = @"INSERT INTO [dbo].[nabusersynch_yesterday] SELECT * FROM [dbo].[nabusersynch_today]";
                using (var dbCommand = database.GetSqlStringCommand(insertStatement))
                {
                    database.ExecuteNonQuery(dbCommand, trans);
                }

                Log.Information("Finish copy today data to yesterday data");
            }
            catch (SqlException ex)
            {
                throw new Exception("An error occurs while copying today data to yesterday data", ex);
            }
        }

        private void CleanYesterdayData(SqlTransaction trans)
        {
            try
            {
                Log.Information("Start clean up yesterday data");

                string deleteStatement = @"DELETE FROM [dbo].[nabusersynch_yesterday]";
                using (var dbCommand = database.GetSqlStringCommand(deleteStatement))
                {
                    database.ExecuteNonQuery(dbCommand, trans);
                }

                Log.Information("Finish clean up yesterday data");
            }
            catch (SqlException ex)
            {
                throw new Exception("An error occurs while cleaning up yesterday data", ex);
            }
        }

        private static DataTable CreateNabUserDataTable(string fileName)
        {
            //Creating object of datatable  
            DataTable nabUserTable = new DataTable();
            //creating columns  
            nabUserTable.Columns.Add("id");
            nabUserTable.Columns.Add("firstname");
            nabUserTable.Columns.Add("lastname");
            nabUserTable.Columns.Add("useremail");
            nabUserTable.Columns.Add("usergroup");

            string nabUserCsvData = File.ReadAllText(fileName);
            //spliting row after new line  
            foreach (string csvRow in nabUserCsvData.Split('\n'))
            {
                if (!string.IsNullOrEmpty(csvRow))
                {
                    //Adding each row into datatable  
                    nabUserTable.Rows.Add();
                    int count = 0;
                    foreach (string FileRec in csvRow.Split(','))
                    {
                        nabUserTable.Rows[nabUserTable.Rows.Count - 1][count] = FileRec.Replace("\"",string.Empty);
                        count++;
                    }
                }
            }
            return nabUserTable;
        }
    }
}
