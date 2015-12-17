using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Data.Entity.Spatial;

namespace Lombard.Scheduler.Models
{


    public partial class ref_metadata
    {
        [Key]
        public int ref_id { get; set; }

        [Required]
        [StringLength(50)]
        public string ref_name { get; set; }

        [Required]
        public string ref_value { get; set; }

        public DateTime modified_date { get; set; }

        [Required]
        [StringLength(50)]
        public string modified_by { get; set; }
    }
}
