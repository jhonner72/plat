using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    [Migration(20151202140000)]
    public class _20151202140000_DRNSequenceDipsAdapter : Migration
    {
        public override void Up()
        {
            Create.Sequence("DipsAdapterDRNSequence").MinValue(1).MaxValue(999999).IncrementBy(1).StartWith(1).Cycle();
        }

        public override void Down()
        {
            Delete.Sequence("DipsAdapterDRNSequence");
        }
    }
}