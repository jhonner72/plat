using Lombard.Ingestion.Data.Domain;
using Serilog;
using System;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;
using System.Linq;

namespace Lombard.Ingestion.Data.Repository
{
    public class BulkIngestionRepository
    {
        private string connectionString;

        public BulkIngestionRepository(string connectionString)
        {
            this.connectionString = connectionString;
        }

        public virtual void BulkImportEclData(IList<AusPostEclData> entities, string fileState)
        {
            if (entities.Any())
            {
                using (var connection = new SqlConnection(connectionString))
                {
                    connection.Open();

                    using (var transaction = connection.BeginTransaction())
                    {
                        try
                        {
                            using (var command = new SqlCommand("DELETE FROM ref_aus_post_ecl_data WHERE file_state = @FileState", connection, transaction))
                            {
                                command.Parameters.AddWithValue("@FileState", fileState);

                                int deletedCount = command.ExecuteNonQuery();

                                Log.Information("Deleted {0} records.", deletedCount);
                            }

                            using (var bulkCopy = new SqlBulkCopy(connection, SqlBulkCopyOptions.Default, transaction))
                            {
                                bulkCopy.DestinationTableName = "ref_aus_post_ecl_data";
                                bulkCopy.ColumnMappings.Add("file_state", "file_state");
                                bulkCopy.ColumnMappings.Add("file_date", "file_date");
                                bulkCopy.ColumnMappings.Add("record_sequence", "record_sequence");
                                bulkCopy.ColumnMappings.Add("record_content", "record_content");
                                bulkCopy.ColumnMappings.Add("record_type", "record_type");

                                var eclDataTable = new DataTable();
                                eclDataTable.Columns.Add("file_state", typeof(string));
                                eclDataTable.Columns.Add("file_date", typeof(DateTime));
                                eclDataTable.Columns.Add("record_sequence", typeof(int));
                                eclDataTable.Columns.Add("record_content", typeof(string));
                                eclDataTable.Columns.Add("record_type", typeof(string));

                                foreach (var entity in entities)
                                {
                                    var row = eclDataTable.NewRow();

                                    row["file_state"] = entity.file_state;
                                    row["file_date"] = entity.file_date;
                                    row["record_sequence"] = entity.record_sequence;
                                    row["record_content"] = entity.record_content;
                                    row["record_type"] = entity.record_type;

                                    eclDataTable.Rows.Add(row);
                                }

                                bulkCopy.WriteToServer(eclDataTable);

                                Log.Information("Inserted {0} records.", entities.Count);
                            }

                            transaction.Commit();
                        }
                        catch (Exception)
                        {
                            transaction.Rollback();

                            throw;
                        }
                    }

                    connection.Close();
                }
            }
        }
    }
}
