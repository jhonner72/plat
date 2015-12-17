﻿using FluentMigrator;

namespace Lombard.Dips.Data.Migrations
{
    [Migration(20150401140403)]
    public class CreateNabChqScanPodTable : Migration
    {
        private string dbName = "NabChqScanPod";
        public override void Up()
        {
            Create.Table(dbName)
                .WithColumn("S_DEL_IND ")
                    .AsString(5)
                    .Nullable()
                .WithColumn("S_BATCH ")
                    .AsString(8)
                    .Nullable()
                .WithColumn("S_MODIFIED ")
                    .AsString(5)
                    .Nullable()
                .WithColumn("S_COMPLETE ")
                    .AsString(5)
                    .Nullable()
                .WithColumn("S_TYPE ")
                    .AsString(5)
                    .Nullable()
                .WithColumn("S_STATUS1 ")
                    .AsString(10)
                    .Nullable()
                .WithColumn("S_STATUS2 ")
                    .AsString(10)
                    .Nullable()
                .WithColumn("S_STATUS3 ")
                    .AsString(10)
                    .Nullable()
                .WithColumn("S_STATUS4 ")
                    .AsString(10)
                    .Nullable()
                .WithColumn("S_IMG1_OFF ")
                    .AsString(10)
                    .Nullable()
                .WithColumn("S_IMG1_LEN ")
                    .AsString(10)
                    .Nullable()
                .WithColumn("S_IMG1_TYP ")
                    .AsString(5)
                    .Nullable()
                .WithColumn("S_IMG2_OFF ")
                    .AsString(10)
                    .Nullable()
                .WithColumn("S_IMG2_LEN ")
                   .AsString(10)
                   .Nullable()
                .WithColumn("S_IMG2_TYP ")
                    .AsString(5)
                    .Nullable()
                .WithColumn("S_TRACE ")
                    .AsString(9)
                    .Nullable()
                .WithColumn("S_LENGTH ")
                    .AsString(5)
                    .Nullable()
                .WithColumn("S_SEQUENCE ")
                    .AsString(5)
                    .Nullable()
                .WithColumn("S_BALANCE ")
                    .AsString(10)
                    .Nullable()
                .WithColumn("S_REPROCESS ")
                    .AsString(5)
                    .Nullable()
                .WithColumn("S_REPORTED ")
                    .AsString(5)
                    .Nullable()
                .WithColumn("S_COMMITTED ")
                    .AsString(5)
                    .Nullable()
                .WithColumn("batch ")
                    .AsString(8)
                    .Nullable()
                .WithColumn("trace ")
                    .AsString(9)
                    .Nullable()
                .WithColumn("sys_date ")
                    .AsString(8)
                    .Nullable()
                .WithColumn("proc_date ")
                    .AsString(8)
                    .Nullable()
                .WithColumn("ead ")
                    .AsString(11)
                    .Nullable()
                .WithColumn("ser_num ")
                    .AsString(9)
                    .Nullable()
                .WithColumn("bsb_num ")
                    .AsString(6)
                    .Nullable()
                .WithColumn("acc_num ")
                    .AsString(21)
                    .Nullable()
                .WithColumn("trancode ")
                    .AsString(3)
                    .Nullable()
                .WithColumn("amount ")
                    .AsString(15)
                    .Nullable()
                .WithColumn("pocket ")
                    .AsString(2)
                    .Nullable()
                .WithColumn("payee_name ")
                    .AsString(240)
                    .Nullable()
                .WithColumn("manual_repair ")
                    .AsString(5)
                    .Nullable()
                .WithColumn("doc_type ")
                    .AsString(3)
                    .Nullable()
                .WithColumn("rec_type_id ")
                    .AsString(4)
                    .Nullable()
                .WithColumn("collecting_bank ")
                    .AsString(6)
                    .Nullable()
                .WithColumn("unit_id ")
                    .AsString(3)
                    .Nullable()
                .WithColumn("man_rep_ind ")
                    .AsString(1)
                    .Nullable()
                .WithColumn("proof_seq ")
                    .AsString(12)
                    .Nullable()
                .WithColumn("trans_seq ")
                    .AsString(5)
                    .Nullable()
                .WithColumn("delay_ind ")
                    .AsString(1)
                    .Nullable()
                .WithColumn("fv_exchange ")
                    .AsString(1)
                    .Nullable()
                .WithColumn("adj_code ")
                    .AsString(2)
                    .Nullable()
                .WithColumn("adj_desc ")
                    .AsString(30)
                    .Nullable()
                .WithColumn("op_id ")
                    .AsString(15)
                    .Nullable()
                .WithColumn("proc_time ")
                    .AsString(4)
                    .Nullable()
                .WithColumn("override ")
                    .AsString(5)
                    .Nullable()
                .WithColumn("fv_ind ")
                    .AsString(1)
                    .Nullable()
                .WithColumn("host_trans_no ")
                    .AsString(3)
                    .Nullable()
                .WithColumn("job_id ")
                    .AsString(15)
                    .Nullable()
                .WithColumn("volume ")
                    .AsString(8)
                    .Nullable()
                .WithColumn("img_location ")
                    .AsString(80)
                    .Nullable()
                .WithColumn("img_front ")
                    .AsString(5)
                    .Nullable()
                .WithColumn("img_rear ")
                    .AsString(8)
                    .Nullable()
                .WithColumn("held_ind ")
                    .AsString(1)
                    .Nullable()
                .WithColumn("receiving_bank ")
                    .AsString(3)
                    .Nullable()
                .WithColumn("ie_transaction_id ")
                    .AsString(12)
                    .Nullable()
                .WithColumn("batch_type ")
                    .AsString(6)
                    .Nullable()
                .WithColumn("sub_batch_type ")
                    .AsString(6)
                    .Nullable()
                .WithColumn("doc_ref_num ")
                    .AsString(6)
                    .Nullable()
                .WithColumn("raw_micr ")
                    .AsString(9)
                    .Nullable()
                .WithColumn("raw_ocr ")
                    .AsString(64)
                    .Nullable()
                .WithColumn("processing_state ")
                    .AsString(3)
                    .Nullable()
                .WithColumn("micr_flag ")
                    .AsString(1)
                    .Nullable()
                .WithColumn("micr_unproc_flag ")
                    .AsString(1)
                    .Nullable()
                .WithColumn("micr_suspect_fraud_flag ")
                    .AsString(1)
                    .Nullable();



        }
        public override void Down()
        {
            Delete.Table(dbName);
        }
    }
}
