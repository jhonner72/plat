namespace Lombard.ECLMatchingEngine.Service.Data
{
    using System;
    using System.Collections.Generic;
    using System.Data.Entity;
    using System.ComponentModel.DataAnnotations.Schema;
    using System.Linq;

    public interface IECLInfoDataEntityFrameworks : IDisposable
    {
        List<ref_aus_post_ecl_data> GetRecordInfo{get;}
    }

    public partial class ECLRecordDbContext : DbContext, IECLInfoDataEntityFrameworks
    {
        public ECLRecordDbContext()
            : base("name=ECLRecordDbContext")
        {
        }

        public virtual DbSet<ref_aus_post_ecl_data> ref_aus_post_ecl_data { get; set; }

        public List<ref_aus_post_ecl_data> GetRecordInfo
        {
            get
            {
                List<ref_aus_post_ecl_data> ECLRecordData = this.ref_aus_post_ecl_data.Where(a => a.record_type == "D").ToList<ref_aus_post_ecl_data>();

                return ECLRecordData;

            }
        } 
        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {
        }
    }
}
