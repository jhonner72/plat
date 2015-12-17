using FluentMigrator;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lombard.Data.Tracking.Migrator.Migrations
{
    [Migration(20150902000001)]
    public class CreateRefCreditCardTable : Migration
    {
        public override void Up()
        {
            Create.Table("ref_credit_card")
                .WithColumn("card_prefix").AsString(6).NotNullable().PrimaryKey("PK_ref_credit_card")
                .WithColumn("card_type").AsString(50).NotNullable();

            SeedData();
        }

        private void SeedData()
        {
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('377872','American Express');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('377873','American Express');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('377874','American Express');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('377876','American Express');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('377877','American Express');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('377878','American Express');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('401795','Visa Classic Card');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('430327','NAB Card');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('430328','NAB Card');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('430329','NAB Card');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('430330','NAB Card');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('433687','NAB Card');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('455701','NAB Card');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('455702','NAB Card');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('455703','NAB Card');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('455704','NAB Card');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('471527','NAB Card');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('490292','Visa Gold Card');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('490289','Visa Classic Card');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('531355','NAB Card');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('531356','NAB Card');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('531357','NAB Card');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('531358','NAB Card');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('531359','NAB Card');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('552061','NAB Card');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('552270','NAB Card');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('555001','NAB Card');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('556733','NAB Card');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('558388','NAB Card');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('558741','NAB Card');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('558836','NAB Card');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('561058','NAB Card');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('504545','BBL Card');");
            Execute.Sql("insert into dbo.ref_credit_card (card_prefix, card_type) values ('561055','CBA Card');");
        }

        public override void Down()
        {
            Delete.Table("ref_credit_card");
        }
    }
}
