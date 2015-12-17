using System;
using System.Data.Entity;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;

namespace Lombard.Scheduler.Models
{


    public partial class Metadata : DbContext
    {
        public Metadata()
            : base("name=NonProcessingDay")
        {
        }

        public virtual DbSet<ref_metadata> ref_metadata { get; set; }

        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {
        }
    }
}
