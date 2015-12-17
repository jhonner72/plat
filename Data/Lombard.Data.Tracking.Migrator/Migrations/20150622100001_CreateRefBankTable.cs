using FluentMigrator;


namespace Lombard.Data.Tracking.Migrator.Migrations
{
    [Migration(20150622100013)]
    public class _20150622100001_CreateRefBankTable : Migration
    {
        public override void Up()
        {
              var isInsert = true;
              if (!Schema.Table("ref_bank").Exists())
              {
                  Create.Table("ref_bank")
                      .WithColumn("bank_code").AsString(3).NotNullable().PrimaryKey("PK_ref_bank")
                      .WithColumn("bank_name").AsString(100).NotNullable().PrimaryKey("PK_ref_bank")
                      .WithColumn("bsb").AsString(3).NotNullable().PrimaryKey("PK_ref_bank")
                      .WithColumn("effective_date").AsDate().NotNullable().PrimaryKey("PK_ref_bank")
                      .WithColumn("bank_group_code").AsString(3).NotNullable().PrimaryKey("PK_ref_bank");

                  isInsert = true;
              }
              else
                  isInsert = false;  

            SeedData(isInsert);
        }

        public override void Down()
        {
            Delete.Table("ref_bank");
        }

        private void SeedData(bool isInsert)
        {
            if (!isInsert)
            {
                Execute.Sql("DELETE FROM ref_bank;");
            }

            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('AMP','AMP Bank Ltd','939','19800101','AMP');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('ARA','Arab Bank Australia Ltd','917','19800101','ARA');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('ANZ','Australia and New Zealand Banking Group Ltd','01','19800101','ANZ');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('ANZ','Australia and New Zealand Banking Group Ltd','71','19800101','ANZ');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('ANZ','Australia and New Zealand Banking Group Ltd','27','19800101','ANZ');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('ANZ','Australia and New Zealand Banking Group Ltd','28','19800101','ANZ');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('T&C','Australia and New Zealand Banking Group Ltd','15','19800101','ANZ');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('APO','Australia Post','90','20110523','APO');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('ADC','Australian Defence Credit Union','642','20110307','ADC');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('BAL','Bank of America, NA','23','20110725','BAL');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('BOC','Bank of China','35','19800101','BOC');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('BCA','Bank of China (Australia) Ltd','980','19800101','BCA');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('COM','Bank of Communications Co. Ltd','818','20120305','COM');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('BQL','Bank of Queensland Ltd','12','19800101','BQL');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('PPB','Bank of Queensland Ltd','653','20071126','BQL');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('HOM','Bank of Queensland Ltd','639','20080922','BQL');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('LBA','Bank of Sydney Ltd','942','20130503','LBA');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('BOT','Bank of Tokyo-Mitsubishi UFJ, Ltd','29','20060101','BOT');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('BBL','Bendigo and Adelaide Bank Limited (BBL)','633','20110912','BBL');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('ADL','Bendigo and Adelaide Bank Limited (ADL)','610','20110912','ADL');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('BNP','BNP Paribas','22','20111111','BNP');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('BPS','BNP Paribas Securities Services','255','20120910','BPS');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('CAP','Capricornian Ltd (The)','813','20090316','CAP');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('CTI','Citigroup Pty Ltd','24','20060111','CTI');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('CBA','Commonwealth Bank of Australia','06','19800101','CBA');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('CBA','Commonwealth Bank of Australia','76','19800101','CBA');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('CST','Commonwealth Bank of Australia','40','19800101','CBA');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('TBT','Commonwealth Bank of Australia','42','19800101','CBA');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('TBT','Commonwealth Bank of Australia','52','19800101','CBA');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('BWA','Commonwealth Bank of Australia','30','20140721','CBA');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('CDB','Commonwealth Development Bank of Australia','86','19800101','CDB');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('CFC','Community First Credit Union Limited','512','20110523','CFC');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('NEC','Community Mutual Ltd','932','20120501','NEC');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('CUA','Credit Union Australia Limited','814','20081124','CUA');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('CRU','Cuscal Limited','80','20070319','CRU');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('BCY','Delphi Bank (a division of Bendigo and Adelaide Bank Limited)','941','19800101','BCY');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('DBA','Deutsche Bank Aktiengesellschaft','41','19800101','DBA');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('ECU','ECU Australia Limited','654','20120521','ECU');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('GBS','Greater Building Society Ltd','637','20050725','GBS');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('HBS','Heritage Bank Limited','638','19800101','HBS');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('HBA','HSBC Bank Australia Ltd','34','19800101','HBA');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('HUM','Hume Bank Limited','640','20140701','HUM');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('HUE','Hunter United Employees Credit Union Ltd','704','19800101','HUE');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('CUS','Indue Ltd','70','20080728','CUS');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('IBK','Industrial and Commercial Bank of China Limited','819','20081124','IBK');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('ING','ING Bank (Australia) Ltd','923','19800101','ING');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('GNI','ING Bank, NV (Sydney Branch)','936','19800101','GNI');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('CMB','JP Morgan Chase Bank, NA','21','19800101','CMB');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('FNC','JP Morgan Chase Bank, NA','915','19800101','FNC');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('MBL','Macquarie Bank Ltd','18','19800101','MBL');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('MMB','Maitland Mutual Building Society Ltd','646','20091123','MMB');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('MCU','mecu Limited','313','20111122','MCU');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('MCU','mecu Limited','31','20111122','MCU');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('ICB','Mega International Commercial Bank Co, Ltd','931','19800101','ICB');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('MEB','Members Equity Bank Pty Ltd','944','19800101','MEB');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('MCB','Mizuho Bank, Ltd','918','20130730','MCB');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('NAB','National Australia Bank Ltd','08','19800101','NAB');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('NAB','National Australia Bank Ltd','78','19800101','NAB');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('NAB','National Australia Bank Ltd','05','19800101','NAB');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('NAB','National Australia Bank Ltd','75','19800101','NAB');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('BNZ','National Australia Bank Ltd','20','19800101','NAB');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('NEW','Newcastle Permanent Building Society Ltd','650','20050725','NEW');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('OCB','Oversea-Chinese Banking Corporation Ltd','45','19800101','OCB');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('PCU','Police Bank Ltd','815','20121203','PCU');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('PNB','Police & Nurses Limited, trading as p&nbank','777','20131118','PNB');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('QTM','QT Mutual Bank Ltd','514','20130909','QTM');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('PIB','Rabobank Australia Ltd','14','19800101','PIB');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('RCU','Railways Credit Union Limited','721','20120305','RCU');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('RBA','Reserve Bank of Australia','09','19800101','RBA');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('SGE','SGE Credit Union','659','20120723','SGE');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('SNX','Southern Cross Credit Union Ltd','722','20120723','SNX');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('SSB','State Street Bank & Trust Company','913','19800101','SSB');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('SCU','Summerland Credit Union Ltd','728','20120521','SCU');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('MET','Suncorp-Metway Ltd','48','20110912','MET');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('SUN','Suncorp-Metway Ltd','66','20110912','SUN');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('TBB','Taiwan Business Bank (Sydney Branch)','943','20091123','TBB');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('TCU','Teachers Mutual Bank Limited','812','20100524','TCU');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('HSB','The Hong Kong & Shanghai Banking Corporation Limited, Australia Branch','985','20110912','HSB');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('RBS','The Royal Bank of Scotland PLC, Australia Branch','952','20100524','RBS');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('UOB','United Overseas Bank Ltd','922','19800101','UOB');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('UFS','Uniting Financial Services','634','20090316','UFS');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('WCU','Warwick Credit Union Ltd','817','20090914','WCU');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('WBC','Westpac Banking Corporation','03','19800101','WBC');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('WBC','Westpac Banking Corporation','73','19800101','WBC');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('WBC','Westpac Banking Corporation','04','19800101','WBC');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('WBC','Westpac Banking Corporation','74','19800101','WBC');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('CBL','Westpac Banking Corporation','47','19800101','WBC');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('BML','Westpac Banking Corporation','55','19800101','WBC');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('BOM','Westpac Banking Corporation','19','20110523','WBC');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('BTA','Westpac Banking Corporation','26','19800101','WBC');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('STG','Westpac Banking Corporation','11','20111121','WBC');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('SGP','Westpac Banking Corporation','33','20111121','WBC');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('ADV','Westpac Banking Corporation','46','20111121','WBC');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('ADV','Westpac Banking Corporation','50','20111121','WBC');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('BSA','Westpac Banking Corporation','10','20111121','WBC');");
            Execute.Sql("insert into dbo.ref_bank (bank_code, bank_name, bsb, effective_date, bank_group_code) values ('MPB','Wide Bay Australia Ltd','645','20080728','MPB');");
        }
    }
}
