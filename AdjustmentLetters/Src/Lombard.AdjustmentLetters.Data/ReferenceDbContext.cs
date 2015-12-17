using System;
using System.Collections.Generic;
using System.Linq;
using System.Data.Common;
using System.Data.Entity;
using Lombard.AdjustmentLetters.Data.Domain;

namespace Lombard.AdjustmentLetters.Data
{
    public interface IReferenceDbContext : IDisposable
    {
        List<Branch> GetAllBranches();
    }

    public class ReferenceDbContext : DbContext, IReferenceDbContext
    {
        public ReferenceDbContext(DbConnection dbConnection)
            : base(dbConnection, true)
        {
            Database.SetInitializer<ReferenceDbContext>(null);

        }

        public DbSet<Branch> Branches { get; set; }

        public List<Branch> GetAllBranches()
        {
            return Branches.ToList();
        }

        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {
            // Rename a column so that not all property/column names match
            modelBuilder.Entity<Branch>()
                .HasKey(a => a.branch_bsb)
                .Map(x => x.ToTable("ref_branch"));

            base.OnModelCreating(modelBuilder);
        }
    }
}
