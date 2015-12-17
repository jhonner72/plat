namespace Lombard.ECLMatchingEngine.Service.Data
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.DataAnnotations;
    using System.ComponentModel.DataAnnotations.Schema;
    using System.Data.Entity.Spatial;

    public partial class ref_aus_post_ecl_data
    {
        [Key]
        [Column(Order = 0)]
        [StringLength(3)]
        public string file_state { get; set; }

        [Key]
        [Column(Order = 1)]
        public DateTime file_date { get; set; }

        [Key]
        [Column(Order = 2)]
        [DatabaseGenerated(DatabaseGeneratedOption.None)]
        public int record_sequence { get; set; }

        [Key]
        [Column(Order = 3)]
        [StringLength(112)]
        public string record_content { get; set; }

        [Key]
        [Column(Order = 4)]
        [StringLength(1)]
        public string record_type { get; set; }
    }
}
