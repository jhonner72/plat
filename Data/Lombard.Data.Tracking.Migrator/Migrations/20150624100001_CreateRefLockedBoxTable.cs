using FluentMigrator;

namespace Lombard.Data.Tracking.Migrator.Migrations
{
    [Migration(20151030100003)]
    public class _20150624100001_CreateRefLockedBoxTable : Migration
    {
        public override void Up()
        {
            if (Schema.Table("ref_locked_box").Exists())
            {
                Delete.Table("ref_locked_box");
            }

            Create.Table("ref_locked_box")
                .WithColumn("customer_id").AsString(4).NotNullable().PrimaryKey("PK_ref_lockbox")
                .WithColumn("bsb").AsString(6).NotNullable().PrimaryKey("PK_ref_lockbox")
                .WithColumn("effective_date").AsDate().NotNullable().PrimaryKey("PK_ref_lockbox")
                .WithColumn("customer_name").AsString(100).NotNullable()
                .WithColumn("file_name").AsString(50).NotNullable()
                .WithColumn("locked_box_id").AsString(4)
                .WithColumn("state_code").AsString(3).NotNullable()
                .WithColumn("state").AsString(1).NotNullable()
                .WithColumn("nol_prefix").AsString(10)
                .WithColumn("nol_suffix").AsString(10)
                .WithColumn("settlement_bsb").AsString(6)
                .WithColumn("settlement_acc").AsString(17)
                .WithColumn("settlement_ead").AsString(17)
                .WithColumn("settlement_tc").AsString(3)
                .WithColumn("merchant_id").AsString(10);

            SeedData();
        }

        public override void Down()
        {
            Delete.Table("ref_locked_box");
        }

       private void SeedData()
       {
           Execute.Sql("insert into ref_locked_box (customer_id,bsb, effective_date, customer_name, file_name, locked_box_id, state_code, state, nol_prefix, nol_suffix, settlement_bsb, settlement_acc, settlement_ead, settlement_tc, merchant_id) values ('LBC1','991304','2015-06-30','Bayside City Council','MO.IBCC001.VR4.FILE01','4690','VIC','3','1304','.lbox','83004','584272279','0DD304','60','');");
           Execute.Sql("insert into ref_locked_box (customer_id,bsb, effective_date, customer_name, file_name, locked_box_id, state_code, state, nol_prefix, nol_suffix, settlement_bsb, settlement_acc, settlement_ead, settlement_tc, merchant_id) values ('LCG1','994700','2015-06-30','CGU Insurance Limited – Account Services','MO.ICG4001.VR8.FILE01','4700','NSW','2','4700','.lbox','82941','153399038','0DD700','60','');");
           Execute.Sql("insert into ref_locked_box (customer_id,bsb, effective_date, customer_name, file_name, locked_box_id, state_code, state, nol_prefix, nol_suffix, settlement_bsb, settlement_acc, settlement_ead, settlement_tc, merchant_id) values ('LCG2','991305','2015-06-30','CGU Insurance - Premium Payment','MO.ICG1001.VR8.FILE01','1305','NSW','2','1305','.lbox','82941','163110011','0DD305','60','');");
           Execute.Sql("insert into ref_locked_box (customer_id,bsb, effective_date, customer_name, file_name, locked_box_id, state_code, state, nol_prefix, nol_suffix, settlement_bsb, settlement_acc, settlement_ead, settlement_tc, merchant_id) values ('LCY1','991308','2015-06-30','City of Yarra','MO.ICOY001.VR4.FILE01','5217','VIC','3','GNTH','.LBX','83004','895460402','0DD308','60','');");
           Execute.Sql("insert into ref_locked_box (customer_id,bsb, effective_date, customer_name, file_name, locked_box_id, state_code, state, nol_prefix, nol_suffix, settlement_bsb, settlement_acc, settlement_ead, settlement_tc, merchant_id) values ('LEA1','991004','2015-06-30','European Article Numbering Australia','MO.IEAN001.VR4.FILE01','3004','VIC','3','EANA','.LBX','82001','696157718','0DD004','60','');");
           Execute.Sql("insert into ref_locked_box (customer_id,bsb, effective_date, customer_name, file_name, locked_box_id, state_code, state, nol_prefix, nol_suffix, settlement_bsb, settlement_acc, settlement_ead, settlement_tc, merchant_id) values ('LEG1','991313','2015-06-30','East Gippsland Shire Council','MO.IEGS001.VR4.FILE01','1651','VIC','3','EGS1','.LBX','83519','654375709','0DD313','60','');");
           Execute.Sql("insert into ref_locked_box (customer_id,bsb, effective_date, customer_name, file_name, locked_box_id, state_code, state, nol_prefix, nol_suffix, settlement_bsb, settlement_acc, settlement_ead, settlement_tc, merchant_id) values ('LGS1','991309','2015-06-30','Glenelg Shire Council','MO.IGSC001.VR4.FILE01','2819','VIC','3','GCS1','.LBX','83841','515983982','0DD309','60','');");
           Execute.Sql("insert into ref_locked_box (customer_id,bsb, effective_date, customer_name, file_name, locked_box_id, state_code, state, nol_prefix, nol_suffix, settlement_bsb, settlement_acc, settlement_ead, settlement_tc, merchant_id) values ('LHS1','991206','2015-06-30','Hill Shire Council','MO.IHSC001.VR4.FILE01','15','NSW','2','IHSC','.LBX','82155','509201761','0DD206','60','');");
           Execute.Sql("insert into ref_locked_box (customer_id,bsb, effective_date, customer_name, file_name, locked_box_id, state_code, state, nol_prefix, nol_suffix, settlement_bsb, settlement_acc, settlement_ead, settlement_tc, merchant_id) values ('LMC1','991307','2015-06-30','Maroondah City Council','MO.IMCC001.VR4.FILE01','1860','VIC','3','AB3X','.LBX','83171','515321719','0DD307','60','');");
           Execute.Sql("insert into ref_locked_box (customer_id,bsb, effective_date, customer_name, file_name, locked_box_id, state_code, state, nol_prefix, nol_suffix, settlement_bsb, settlement_acc, settlement_ead, settlement_tc, merchant_id) values ('LML1','991211','2015-06-30','MLC Limited','MO.IMLC001.VR8.FILE01','2521','NSW','2','1211','.LBX','82401','562337094','0DD211','60','');");
           Execute.Sql("insert into ref_locked_box (customer_id,bsb, effective_date, customer_name, file_name, locked_box_id, state_code, state, nol_prefix, nol_suffix, settlement_bsb, settlement_acc, settlement_ead, settlement_tc, merchant_id) values ('LML2','991202','2015-06-30','MLC Ltd Cards','MO.IMLC001.VR2.FILE01','2521','NSW','2','','','82401','562337094','0DD202','60','3283409');");
           Execute.Sql("insert into ref_locked_box (customer_id,bsb, effective_date, customer_name, file_name, locked_box_id, state_code, state, nol_prefix, nol_suffix, settlement_bsb, settlement_acc, settlement_ead, settlement_tc, merchant_id) values ('LNC1','991002','2015-06-30','National Credit Card Payments','MO.INAB001.VR1.FILE01','2','VIC','3','','','83996','455464506','0DD002','60','0');");
           Execute.Sql("insert into ref_locked_box (customer_id,bsb, effective_date, customer_name, file_name, locked_box_id, state_code, state, nol_prefix, nol_suffix, settlement_bsb, settlement_acc, settlement_ead, settlement_tc, merchant_id) values ('LOW1','992030','2015-06-30','Officeworks','MO.IVOPR01.VR4.FILE01','2030','VIC','3','VOPR','.LBX','83001','591421770','0DD030','60','');");
           Execute.Sql("insert into ref_locked_box (customer_id,bsb, effective_date, customer_name, file_name, locked_box_id, state_code, state, nol_prefix, nol_suffix, settlement_bsb, settlement_acc, settlement_ead, settlement_tc, merchant_id) values ('LPC1','991201','2015-06-30','Pittwater Council','MO.IPIT001.VR4.FILE01','303','NSW','2','PIT1','.LBX','82294','193757922','0DD201','60','');");
           Execute.Sql("insert into ref_locked_box (customer_id,bsb, effective_date, customer_name, file_name, locked_box_id, state_code, state, nol_prefix, nol_suffix, settlement_bsb, settlement_acc, settlement_ead, settlement_tc, merchant_id) values ('LRD1','991316','2015-06-30','Royal District Nursing Service (RDNS) Limited','MO.IRDS001.VR4.FILE01','8430','VIC','3','RDS1','.LBX','83155','636965094','0DD316','60','9237694');");
           Execute.Sql("insert into ref_locked_box (customer_id,bsb, effective_date, customer_name, file_name, locked_box_id, state_code, state, nol_prefix, nol_suffix, settlement_bsb, settlement_acc, settlement_ead, settlement_tc, merchant_id) values ('LRD2','991314','2015-06-30','RDNS Homecare - Cheques','MO.IRDNS01.VR4.FILE01','1625','VIC','3','RDNS','.LBX','83170','143239203','0DD314','60','');");
           Execute.Sql("insert into ref_locked_box (customer_id,bsb, effective_date, customer_name, file_name, locked_box_id, state_code, state, nol_prefix, nol_suffix, settlement_bsb, settlement_acc, settlement_ead, settlement_tc, merchant_id) values ('LRD3','991302','2015-06-30','RDNS Homecare - Cards','MO.IRDNS01.VR2.FILE01','1625','VIC','3','','','83170','143239203','0DD302','60','6134928');");
           Execute.Sql("insert into ref_locked_box (customer_id,bsb, effective_date, customer_name, file_name, locked_box_id, state_code, state, nol_prefix, nol_suffix, settlement_bsb, settlement_acc, settlement_ead, settlement_tc, merchant_id) values ('LRD4','991317','2015-06-30','Royal District Nursing Services (RDNS) Limited - Cards','MO.IRDS001.VR2.FILE01','6563','VIC','3','','','83155','636965094','0DD317','60','9237694');");
           Execute.Sql("insert into ref_locked_box (customer_id,bsb, effective_date, customer_name, file_name, locked_box_id, state_code, state, nol_prefix, nol_suffix, settlement_bsb, settlement_acc, settlement_ead, settlement_tc, merchant_id) values ('LSV1','991205','2015-06-30','St Vincent’s Mater Hospital','MO.ISVH001.VR4.FILE01','3911','NSW','2','SVHM','.LBX','82057','509413705','0DD205','60','');");
           Execute.Sql("insert into ref_locked_box (customer_id,bsb, effective_date, customer_name, file_name, locked_box_id, state_code, state, nol_prefix, nol_suffix, settlement_bsb, settlement_acc, settlement_ead, settlement_tc, merchant_id) values ('LSV2','991208','2015-06-30','St Vincents Hospital Sydney Limited','MO.ISVHS01.VR4.FILE01','4472','NSW','2','SVHS','.LBX','82057','509413617','0DD208','60','');");
           Execute.Sql("insert into ref_locked_box (customer_id,bsb, effective_date, customer_name, file_name, locked_box_id, state_code, state, nol_prefix, nol_suffix, settlement_bsb, settlement_acc, settlement_ead, settlement_tc, merchant_id) values ('LSV3','991210','2015-06-30','St Vincents and Mater Health Sydney Limited','MO.ISVHH01.VR4.FILE01','4597','NSW','2','SVHH','.LBX','82048','595332468','0DD210','60','');");
           Execute.Sql("insert into ref_locked_box (customer_id,bsb, effective_date, customer_name, file_name, locked_box_id, state_code, state, nol_prefix, nol_suffix, settlement_bsb, settlement_acc, settlement_ead, settlement_tc, merchant_id) values ('LSV4','991209','2015-06-30','St Vincents Private Hospital Sydney Limited','MO.ISVHP01.VR4.FILE01','4657','NSW','2','SVHP','.LBX','82048','509412489','0DD209','60','');");
           Execute.Sql("insert into ref_locked_box (customer_id,bsb, effective_date, customer_name, file_name, locked_box_id, state_code, state, nol_prefix, nol_suffix, settlement_bsb, settlement_acc, settlement_ead, settlement_tc, merchant_id) values ('LSV5','991207','2015-06-30','Trustee of St Vincents Hospital Sydney','MO.ISVHT01.VR4.FILE01','4357','NSW','2','SVHT','.LBX','82057','175330054','0DD207','60','');");
           Execute.Sql("insert into ref_locked_box (customer_id,bsb, effective_date, customer_name, file_name, locked_box_id, state_code, state, nol_prefix, nol_suffix, settlement_bsb, settlement_acc, settlement_ead, settlement_tc, merchant_id) values ('LSV6','991212','2015-06-30','St Vincents Curran Foundation','MO.ISVC001.VR4.FILE01','4603','NSW','2','SVCF','.LBX','82057','28349941','0DD212','60','');");

       }
    }
}
