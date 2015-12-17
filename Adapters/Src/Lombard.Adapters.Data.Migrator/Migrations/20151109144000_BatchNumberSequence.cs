using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    [Migration(20151109144000)]
    public class _20151109144000_BatchNumberSequence:Migration
    {
        public override void Up()
        {
            Create.Sequence("DipsAdapterBatchnoSequence").MinValue(1).MaxValue(99999).IncrementBy(1).StartWith(1).Cycle();
        }

        public override void Down()
        {
            Delete.Sequence("DipsAdapterBatchnoSequence");
        }
    }
}