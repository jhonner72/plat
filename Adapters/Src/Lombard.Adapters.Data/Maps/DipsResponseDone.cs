using System.Data.Entity.ModelConfiguration;
using Lombard.Adapters.Data.Domain;

namespace Lombard.Adapters.Data.Maps
{
      
    public class DipsResponseDoneMap : EntityTypeConfiguration<DipsResponseDone>
    {
        public DipsResponseDoneMap()
        {
            ToTable("response_done")
                .HasKey(x => x.guid_name);
        }
    }
}
