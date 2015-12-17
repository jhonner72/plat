using FluentMigrator;


namespace Lombard.Data.Tracking.Migrator.Migrations
{
    [Migration(20150622100051)]
    public class _20150622100001_CreateRefFeeTable : Migration
    {
        public override void Up()
        {
            Create.Table("ref_fee")
                .WithColumn("fee_name").AsString(100).NotNullable().PrimaryKey("PK_ref_fee")
                .WithColumn("effective_date").AsDate().NotNullable().PrimaryKey("PK_ref_fee")
                .WithColumn("fee_unit_price").AsCurrency().NotNullable();
            SeedData();
        }

        public override void Down()
        {
            Delete.Table("ref_fee");
        }

        private void SeedData()
        {
            Execute.Sql("insert into dbo.ref_fee (fee_name, effective_date, fee_unit_price) values ('Outward Clearings','20150601',0.0842);");
            Execute.Sql("insert into dbo.ref_fee (fee_name, effective_date, fee_unit_price) values ('Locked Box','20150601',0.3186);");
            Execute.Sql("insert into dbo.ref_fee (fee_name, effective_date, fee_unit_price) values ('Inward Clearings IE','20150601',0.0032);");
            Execute.Sql("insert into dbo.ref_fee (fee_name, effective_date, fee_unit_price) values ('Third Party Checking','20150601',0.035);");
            Execute.Sql("insert into dbo.ref_fee (fee_name, effective_date, fee_unit_price) values ('Third Party Checking Validation','20150601',0.725);");
            Execute.Sql("insert into dbo.ref_fee (fee_name, effective_date, fee_unit_price) values ('Inward Dishonour Letters','20150601',0.6545);");
            Execute.Sql("insert into dbo.ref_fee (fee_name, effective_date, fee_unit_price) values ('Inward For Value Processing','20150601',0.195);");
            Execute.Sql("insert into dbo.ref_fee (fee_name, effective_date, fee_unit_price) values ('Burroughs Elite Device Charge','20150601',5000);");
            Execute.Sql("insert into dbo.ref_fee (fee_name, effective_date, fee_unit_price) values ('Burroughs Elite Device Consumable Charge','20150601',500);");
            Execute.Sql("insert into dbo.ref_fee (fee_name, effective_date, fee_unit_price) values ('Standard Listing Processing','20150601',20700);");
            Execute.Sql("insert into dbo.ref_fee (fee_name, effective_date, fee_unit_price) values ('Image Repository Management Charge','20150601',44056);");
            Execute.Sql("insert into dbo.ref_fee (fee_name, effective_date, fee_unit_price) values ('Fixed Scanning Charge NSW','20150601',1);");
            Execute.Sql("insert into dbo.ref_fee (fee_name, effective_date, fee_unit_price) values ('Fixed Scanning Charge VIC','20150601',2);");
            Execute.Sql("insert into dbo.ref_fee (fee_name, effective_date, fee_unit_price) values ('Fixed Scanning Charge QLD','20150601',3);");
            Execute.Sql("insert into dbo.ref_fee (fee_name, effective_date, fee_unit_price) values ('Fixed Scanning Charge WA','20150601',4);");
            Execute.Sql("insert into dbo.ref_fee (fee_name, effective_date, fee_unit_price) values ('Fixed Scanning Charge SA','20150601',5);");
            Execute.Sql("insert into dbo.ref_fee (fee_name, effective_date, fee_unit_price) values ('Excess Daily Listings Charge','20150601',100);");
            Execute.Sql("insert into dbo.ref_fee (fee_name, effective_date, fee_unit_price) values ('Priority 1 & 2 Document Retrieval','20150601',100);");
            Execute.Sql("insert into dbo.ref_fee (fee_name, effective_date, fee_unit_price) values ('Late Courier Overtime Processing','20150601',500);");
            Execute.Sql("insert into dbo.ref_fee (fee_name, effective_date, fee_unit_price) values ('Secure Destruction of Cheques and Vouchers','20150601',5);");
            Execute.Sql("insert into dbo.ref_fee (fee_name, effective_date, fee_unit_price) values ('SLA Penalty Fee','20150601',1000);");
            Execute.Sql("insert into dbo.ref_fee (fee_name, effective_date, fee_unit_price) values ('Accrued Interest Debt','20150601',1000);");

        }
    }
}
