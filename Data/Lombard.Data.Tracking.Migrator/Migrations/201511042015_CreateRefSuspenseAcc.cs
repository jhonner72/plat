using FluentMigrator;
using FluentMigrator.Runner.Extensions;
namespace Lombard.Data.Tracking.Migrator.Migrations
{
    [Migration(20150411000002)]
    public class _201511042015_CreateRefSuspenseAcc : Migration
    {
        public override void Up()
        {
               var isInsert = true;
               if (!Schema.Table("ref_suspense_acc").Exists())
               {
                   Create.Table("ref_suspense_acc")
                       .WithColumn("ref_id").AsInt32().NotNullable().PrimaryKey("PK_ref_suspense_acc").Identity(1, 1)
                       .WithColumn("bank_code").AsString(3).NotNullable()
                       .WithColumn("suspense_bsb").AsString(6).NotNullable()
                       .WithColumn("suspense_acc").AsString(17).NotNullable();

                   isInsert = true;
               }
               else
                   isInsert = false;

               SeedData(isInsert);
        }

        public override void Down()
        {
            Delete.Table("ref_suspense_acc");
        }

        private void SeedData(bool isInsert)
        {
            if (!isInsert)
            {
                Execute.Sql("DELETE FROM ref_suspense_acc;");
            }

            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('BQL','124001','10457814');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('MBL','182222','052951001');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('CMB','21','999999912');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('ARA','917101','818965036');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('CTI','242200','999999915');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('CTI','242200','9002260007');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('BBL','633106','999999915');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('HBA','343001','999999912');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('LBA','942101','827998');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('IBK','819002','110000502');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('OCB','452001','810605036');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('RBS','952868','000045500');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('CRU','082172','017685165');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('MCU','313140','999999915');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('QTM','514000','999999915');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('CFC','512000','999999915');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('ADC','642000','999999915');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('TCU','812000','999999915');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('CAP','813999','999999915');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('CUA','814999','999999915');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('PCU','815000','999999915');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('WCU','817002','999999915');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('NAB','083029','899916251');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('NAB','083029','899919946');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('ALL','182222','052666104');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('CTI','242200','000518423');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('CTI','242200','9522580004');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('NAB','082401','558302825');");
            Execute.Sql("insert into dbo.ref_suspense_acc (bank_code, suspense_bsb, suspense_acc) values ('NAB','082401','562337094');");


        }
    }
}
