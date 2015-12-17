using System;
using System.Data.Common;
using System.Data.Entity;
using System.Data.Entity.Infrastructure;
using System.Data.Entity.Validation;
using System.Threading;
using System.Threading.Tasks;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.Data.Transaction;
using Serilog;
using System.Linq;

namespace Lombard.Adapters.Data
{
    public interface IDipsDbContext : IDisposable
    {
        IDbSet<DipsQueue> Queues { get; set; }
        IDbSet<DipsNabChq> NabChqPods { get; set; }
        IDbSet<DipsDbIndex> DbIndexes { get; set; }

        Int64 GetNextFromBatchNumberSequence();
        Int64 GetNextFromDrnSequence();

        IDbSet<DipsRequest> DipsRequest { get; set; }
        IDbSet<DipsResponseData> DipsResponseData { get; set; }
        IDbSet<DipsResponseDone> DipsResponseDone { get; set; }

        IDipsDbContextTransaction BeginTransaction();
        Task<int> SaveChangesAsync(CancellationToken token);
        int SaveChanges();

        DbEntityEntry<TEntity> Entry<TEntity>(TEntity entity) where TEntity : class;
    }

    [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1063:ImplementIDisposableCorrectly", Justification = "IDisposable is implemented in base class (DbContext)")]
    public class DipsDbContext : DbContext, IDipsDbContext
    {
        public DipsDbContext(DbConnection dbConnection) :
            base(dbConnection, true)
        {
            Database.Log += message => Log.Verbose(message);
            //Log.Debug("Lazy loading on the DipsDbContext is disabled");
            Configuration.LazyLoadingEnabled = false;
        }

        public virtual IDbSet<DipsQueue> Queues { get; set; }
        public virtual IDbSet<DipsNabChq> NabChqPods { get; set; }
        public virtual IDbSet<DipsDbIndex> DbIndexes { get; set; }
        public virtual IDbSet<DipsRequest> DipsRequest { get; set; }
        public virtual IDbSet<DipsResponseData> DipsResponseData { get; set; }
        public virtual IDbSet<DipsResponseDone> DipsResponseDone { get; set; }

        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {
            modelBuilder.Configurations.AddFromAssembly(typeof(DipsDbContext).Assembly);
            base.OnModelCreating(modelBuilder);
        }

        public IDipsDbContextTransaction BeginTransaction()
        {
            return new DipsDbContextTransaction(Database.BeginTransaction());
        }

        public async override Task<int> SaveChangesAsync(CancellationToken token)
        {
            var dbObjects = 0;
            try
            {
                dbObjects = await base.SaveChangesAsync(token);
                return dbObjects;
            }
            catch (DbEntityValidationException ex)
            {

                Log.Error(ex, "DbEntityValidationException Error: " + ex.Message);
                foreach (var e in ex.EntityValidationErrors)
                {
                    Log.Error("Entity of type \"{0}\" in state \"{1}\" has the following validation errors:",
                        e.Entry.Entity.GetType().Name,
                        e.Entry.State);
                    foreach (var ve in e.ValidationErrors)
                    {
                        Log.Error("- Property: \"{0}\", Error: \"{1}\"",
                            ve.PropertyName,
                            ve.ErrorMessage);
                    }
                }
            }
            catch (Exception ex)
            {
                Log.Error("An error occurred in SaveChangesAsync(): " + ex.Message);
                if (ex.InnerException != null)
                {
                    Log.Error("The inner exception is: " + ex.InnerException.Message);

                    if (ex.InnerException.InnerException != null)
                    {
                        Log.Error("The inner x2 exception is: " + ex.InnerException.InnerException.Message);

                        if (ex.InnerException.InnerException.InnerException != null)
                        {
                            Log.Error("The inner x3 exception is: " + ex.InnerException.InnerException.InnerException.Message);

                            if (ex.InnerException.InnerException.InnerException.InnerException != null)
                            {
                                Log.Error("The inner x4 exception is: " + ex.InnerException.InnerException.InnerException.InnerException.Message);

                                if (ex.InnerException.InnerException.InnerException.InnerException.InnerException != null)
                                {
                                    Log.Error("The inner x5 exception is: " + ex.InnerException.InnerException.InnerException.InnerException.InnerException.Message);
                                }
                            }
                        }
                    }

                }

            }

            return dbObjects;
        }

        public override int SaveChanges()
        {
            var dbObjects = 0;
            try
            {
                dbObjects = base.SaveChanges();
                return dbObjects;
            }
            catch (DbEntityValidationException ex)
            {

                Log.Error(ex, "DbEntityValidationException Error: " + ex.Message);
                foreach (var e in ex.EntityValidationErrors)
                {
                    Log.Error("Entity of type \"{0}\" in state \"{1}\" has the following validation errors:",
                        e.Entry.Entity.GetType().Name,
                        e.Entry.State);
                    foreach (var ve in e.ValidationErrors)
                    {
                        Log.Error("- Property: \"{0}\", Error: \"{1}\"",
                            ve.PropertyName,
                            ve.ErrorMessage);
                    }
                }
            }
            catch (Exception ex)
            {
                Log.Error("An error occurred in SaveChanges(): " + ex.Message);
                if (ex.InnerException != null)
                {
                    Log.Error("The inner exception is: " + ex.InnerException.Message);

                    if (ex.InnerException.InnerException != null)
                    {
                        Log.Error("The inner x2 exception is: " + ex.InnerException.InnerException.Message);

                        if (ex.InnerException.InnerException.InnerException != null)
                        {
                            Log.Error("The inner x3 exception is: " + ex.InnerException.InnerException.InnerException.Message);

                            if (ex.InnerException.InnerException.InnerException.InnerException != null)
                            {
                                Log.Error("The inner x4 exception is: " + ex.InnerException.InnerException.InnerException.InnerException.Message);

                                if (ex.InnerException.InnerException.InnerException.InnerException.InnerException != null)
                                {
                                    Log.Error("The inner x5 exception is: " + ex.InnerException.InnerException.InnerException.InnerException.InnerException.Message);
                                }
                            }
                        }
                    }

                }

            }

            return dbObjects;
        }

        public Int64 GetNextFromBatchNumberSequence()
        {
            return this.Database.SqlQuery<System.Int64>("SELECT NEXT VALUE FOR DipsAdapterBatchnoSequence").FirstOrDefault();
        }

        public Int64 GetNextFromDrnSequence()
        {
            return this.Database.SqlQuery<System.Int64>("SELECT NEXT VALUE FOR DipsAdapterDRNSequence").FirstOrDefault();
        }
    }
}