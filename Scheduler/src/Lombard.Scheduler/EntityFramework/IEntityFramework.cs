using System;
using System.Collections.Generic;
using System.Linq;
using System.Data.Entity;
using Lombard.Scheduler.Models;
using Lombard.Vif.Service.Messages.XsdImports;

namespace Lombard.Scheduler.EntityFramework
{
    public interface IEntityFramework
    {
        ref_metadata GetMetadataByName(string name);
        List<ref_metadata> GetBusinessCalendar { get; }
        void Dispose();
    }
    public class ProcessingDay : IEntityFramework
    {
        private Metadata dbContext = new Metadata();
        private List<ref_metadata> metadata = null;

        public ProcessingDay() 
        {

        }

        public List<ref_metadata> GetBusinessCalendar
        {
            get
            {
                metadata = (from row in dbContext.ref_metadata
                                     where row.ref_name == "BusinessCalendar"
                                     select row).ToList();

                return this.metadata;
            }
        }

        public void Dispose()
        {
            dbContext.Dispose();
        }

        public ref_metadata GetMetadataByName(string name)
        {
            return dbContext.ref_metadata.Single(m => m.ref_name == name);
        }
    }
}