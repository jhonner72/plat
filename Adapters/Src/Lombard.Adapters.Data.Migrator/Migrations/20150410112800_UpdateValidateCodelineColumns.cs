using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    [Migration(20150410112800)]
    public class UpdateValidateCodelineColumns : Migration
    {
        public override void Up()
        {
            Rename.Column("ValidateCodelineCompleted")
                .OnTable("queue")
                .To("ResponseCompleted");

            Rename.Column("ValidateCodelineCorrelationId")
                .OnTable("queue")
                .To("CorrelationId");

        }

        public override void Down()
        {
            Rename.Column("ResponseCompleted")
                .OnTable("queue")
                .To("ValidateCodelineCompleted");

            Rename.Column("CorrelationId")
                .OnTable("queue")
                .To("ValidateCodelineCorrelationId");
        }
    }
}
