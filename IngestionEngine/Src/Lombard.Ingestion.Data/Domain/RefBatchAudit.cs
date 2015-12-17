using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace Lombard.Ingestion.Data.Domain
{
    [Table("ref_batch_audit")]
    public class RefBatchAudit
    {
        [Key, Required, Column("file_id")]
        public int FileId { get; set; }

        [Required, Column("machine_number"), StringLength(3)]
        public string MachineNumber { get; set; }

        [Required, Column("file_timestamp")]
        public DateTime FileTimeStamp { get; set; }

        [Required, Column("file_name"), StringLength(255)]
        public string Filename { get; set; }

        [Required, Column("batch_number"), StringLength(40)]
        public string BatchNumber { get; set; }

        [Required, Column("processing_date")]
        public DateTime ProcessingDate { get; set; }

        [Required, Column("work_type"), StringLength(50)]
        public string WorkType { get; set; }

        [Required, Column("record_count")]
        public int RecordCount { get; set; }

        [Required, Column("first_DRN"), StringLength(16)]
        public string FirstDrn { get; set; }

        [Required, Column("last_DRN"), StringLength(16)]
        public string LastDrn { get; set; }
    }
}
