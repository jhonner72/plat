namespace Lombard.Data.Tracking.Migrator.Migrations
{
    using FluentMigrator;

    [Migration(20150624100059)]
    public class _2015091400001_CreateRefCorporateTable : Migration
    {
        public override void Up()
        {
            if (Schema.Table("ref_corporate").Exists())
            {
                Delete.Table("ref_corporate");
            }

            Create.Table("ref_corporate")
                .WithColumn("corporate_group_code").AsString(4).NotNullable().PrimaryKey("PK_ref_corporate_customer")
                .WithColumn("customer_name").AsString(100).NotNullable().PrimaryKey("PK_ref_corporate_customer")
                .WithColumn("state_code").AsString(3).NotNullable().PrimaryKey("PK_ref_corporate_customer")
                .WithColumn("bsb").AsString(6).NotNullable().PrimaryKey("PK_ref_corporate_customer")
                .WithColumn("acc").AsString(17).NotNullable().PrimaryKey("PK_ref_corporate_customer")
                .WithColumn("effective_date").AsDateTime().NotNullable().PrimaryKey("PK_ref_corporate_customer")
                .WithColumn("status").AsString(1).NotNullable();

            SeedData(true);
        }
        public override void Down()
        {
            Delete.Table("ref_corporate");
        }
        private void SeedData(bool isInsert)
        {
            if (!isInsert)
            {
                Execute.Sql("DELETE FROM ref_corporate;");
            }

            Execute.Sql("INSERT [dbo].[ref_corporate] ([corporate_group_code], [customer_name], [state_code], [bsb], [acc], [effective_date], [status]) VALUES (N'CAI1', N'AIA', N'VIC', N'083001', N'037053966', CAST(N'2015-09-01' AS Date), N'A');");
            Execute.Sql("INSERT [dbo].[ref_corporate] ([corporate_group_code], [customer_name], [state_code], [bsb], [acc], [effective_date], [status]) VALUES (N'CCO1', N'Coles', N'VIC', N'083001', N'515096734', CAST(N'2015-09-01' AS Date), N'A');");
            Execute.Sql("INSERT [dbo].[ref_corporate] ([corporate_group_code], [customer_name], [state_code], [bsb], [acc], [effective_date], [status]) VALUES (N'CKM1', N'Kmart', N'VIC', N'083001', N'487993787', CAST(N'2015-09-01' AS Date), N'A');");
            Execute.Sql("INSERT [dbo].[ref_corporate] ([corporate_group_code], [customer_name], [state_code], [bsb], [acc], [effective_date], [status]) VALUES (N'CLL1', N'Liquorland', N'VIC', N'082330', N'045335736', CAST(N'2015-09-01' AS Date), N'A');");
            Execute.Sql("INSERT [dbo].[ref_corporate] ([corporate_group_code], [customer_name], [state_code], [bsb], [acc], [effective_date], [status]) VALUES (N'CLL1', N'Liquorland', N'VIC', N'084004', N'532436526', CAST(N'2015-09-01' AS Date), N'A');");
            Execute.Sql("INSERT [dbo].[ref_corporate] ([corporate_group_code], [customer_name], [state_code], [bsb], [acc], [effective_date], [status]) VALUES (N'CML1', N'Meryll Lynch', N'VIC', N'082001', N'148224772', CAST(N'2015-09-01' AS Date), N'A');");
            Execute.Sql("INSERT [dbo].[ref_corporate] ([corporate_group_code], [customer_name], [state_code], [bsb], [acc], [effective_date], [status]) VALUES (N'CML1', N'Meryll Lynch', N'VIC', N'082001', N'453954886', CAST(N'2015-09-01' AS Date), N'A');");
            Execute.Sql("INSERT [dbo].[ref_corporate] ([corporate_group_code], [customer_name], [state_code], [bsb], [acc], [effective_date], [status]) VALUES (N'CML1', N'Meryll Lynch', N'VIC', N'082001', N'689869303', CAST(N'2015-09-01' AS Date), N'A');");
            Execute.Sql("INSERT [dbo].[ref_corporate] ([corporate_group_code], [customer_name], [state_code], [bsb], [acc], [effective_date], [status]) VALUES (N'CML1', N'Meryll Lynch', N'VIC', N'082817', N'564205984', CAST(N'2015-09-01' AS Date), N'A');");
            Execute.Sql("INSERT [dbo].[ref_corporate] ([corporate_group_code], [customer_name], [state_code], [bsb], [acc], [effective_date], [status]) VALUES (N'CML1', N'Meryll Lynch', N'VIC', N'083004', N'048055190', CAST(N'2015-09-01' AS Date), N'A');");
            Execute.Sql("INSERT [dbo].[ref_corporate] ([corporate_group_code], [customer_name], [state_code], [bsb], [acc], [effective_date], [status]) VALUES (N'CML1', N'Meryll Lynch', N'VIC', N'083004', N'482874869', CAST(N'2015-09-01' AS Date), N'A');");
            Execute.Sql("INSERT [dbo].[ref_corporate] ([corporate_group_code], [customer_name], [state_code], [bsb], [acc], [effective_date], [status]) VALUES (N'CML1', N'Meryll Lynch', N'VIC', N'083004', N'515181218', CAST(N'2015-09-01' AS Date), N'A');");
            Execute.Sql("INSERT [dbo].[ref_corporate] ([corporate_group_code], [customer_name], [state_code], [bsb], [acc], [effective_date], [status]) VALUES (N'CML1', N'Meryll Lynch', N'VIC', N'083004', N'657446242', CAST(N'2015-09-01' AS Date), N'A');");
            Execute.Sql("INSERT [dbo].[ref_corporate] ([corporate_group_code], [customer_name], [state_code], [bsb], [acc], [effective_date], [status]) VALUES (N'COW1', N'Officeworks ', N'VIC', N'083001', N'619741734', CAST(N'2015-09-01' AS Date), N'A');");
            Execute.Sql("INSERT [dbo].[ref_corporate] ([corporate_group_code], [customer_name], [state_code], [bsb], [acc], [effective_date], [status]) VALUES (N'CSP1', N'SuperPartners', N'VIC', N'082001', N'666986080', CAST(N'2015-09-01' AS Date), N'A');");
            Execute.Sql("INSERT [dbo].[ref_corporate] ([corporate_group_code], [customer_name], [state_code], [bsb], [acc], [effective_date], [status]) VALUES (N'CSP1', N'SuperPartners', N'VIC', N'083355', N'037558307', CAST(N'2015-09-01' AS Date), N'A');");
            Execute.Sql("INSERT [dbo].[ref_corporate] ([corporate_group_code], [customer_name], [state_code], [bsb], [acc], [effective_date], [status]) VALUES (N'CSP1', N'SuperPartners', N'VIC', N'083355', N'537447494', CAST(N'2015-09-01' AS Date), N'A');");
            Execute.Sql("INSERT [dbo].[ref_corporate] ([corporate_group_code], [customer_name], [state_code], [bsb], [acc], [effective_date], [status]) VALUES (N'CSP1', N'SuperPartners', N'VIC', N'084004', N'036057228', CAST(N'2015-09-01' AS Date), N'A');");
            Execute.Sql("INSERT [dbo].[ref_corporate] ([corporate_group_code], [customer_name], [state_code], [bsb], [acc], [effective_date], [status]) VALUES (N'CSP1', N'SuperPartners', N'VIC', N'084092', N'036056284', CAST(N'2015-09-01' AS Date), N'A');");
        }
    }
}
