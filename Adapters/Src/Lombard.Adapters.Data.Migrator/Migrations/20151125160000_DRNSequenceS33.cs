using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    [Migration(20151125160000)]
    public class _20151125160000_DRNSequenceS33 : Migration
    {
        public override void Up()
        {
            Create.Sequence("DRNSequence").MinValue(1).MaxValue(999999).IncrementBy(1).StartWith(1).Cycle();
        }

        public override void Down()
        {
            Delete.Sequence("DRNSequence");
        }
    }
}