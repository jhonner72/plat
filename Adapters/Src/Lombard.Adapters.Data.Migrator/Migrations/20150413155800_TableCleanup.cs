using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    [Migration(20150413155800)]
    public class TableCleanup : Migration
    {

        private const string DbIndexDbName = "DB_INDEX";
        private const string QueueDbName = "queue";
        private const string NabChqOldDbName = "NabChqScanPod";
        private const string NabChqNewDbName = "NabChq";
       
        

        public override void Up()
        {
            
            #region Modify [dbo].[DB_INDEX] 
            /* Perform modifications to Table [dbo].[DB_INDEX] */
            Alter.Table(DbIndexDbName)
                .AlterColumn("DEL_IND")
                    .AsFixedLengthAnsiString(5)
                    .Nullable()
                .AlterColumn("BATCH")
                    .AsFixedLengthAnsiString(8)
                    .Nullable()
                .AlterColumn("TRACE")
                    .AsFixedLengthAnsiString(9)
                    .Nullable()
                .AlterColumn("SEQUENCE")
                    .AsFixedLengthAnsiString(5)
                    .Nullable()
                .AlterColumn("TABLE_NO")
                    .AsFixedLengthAnsiString(5)
                    .Nullable()
                .AlterColumn("REC_NO")
                    .AsFixedLengthAnsiString(10)
                    .Nullable();

                Rename.Column("DEL_IND")
                    .OnTable(DbIndexDbName)
                    .To("DEL_IND");

                Rename.Column("BATCH")
                    .OnTable(DbIndexDbName)
                    .To("BATCH");

                Rename.Column("TRACE")
                    .OnTable(DbIndexDbName)
                    .To("TRACE");

                Rename.Column("SEQUENCE")
                    .OnTable(DbIndexDbName)
                    .To("SEQUENCE");

                Rename.Column("TABLE_NO")
                    .OnTable(DbIndexDbName)
                    .To("TABLE_NO");

                Rename.Column("REC_NO")
                    .OnTable(DbIndexDbName)
                    .To("REC_NO");


            Create.Index("DB_INDEX_H").OnTable(DbIndexDbName)
                .OnColumn("BATCH")
                    .Ascending()
                .OnColumn("TRACE")
                    .Ascending()
                .OnColumn("SEQUENCE")
                    .Ascending()
                .WithOptions()
                    .NonClustered()
                .WithOptions()
                    .Unique();

            #endregion

            #region Modify [dbo].[queue]
            Alter.Table(QueueDbName)
              .AlterColumn("S_LOCATION")
                  .AsFixedLengthAnsiString(33)
                  .Nullable()
              .AlterColumn("S_PINDEX")
                  .AsFixedLengthAnsiString(16)
                  .Nullable()
              .AlterColumn("S_LOCK")
                  .AsFixedLengthAnsiString(10)
                  .Nullable()
              .AlterColumn("S_CLIENT")
                  .AsFixedLengthAnsiString(80)
                  .Nullable()
              .AlterColumn("S_JOB_ID")
                  .AsFixedLengthAnsiString(128)
                  .Nullable()
              .AlterColumn("S_MODIFIED")
                  .AsFixedLengthAnsiString(5)
                  .Nullable()
              .AlterColumn("S_COMPLETE")
                  .AsFixedLengthAnsiString(5)
                  .Nullable()
              .AlterColumn("S_BATCH")
                  .AsFixedLengthAnsiString(8)
                  .Nullable()
              .AlterColumn("S_TRACE")
                  .AsFixedLengthAnsiString(9)
                  .Nullable()
              .AlterColumn("S_SDATE")
                  .AsFixedLengthAnsiString(8)
                  .Nullable()
              .AlterColumn("S_STIME")
                  .AsFixedLengthAnsiString(8)
                  .Nullable()
              .AlterColumn("S_UTIME")
                  .AsFixedLengthAnsiString(10)
                  .Nullable()
              .AlterColumn("S_PRIORITY")
                  .AsFixedLengthAnsiString(5)
                  .Nullable()
              .AlterColumn("S_IMG_PATH")
                  .AsFixedLengthAnsiString(80)
                  .Nullable()
              .AlterColumn("S_USERNAME")
                  .AsFixedLengthAnsiString(250)
                  .Nullable()
              .AlterColumn("S_SELNSTRING")
                  .AsFixedLengthAnsiString(128)
                  .Nullable()
              .AlterColumn("S_VERSION")
                  .AsFixedLengthAnsiString(32)
                  .Nullable()
              .AlterColumn("S_LOCKUSER")
                  .AsFixedLengthAnsiString(17)
                  .Nullable()
              .AlterColumn("S_LOCKMODULENAME")
                  .AsFixedLengthAnsiString(17)
                  .Nullable()
              .AlterColumn("S_LOCKUNITID")
                  .AsFixedLengthAnsiString(10)
                  .Nullable()
              .AlterColumn("S_LOCKMACHINENAME")
                  .AsFixedLengthAnsiString(17)
                  .Nullable()
              .AlterColumn("S_LOCKTIME")
                  .AsFixedLengthAnsiString(10)
                  .Nullable()
              .AlterColumn("S_PROCDATE")
                  .AsFixedLengthAnsiString(9)
                  .Nullable()
              .AlterColumn("S_REPORTED")
                  .AsFixedLengthAnsiString(5)
                  .Nullable()
             .AlterColumn("CorrelationId")
                  .AsFixedLengthAnsiString(5)
                  .Nullable();

            Delete.Column("ConcurrencyToken")
                .FromTable(QueueDbName);
            Alter.Table(QueueDbName)
                .AddColumn("ConcurrencyToken")
                .AsCustom("Timestamp")
                .Nullable();


            Create.Index("queue_TAG_BATCH")
                .OnTable(QueueDbName)
                    .OnColumn("S_BATCH")
                        .Ascending()
                    .WithOptions()
                        .NonClustered();

            Create.Index("queue_TAG_LOCN")
                .OnTable(QueueDbName)
                    .OnColumn("S_LOCATION")
                        .Ascending()
                    .WithOptions()
                        .NonClustered();

            Create.Index("queue_TAG_PINDEX")
                .OnTable(QueueDbName)
                    .OnColumn("S_PINDEX")
                        .Ascending()
                    .WithOptions()
                        .NonClustered();

            #endregion

            #region Modify [dbo].[NabChqScanPod]
            Rename.Table(NabChqOldDbName).To(NabChqNewDbName);

            Alter.Table(NabChqNewDbName)
               .AlterColumn("S_DEL_IND ")
                   .AsFixedLengthAnsiString(5)
                   .Nullable()
               .AlterColumn("S_BATCH ")
                   .AsFixedLengthAnsiString(8)
                   .Nullable()
               .AlterColumn("S_MODIFIED ")
                   .AsFixedLengthAnsiString(5)
                   .Nullable()
               .AlterColumn("S_COMPLETE ")
                   .AsFixedLengthAnsiString(5)
                   .Nullable()
               .AlterColumn("S_TYPE ")
                   .AsFixedLengthAnsiString(5)
                   .Nullable()
               .AlterColumn("S_STATUS1 ")
                   .AsFixedLengthAnsiString(10)
                   .Nullable()
               .AlterColumn("S_STATUS2 ")
                   .AsFixedLengthAnsiString(10)
                   .Nullable()
               .AlterColumn("S_STATUS3 ")
                   .AsFixedLengthAnsiString(10)
                   .Nullable()
               .AlterColumn("S_STATUS4 ")
                   .AsFixedLengthAnsiString(10)
                   .Nullable()
               .AlterColumn("S_IMG1_OFF ")
                   .AsFixedLengthAnsiString(10)
                   .Nullable()
               .AlterColumn("S_IMG1_LEN ")
                   .AsFixedLengthAnsiString(10)
                   .Nullable()
               .AlterColumn("S_IMG1_TYP ")
                   .AsFixedLengthAnsiString(5)
                   .Nullable()
               .AlterColumn("S_IMG2_OFF ")
                   .AsFixedLengthAnsiString(10)
                   .Nullable()
               .AlterColumn("S_IMG2_LEN ")
                  .AsFixedLengthAnsiString(10)
                  .Nullable()
               .AlterColumn("S_IMG2_TYP ")
                   .AsFixedLengthAnsiString(5)
                   .Nullable()
               .AlterColumn("S_TRACE ")
                   .AsFixedLengthAnsiString(9)
                   .Nullable()
               .AlterColumn("S_LENGTH ")
                   .AsFixedLengthAnsiString(5)
                   .Nullable()
               .AlterColumn("S_SEQUENCE ")
                   .AsFixedLengthAnsiString(5)
                   .Nullable()
               .AlterColumn("S_BALANCE ")
                   .AsFixedLengthAnsiString(10)
                   .Nullable()
               .AlterColumn("S_REPROCESS ")
                   .AsFixedLengthAnsiString(5)
                   .Nullable()
               .AlterColumn("S_REPORTED ")
                   .AsFixedLengthAnsiString(5)
                   .Nullable()
               .AlterColumn("S_COMMITTED ")
                   .AsFixedLengthAnsiString(5)
                   .Nullable()
               .AlterColumn("batch ")
                   .AsFixedLengthAnsiString(8)
                   .Nullable()
               .AlterColumn("trace ")
                   .AsFixedLengthAnsiString(9)
                   .Nullable()
               .AlterColumn("sys_date ")
                   .AsFixedLengthAnsiString(8)
                   .Nullable()
               .AlterColumn("proc_date ")
                   .AsFixedLengthAnsiString(8)
                   .Nullable()
               .AlterColumn("ead ")
                   .AsFixedLengthAnsiString(11)
                   .Nullable()
               .AlterColumn("ser_num ")
                   .AsFixedLengthAnsiString(9)
                   .Nullable()
               .AlterColumn("bsb_num ")
                   .AsFixedLengthAnsiString(6)
                   .Nullable()
               .AlterColumn("acc_num ")
                   .AsFixedLengthAnsiString(21)
                   .Nullable()
               .AlterColumn("trancode ")
                   .AsFixedLengthAnsiString(3)
                   .Nullable()
               .AlterColumn("amount ")
                   .AsFixedLengthAnsiString(15)
                   .Nullable()
               .AlterColumn("pocket ")
                   .AsFixedLengthAnsiString(2)
                   .Nullable()
               .AlterColumn("payee_name ")
                   .AsFixedLengthAnsiString(240)
                   .Nullable()
               .AlterColumn("manual_repair ")
                   .AsFixedLengthAnsiString(5)
                   .Nullable()
               .AlterColumn("doc_type ")
                   .AsFixedLengthAnsiString(3)
                   .Nullable()
               .AlterColumn("rec_type_id ")
                   .AsFixedLengthAnsiString(4)
                   .Nullable()
               .AlterColumn("collecting_bank ")
                   .AsFixedLengthAnsiString(6)
                   .Nullable()
               .AlterColumn("unit_id ")
                   .AsFixedLengthAnsiString(3)
                   .Nullable()
               .AlterColumn("man_rep_ind ")
                   .AsFixedLengthAnsiString(1)
                   .Nullable()
               .AlterColumn("proof_seq ")
                   .AsFixedLengthAnsiString(12)
                   .Nullable()
               .AlterColumn("trans_seq ")
                   .AsFixedLengthAnsiString(5)
                   .Nullable()
               .AlterColumn("delay_ind ")
                   .AsFixedLengthAnsiString(1)
                   .Nullable()
               .AlterColumn("fv_exchange ")
                   .AsFixedLengthAnsiString(1)
                   .Nullable()
               .AlterColumn("adj_code ")
                   .AsFixedLengthAnsiString(2)
                   .Nullable()
               .AlterColumn("adj_desc ")
                   .AsFixedLengthAnsiString(30)
                   .Nullable()
               .AlterColumn("op_id ")
                   .AsFixedLengthAnsiString(15)
                   .Nullable()
               .AlterColumn("proc_time ")
                   .AsFixedLengthAnsiString(4)
                   .Nullable()
               .AlterColumn("override ")
                   .AsFixedLengthAnsiString(5)
                   .Nullable()
               .AlterColumn("fv_ind ")
                   .AsFixedLengthAnsiString(1)
                   .Nullable()
               .AlterColumn("host_trans_no ")
                   .AsFixedLengthAnsiString(3)
                   .Nullable()
               .AlterColumn("job_id ")
                   .AsFixedLengthAnsiString(15)
                   .Nullable()
               .AlterColumn("volume ")
                   .AsFixedLengthAnsiString(8)
                   .Nullable()
               .AlterColumn("img_location ")
                   .AsFixedLengthAnsiString(80)
                   .Nullable()
               .AlterColumn("img_front ")
                   .AsFixedLengthAnsiString(8)
                   .Nullable()
               .AlterColumn("img_rear ")
                   .AsFixedLengthAnsiString(8)
                   .Nullable()
               .AlterColumn("held_ind ")
                   .AsFixedLengthAnsiString(1)
                   .Nullable()
               .AlterColumn("receiving_bank ")
                   .AsFixedLengthAnsiString(3)
                   .Nullable()
               .AlterColumn("ie_transaction_id ")
                   .AsFixedLengthAnsiString(12)
                   .Nullable()
               .AlterColumn("batch_type ")
                   .AsFixedLengthAnsiString(6)
                   .Nullable()
               .AlterColumn("sub_batch_type ")
                   .AsFixedLengthAnsiString(6)
                   .Nullable()
               .AlterColumn("doc_ref_num ")
                   .AsFixedLengthAnsiString(9)
                   .Nullable()
               .AlterColumn("raw_micr ")
                   .AsFixedLengthAnsiString(64)
                   .Nullable()
               .AlterColumn("raw_ocr ")
                   .AsFixedLengthAnsiString(64)
                   .Nullable()
               .AlterColumn("processing_state ")
                   .AsFixedLengthAnsiString(3)
                   .Nullable()
               .AlterColumn("micr_flag ")
                   .AsFixedLengthAnsiString(1)
                   .Nullable()
               .AlterColumn("micr_unproc_flag ")
                   .AsFixedLengthAnsiString(1)
                   .Nullable()
               .AlterColumn("micr_suspect_fraud_flag ")
                   .AsFixedLengthAnsiString(1)
                   .Nullable();

            Rename.Column("S_DEL_IND")
                    .OnTable(NabChqNewDbName)
                    .To("S_DEL_IND");

            Rename.Column("S_BATCH")
                    .OnTable(NabChqNewDbName)
                    .To("S_BATCH");

            Rename.Column("S_MODIFIED")
                    .OnTable(NabChqNewDbName)
                    .To("S_MODIFIED");

            Rename.Column("S_COMPLETE")
                    .OnTable(NabChqNewDbName)
                    .To("S_COMPLETE");

            Rename.Column("S_TYPE")
                    .OnTable(NabChqNewDbName)
                    .To("S_TYPE");

            Rename.Column("S_STATUS1")
                    .OnTable(NabChqNewDbName)
                    .To("S_STATUS1");

            Rename.Column("S_STATUS2")
                    .OnTable(NabChqNewDbName)
                    .To("S_STATUS2");

            Rename.Column("S_STATUS3")
                    .OnTable(NabChqNewDbName)
                    .To("S_STATUS3");

            Rename.Column("S_STATUS4")
                    .OnTable(NabChqNewDbName)
                    .To("S_STATUS4");

            Rename.Column("S_IMG1_OFF")
                    .OnTable(NabChqNewDbName)
                    .To("S_IMG1_OFF");

            Rename.Column("S_IMG1_LEN")
                    .OnTable(NabChqNewDbName)
                    .To("S_IMG1_LEN");

            Rename.Column("S_IMG1_TYP")
                    .OnTable(NabChqNewDbName)
                    .To("S_IMG1_TYP");

            Rename.Column("S_IMG2_OFF")
                    .OnTable(NabChqNewDbName)
                    .To("S_IMG2_OFF");

            Rename.Column("S_IMG2_LEN")
                    .OnTable(NabChqNewDbName)
                    .To("S_IMG2_LEN");

            Rename.Column("S_IMG2_TYP")
                    .OnTable(NabChqNewDbName)
                    .To("S_IMG2_TYP");

            Rename.Column("S_TRACE")
                    .OnTable(NabChqNewDbName)
                    .To("S_TRACE");

            Rename.Column("S_LENGTH")
                    .OnTable(NabChqNewDbName)
                    .To("S_LENGTH");

            Rename.Column("S_SEQUENCE")
                    .OnTable(NabChqNewDbName)
                    .To("S_SEQUENCE");

            Rename.Column("S_BALANCE")
                    .OnTable(NabChqNewDbName)
                    .To("S_BALANCE");

            Rename.Column("S_REPROCESS")
                    .OnTable(NabChqNewDbName)
                    .To("S_REPROCESS");

            Rename.Column("S_REPORTED")
                    .OnTable(NabChqNewDbName)
                    .To("S_REPORTED");

            Rename.Column("S_COMMITTED")
                    .OnTable(NabChqNewDbName)
                    .To("S_COMMITTED");

            Rename.Column("batch")
                    .OnTable(NabChqNewDbName)
                    .To("batch");

            Rename.Column("trace")
                    .OnTable(NabChqNewDbName)
                    .To("trace");

            Rename.Column("sys_date")
                    .OnTable(NabChqNewDbName)
                    .To("sys_date");

            Rename.Column("proc_date")
                    .OnTable(NabChqNewDbName)
                    .To("proc_date");

            Rename.Column("ead")
                    .OnTable(NabChqNewDbName)
                    .To("ead");

            Rename.Column("ser_num")
                    .OnTable(NabChqNewDbName)
                    .To("ser_num");

            Rename.Column("bsb_num")
                    .OnTable(NabChqNewDbName)
                    .To("bsb_num");

            Rename.Column("acc_num")
                    .OnTable(NabChqNewDbName)
                    .To("acc_num");

            Rename.Column("trancode")
                    .OnTable(NabChqNewDbName)
                    .To("trancode");

            Rename.Column("amount")
                    .OnTable(NabChqNewDbName)
                    .To("amount");

            Rename.Column("pocket")
                    .OnTable(NabChqNewDbName)
                    .To("pocket");

            Rename.Column("payee_name")
                    .OnTable(NabChqNewDbName)
                    .To("payee_name");

            Rename.Column("payee_name")
                    .OnTable(NabChqNewDbName)
                    .To("payee_name");

            Rename.Column("manual_repair")
                    .OnTable(NabChqNewDbName)
                    .To("manual_repair");

            Rename.Column("doc_type")
                    .OnTable(NabChqNewDbName)
                    .To("doc_type");

            Rename.Column("rec_type_id")
                    .OnTable(NabChqNewDbName)
                    .To("rec_type_id");

            Rename.Column("collecting_bank")
                    .OnTable(NabChqNewDbName)
                    .To("collecting_bank");

            Rename.Column("unit_id")
                    .OnTable(NabChqNewDbName)
                    .To("unit_id");

            Rename.Column("man_rep_ind")
                    .OnTable(NabChqNewDbName)
                    .To("man_rep_ind");

            Rename.Column("proof_seq")
                    .OnTable(NabChqNewDbName)
                    .To("proof_seq");

            Rename.Column("trans_seq")
                    .OnTable(NabChqNewDbName)
                    .To("trans_seq");

            Rename.Column("delay_ind")
                    .OnTable(NabChqNewDbName)
                    .To("delay_ind");

            Rename.Column("fv_exchange")
                    .OnTable(NabChqNewDbName)
                    .To("fv_exchange");

            Rename.Column("adj_code")
                    .OnTable(NabChqNewDbName)
                    .To("adj_code");

            Rename.Column("adj_desc")
                    .OnTable(NabChqNewDbName)
                    .To("adj_desc");

            Rename.Column("op_id")
                    .OnTable(NabChqNewDbName)
                    .To("op_id");

            Rename.Column("proc_time")
                    .OnTable(NabChqNewDbName)
                    .To("proc_time");

            Rename.Column("override")
                    .OnTable(NabChqNewDbName)
                    .To("override");

            Rename.Column("fv_ind")
                    .OnTable(NabChqNewDbName)
                    .To("fv_ind");

            Rename.Column("host_trans_no")
                    .OnTable(NabChqNewDbName)
                    .To("host_trans_no");

            Rename.Column("job_id")
                    .OnTable(NabChqNewDbName)
                    .To("job_id");

            Rename.Column("volume")
                    .OnTable(NabChqNewDbName)
                    .To("volume");

            Rename.Column("img_location")
                    .OnTable(NabChqNewDbName)
                    .To("img_location");

            Rename.Column("img_front")
                    .OnTable(NabChqNewDbName)
                    .To("img_front");

            Rename.Column("img_rear")
                    .OnTable(NabChqNewDbName)
                    .To("img_rear");

            Rename.Column("held_ind")
                    .OnTable(NabChqNewDbName)
                    .To("held_ind");

            Rename.Column("receiving_bank")
                    .OnTable(NabChqNewDbName)
                    .To("receiving_bank");

            Rename.Column("ie_transaction_id")
                    .OnTable(NabChqNewDbName)
                    .To("ie_transaction_id");

            Rename.Column("batch_type")
                    .OnTable(NabChqNewDbName)
                    .To("batch_type");

            Rename.Column("sub_batch_type")
                    .OnTable(NabChqNewDbName)
                    .To("sub_batch_type");

            Rename.Column("doc_ref_num")
                    .OnTable(NabChqNewDbName)
                    .To("doc_ref_num");

            Rename.Column("raw_micr")
                    .OnTable(NabChqNewDbName)
                    .To("raw_micr");

            Rename.Column("raw_ocr")
                    .OnTable(NabChqNewDbName)
                    .To("raw_ocr");

            Rename.Column("processing_state")
                    .OnTable(NabChqNewDbName)
                    .To("processing_state");

            Rename.Column("micr_flag")
                    .OnTable(NabChqNewDbName)
                    .To("micr_flag");

            Rename.Column("micr_unproc_flag")
                    .OnTable(NabChqNewDbName)
                    .To("micr_unproc_flag");

            Rename.Column("micr_suspect_fraud_flag")
                    .OnTable(NabChqNewDbName)
                    .To("micr_suspect_fraud_flag");

            Create.Index("NabChq_DBTS")
                .OnTable(NabChqNewDbName)
                    .OnColumn("sys_date")
                        .Ascending()
                    .OnColumn("S_BATCH")
                        .Ascending()
                    .OnColumn("S_TRACE")
                        .Ascending()
                    .OnColumn("S_SEQUENCE")
                        .Ascending()
                    .WithOptions()
                        .NonClustered()
                    .WithOptions()
                        .Unique();

            Create.Index("NabChq_S")
                .OnTable(NabChqNewDbName)
                    .OnColumn("S_BATCH")
                        .Ascending()
                    .WithOptions()
                        .NonClustered();

            Create.Index("NabChq_TS")
                .OnTable(NabChqNewDbName)
                    .OnColumn("S_TRACE")
                        .Ascending()
                    .OnColumn("S_SEQUENCE")
                        .Ascending()
                    .WithOptions()
                        .NonClustered();
            
            
            
            #endregion

        }


        public override void Down()
        {

            #region Revert [dbo].[DB_INDEX]
            /* Revert modifications to Table [dbo].[DB_INDEX] */
            Alter.Table(DbIndexDbName)
              .AlterColumn("DEL_IND")
                  .AsString(5)
                  .Nullable()
              .AlterColumn("BATCH")
                  .AsString(8)
                  .Nullable()
              .AlterColumn("TRACE")
                  .AsString(9)
                  .Nullable()
              .AlterColumn("SEQUENCE")
                  .AsString(5)
                  .Nullable()
              .AlterColumn("TABLE_NO")
                  .AsString(5)
                  .Nullable()
              .AlterColumn("REC_NO")
                  .AsString(10)
                  .Nullable();

            Rename.Column("DEL_IND")
                    .OnTable(DbIndexDbName)
                    .To("DEL_IND ");

            Rename.Column("BATCH")
                .OnTable(DbIndexDbName)
                .To("BATCH ");

            Rename.Column("TRACE")
                .OnTable(DbIndexDbName)
                .To("TRACE ");

            Rename.Column("SEQUENCE")
                .OnTable(DbIndexDbName)
                .To("SEQUENCE ");

            Rename.Column("TABLE_NO")
                .OnTable(DbIndexDbName)
                .To("TABLE_NO ");

            Rename.Column("REC_NO")
                .OnTable(DbIndexDbName)
                .To("REC_NO ");

            Delete.Index("DB_INDEX_H")
                .OnTable(DbIndexDbName);
            #endregion

            #region Revert [dbo].[queue]
            Alter.Table(QueueDbName)
              .AlterColumn("S_LOCATION")
                  .AsString(33)
                  .Nullable()
              .AlterColumn("S_PINDEX")
                  .AsString(16)
                  .Nullable()
              .AlterColumn("S_LOCK")
                  .AsString(10)
                  .Nullable()
              .AlterColumn("S_CLIENT")
                  .AsString(80)
                  .Nullable()
              .AlterColumn("S_JOB_ID")
                  .AsString(128)
                  .Nullable()
              .AlterColumn("S_MODIFIED")
                  .AsString(5)
                  .Nullable()
              .AlterColumn("S_COMPLETE")
                  .AsString(5)
                  .Nullable()
              .AlterColumn("S_BATCH")
                  .AsString(8)
                  .Nullable()
              .AlterColumn("S_TRACE")
                  .AsString(9)
                  .Nullable()
              .AlterColumn("S_SDATE")
                  .AsString(8)
                  .Nullable()
              .AlterColumn("S_STIME")
                  .AsString(8)
                  .Nullable()
              .AlterColumn("S_UTIME")
                  .AsString(10)
                  .Nullable()
              .AlterColumn("S_PRIORITY")
                  .AsString(5)
                  .Nullable()
              .AlterColumn("S_IMG_PATH")
                  .AsString(80)
                  .Nullable()
              .AlterColumn("S_USERNAME")
                  .AsString(250)
                  .Nullable()
              .AlterColumn("S_SELNSTRING")
                  .AsString(128)
                  .Nullable()
              .AlterColumn("S_VERSION")
                  .AsString(32)
                  .Nullable()
              .AlterColumn("S_LOCKUSER")
                  .AsString(17)
                  .Nullable()
              .AlterColumn("S_LOCKMODULENAME")
                  .AsString(17)
                  .Nullable()
              .AlterColumn("S_LOCKUNITID")
                  .AsString(10)
                  .Nullable()
              .AlterColumn("S_LOCKMACHINENAME")
                  .AsString(17)
                  .Nullable()
              .AlterColumn("S_LOCKTIME")
                  .AsString(10)
                  .Nullable()
              .AlterColumn("S_PROCDATE")
                  .AsString(9)
                  .Nullable()
              .AlterColumn("S_REPORTED")
                  .AsString(5)
                  .Nullable()
              .AlterColumn("CorrelationId")
                  .AsString(5)
                  .Nullable();

            Delete.Column("ConcurrencyToken")
                .FromTable(QueueDbName);
            Alter.Table(QueueDbName)
                .AddColumn("ConcurrencyToken")
                .AsByte()
                .Nullable();

            Delete.Index("queue_TAG_BATCH")
                .OnTable(QueueDbName);
            Delete.Index("queue_TAG_LOCN")
                .OnTable(QueueDbName);
            Delete.Index("queue_TAG_PINDEX")
                .OnTable(QueueDbName);


            #endregion

            #region Revert [dbo].[NabChqScanPod]
            Rename.Table(NabChqNewDbName).To(NabChqOldDbName);

            Alter.Table(NabChqOldDbName)
               .AlterColumn("S_DEL_IND ")
                   .AsString(5)
                   .Nullable()
               .AlterColumn("S_BATCH ")
                   .AsString(8)
                   .Nullable()
               .AlterColumn("S_MODIFIED ")
                   .AsString(5)
                   .Nullable()
               .AlterColumn("S_COMPLETE ")
                   .AsString(5)
                   .Nullable()
               .AlterColumn("S_TYPE ")
                   .AsString(5)
                   .Nullable()
               .AlterColumn("S_STATUS1 ")
                   .AsString(10)
                   .Nullable()
               .AlterColumn("S_STATUS2 ")
                   .AsString(10)
                   .Nullable()
               .AlterColumn("S_STATUS3 ")
                   .AsString(10)
                   .Nullable()
               .AlterColumn("S_STATUS4 ")
                   .AsString(10)
                   .Nullable()
               .AlterColumn("S_IMG1_OFF ")
                   .AsString(10)
                   .Nullable()
               .AlterColumn("S_IMG1_LEN ")
                   .AsString(10)
                   .Nullable()
               .AlterColumn("S_IMG1_TYP ")
                   .AsString(5)
                   .Nullable()
               .AlterColumn("S_IMG2_OFF ")
                   .AsString(10)
                   .Nullable()
               .AlterColumn("S_IMG2_LEN ")
                  .AsString(10)
                  .Nullable()
               .AlterColumn("S_IMG2_TYP ")
                   .AsString(5)
                   .Nullable()
               .AlterColumn("S_TRACE ")
                   .AsString(9)
                   .Nullable()
               .AlterColumn("S_LENGTH ")
                   .AsString(5)
                   .Nullable()
               .AlterColumn("S_SEQUENCE ")
                   .AsString(5)
                   .Nullable()
               .AlterColumn("S_BALANCE ")
                   .AsString(10)
                   .Nullable()
               .AlterColumn("S_REPROCESS ")
                   .AsString(5)
                   .Nullable()
               .AlterColumn("S_REPORTED ")
                   .AsString(5)
                   .Nullable()
               .AlterColumn("S_COMMITTED ")
                   .AsString(5)
                   .Nullable()
               .AlterColumn("batch ")
                   .AsString(8)
                   .Nullable()
               .AlterColumn("trace ")
                   .AsString(9)
                   .Nullable()
               .AlterColumn("sys_date ")
                   .AsString(8)
                   .Nullable()
               .AlterColumn("proc_date ")
                   .AsString(8)
                   .Nullable()
               .AlterColumn("ead ")
                   .AsString(11)
                   .Nullable()
               .AlterColumn("ser_num ")
                   .AsString(9)
                   .Nullable()
               .AlterColumn("bsb_num ")
                   .AsString(6)
                   .Nullable()
               .AlterColumn("acc_num ")
                   .AsString(21)
                   .Nullable()
               .AlterColumn("trancode ")
                   .AsString(3)
                   .Nullable()
               .AlterColumn("amount ")
                   .AsString(15)
                   .Nullable()
               .AlterColumn("pocket ")
                   .AsString(2)
                   .Nullable()
               .AlterColumn("payee_name ")
                   .AsString(240)
                   .Nullable()
               .AlterColumn("manual_repair ")
                   .AsString(5)
                   .Nullable()
               .AlterColumn("doc_type ")
                   .AsString(3)
                   .Nullable()
               .AlterColumn("rec_type_id ")
                   .AsString(4)
                   .Nullable()
               .AlterColumn("collecting_bank ")
                   .AsString(6)
                   .Nullable()
               .AlterColumn("unit_id ")
                   .AsString(3)
                   .Nullable()
               .AlterColumn("man_rep_ind ")
                   .AsString(1)
                   .Nullable()
               .AlterColumn("proof_seq ")
                   .AsString(12)
                   .Nullable()
               .AlterColumn("trans_seq ")
                   .AsString(5)
                   .Nullable()
               .AlterColumn("delay_ind ")
                   .AsString(1)
                   .Nullable()
               .AlterColumn("fv_exchange ")
                   .AsString(1)
                   .Nullable()
               .AlterColumn("adj_code ")
                   .AsString(2)
                   .Nullable()
               .AlterColumn("adj_desc ")
                   .AsString(30)
                   .Nullable()
               .AlterColumn("op_id ")
                   .AsString(15)
                   .Nullable()
               .AlterColumn("proc_time ")
                   .AsString(4)
                   .Nullable()
               .AlterColumn("override ")
                   .AsString(5)
                   .Nullable()
               .AlterColumn("fv_ind ")
                   .AsString(1)
                   .Nullable()
               .AlterColumn("host_trans_no ")
                   .AsString(3)
                   .Nullable()
               .AlterColumn("job_id ")
                   .AsString(15)
                   .Nullable()
               .AlterColumn("volume ")
                   .AsString(8)
                   .Nullable()
               .AlterColumn("img_location ")
                   .AsString(80)
                   .Nullable()
               .AlterColumn("img_front ")
                   .AsString(5)
                   .Nullable()
               .AlterColumn("img_rear ")
                   .AsString(8)
                   .Nullable()
               .AlterColumn("held_ind ")
                   .AsString(1)
                   .Nullable()
               .AlterColumn("receiving_bank ")
                   .AsString(3)
                   .Nullable()
               .AlterColumn("ie_transaction_id ")
                   .AsString(12)
                   .Nullable()
               .AlterColumn("batch_type ")
                   .AsString(6)
                   .Nullable()
               .AlterColumn("sub_batch_type ")
                   .AsString(6)
                   .Nullable()
               .AlterColumn("doc_ref_num ")
                   .AsString(6)
                   .Nullable()
               .AlterColumn("raw_micr ")
                   .AsString(9)
                   .Nullable()
               .AlterColumn("raw_ocr ")
                   .AsString(64)
                   .Nullable()
               .AlterColumn("processing_state ")
                   .AsString(3)
                   .Nullable()
               .AlterColumn("micr_flag ")
                   .AsString(1)
                   .Nullable()
               .AlterColumn("micr_unproc_flag ")
                   .AsString(1)
                   .Nullable()
               .AlterColumn("micr_suspect_fraud_flag ")
                   .AsString(1)
                   .Nullable();

            Rename.Column("S_DEL_IND")
                   .OnTable(NabChqOldDbName)
                   .To("S_DEL_IND ");

            Rename.Column("S_BATCH")
                    .OnTable(NabChqOldDbName)
                    .To("S_BATCH ");

            Rename.Column("S_MODIFIED")
                    .OnTable(NabChqOldDbName)
                    .To("S_MODIFIED ");

            Rename.Column("S_COMPLETE")
                    .OnTable(NabChqOldDbName)
                    .To("S_COMPLETE ");

            Rename.Column("S_TYPE")
                    .OnTable(NabChqOldDbName)
                    .To("S_TYPE ");

            Rename.Column("S_STATUS1")
                    .OnTable(NabChqOldDbName)
                    .To("S_STATUS1 ");

            Rename.Column("S_STATUS2")
                    .OnTable(NabChqOldDbName)
                    .To("S_STATUS2 ");

            Rename.Column("S_STATUS3")
                    .OnTable(NabChqOldDbName)
                    .To("S_STATUS3 ");

            Rename.Column("S_STATUS4")
                    .OnTable(NabChqOldDbName)
                    .To("S_STATUS4 ");

            Rename.Column("S_IMG1_OFF")
                    .OnTable(NabChqOldDbName)
                    .To("S_IMG1_OFF ");

            Rename.Column("S_IMG1_LEN")
                    .OnTable(NabChqOldDbName)
                    .To("S_IMG1_LEN ");

            Rename.Column("S_IMG1_TYP")
                    .OnTable(NabChqOldDbName)
                    .To("S_IMG1_TYP ");

            Rename.Column("S_IMG2_OFF")
                    .OnTable(NabChqOldDbName)
                    .To("S_IMG2_OFF ");

            Rename.Column("S_IMG2_LEN")
                    .OnTable(NabChqOldDbName)
                    .To("S_IMG2_LEN ");

            Rename.Column("S_IMG2_TYP")
                    .OnTable(NabChqOldDbName)
                    .To("S_IMG2_TYP ");

            Rename.Column("S_TRACE")
                    .OnTable(NabChqOldDbName)
                    .To("S_TRACE ");

            Rename.Column("S_LENGTH")
                    .OnTable(NabChqOldDbName)
                    .To("S_LENGTH ");

            Rename.Column("S_SEQUENCE")
                    .OnTable(NabChqOldDbName)
                    .To("S_SEQUENCE ");

            Rename.Column("S_BALANCE")
                    .OnTable(NabChqOldDbName)
                    .To("S_BALANCE ");

            Rename.Column("S_REPROCESS")
                    .OnTable(NabChqOldDbName)
                    .To("S_REPROCESS ");

            Rename.Column("S_REPORTED")
                    .OnTable(NabChqOldDbName)
                    .To("S_REPORTED ");

            Rename.Column("S_COMMITTED")
                    .OnTable(NabChqOldDbName)
                    .To("S_COMMITTED ");

            Rename.Column("batch")
                    .OnTable(NabChqOldDbName)
                    .To("batch ");

            Rename.Column("trace")
                    .OnTable(NabChqOldDbName)
                    .To("trace ");

            Rename.Column("sys_date")
                    .OnTable(NabChqOldDbName)
                    .To("sys_date ");

            Rename.Column("proc_date")
                    .OnTable(NabChqOldDbName)
                    .To("proc_date ");

            Rename.Column("ead")
                    .OnTable(NabChqOldDbName)
                    .To("ead ");

            Rename.Column("ser_num")
                    .OnTable(NabChqOldDbName)
                    .To("ser_num ");

            Rename.Column("bsb_num")
                    .OnTable(NabChqOldDbName)
                    .To("bsb_num ");

            Rename.Column("acc_num")
                    .OnTable(NabChqOldDbName)
                    .To("acc_num ");

            Rename.Column("trancode")
                    .OnTable(NabChqOldDbName)
                    .To("trancode ");

            Rename.Column("amount")
                    .OnTable(NabChqOldDbName)
                    .To("amount ");

            Rename.Column("pocket")
                    .OnTable(NabChqOldDbName)
                    .To("pocket ");

            Rename.Column("payee_name")
                    .OnTable(NabChqOldDbName)
                    .To("payee_name ");

            Rename.Column("payee_name")
                    .OnTable(NabChqOldDbName)
                    .To("payee_name ");

            Rename.Column("manual_repair")
                    .OnTable(NabChqOldDbName)
                    .To("manual_repair ");

            Rename.Column("doc_type")
                    .OnTable(NabChqOldDbName)
                    .To("doc_type ");

            Rename.Column("rec_type_id")
                    .OnTable(NabChqOldDbName)
                    .To("rec_type_id ");

            Rename.Column("collecting_bank")
                    .OnTable(NabChqOldDbName)
                    .To("collecting_bank ");

            Rename.Column("unit_id")
                    .OnTable(NabChqOldDbName)
                    .To("unit_id ");

            Rename.Column("man_rep_ind")
                    .OnTable(NabChqOldDbName)
                    .To("man_rep_ind ");

            Rename.Column("proof_seq")
                    .OnTable(NabChqOldDbName)
                    .To("proof_seq ");

            Rename.Column("trans_seq")
                    .OnTable(NabChqOldDbName)
                    .To("trans_seq ");

            Rename.Column("delay_ind")
                    .OnTable(NabChqOldDbName)
                    .To("delay_ind ");

            Rename.Column("fv_exchange")
                    .OnTable(NabChqOldDbName)
                    .To("fv_exchange ");

            Rename.Column("adj_code")
                    .OnTable(NabChqOldDbName)
                    .To("adj_code ");

            Rename.Column("adj_desc")
                    .OnTable(NabChqOldDbName)
                    .To("adj_desc ");

            Rename.Column("op_id")
                    .OnTable(NabChqOldDbName)
                    .To("op_id ");

            Rename.Column("proc_time")
                    .OnTable(NabChqOldDbName)
                    .To("proc_time ");

            Rename.Column("override")
                    .OnTable(NabChqOldDbName)
                    .To("override ");

            Rename.Column("fv_ind")
                    .OnTable(NabChqOldDbName)
                    .To("fv_ind ");

            Rename.Column("host_trans_no")
                    .OnTable(NabChqOldDbName)
                    .To("host_trans_no ");

            Rename.Column("job_id")
                    .OnTable(NabChqOldDbName)
                    .To("job_id ");

            Rename.Column("volume")
                    .OnTable(NabChqOldDbName)
                    .To("volume ");

            Rename.Column("img_location")
                    .OnTable(NabChqOldDbName)
                    .To("img_location ");

            Rename.Column("img_front")
                    .OnTable(NabChqOldDbName)
                    .To("img_front ");

            Rename.Column("img_rear")
                    .OnTable(NabChqOldDbName)
                    .To("img_rear ");

            Rename.Column("held_ind")
                    .OnTable(NabChqOldDbName)
                    .To("held_ind ");

            Rename.Column("receiving_bank")
                    .OnTable(NabChqOldDbName)
                    .To("receiving_bank ");

            Rename.Column("ie_transaction_id")
                    .OnTable(NabChqOldDbName)
                    .To("ie_transaction_id ");

            Rename.Column("batch_type")
                    .OnTable(NabChqOldDbName)
                    .To("batch_type ");

            Rename.Column("sub_batch_type")
                    .OnTable(NabChqOldDbName)
                    .To("sub_batch_type ");

            Rename.Column("doc_ref_num")
                    .OnTable(NabChqOldDbName)
                    .To("doc_ref_num ");

            Rename.Column("raw_micr")
                    .OnTable(NabChqOldDbName)
                    .To("raw_micr ");

            Rename.Column("raw_ocr")
                    .OnTable(NabChqOldDbName)
                    .To("raw_ocr ");

            Rename.Column("processing_state")
                    .OnTable(NabChqOldDbName)
                    .To("processing_state ");

            Rename.Column("micr_flag")
                    .OnTable(NabChqOldDbName)
                    .To("micr_flag ");

            Rename.Column("micr_unproc_flag")
                    .OnTable(NabChqOldDbName)
                    .To("micr_unproc_flag ");

            Rename.Column("micr_suspect_fraud_flag")
                    .OnTable(NabChqOldDbName)
                    .To("micr_suspect_fraud_flag ");

            Delete.Index("NabChq_DBTS")
                .OnTable(NabChqOldDbName);
            Delete.Index("NabChq_S")
                .OnTable(NabChqOldDbName);
            Delete.Index("NabChq_TS")
                .OnTable(NabChqOldDbName);
            #endregion
        }
    }
}
