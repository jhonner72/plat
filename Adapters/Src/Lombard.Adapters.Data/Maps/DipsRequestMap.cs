using System.Data.Entity.ModelConfiguration;
using Lombard.Adapters.Data.Domain;

namespace Lombard.Adapters.Data.Maps
{
      
    public class DipsRequestMap : EntityTypeConfiguration<DipsRequest>
    {
        public DipsRequestMap()
        {
            ToTable("request")
                .HasKey(x => x.guid_name);
        }
    }
}
