﻿// ------------------------------------------------------------------------------
//  <auto-generated>
//      This code was generated by SpecFlow (http://www.specflow.org/).
//      SpecFlow Version:1.9.0.77
//      SpecFlow Generator Version:1.9.0.0
//      Runtime Version:4.0.30319.18408
// 
//      Changes to this file may cause incorrect behavior and will be lost if
//      the code is regenerated.
//  </auto-generated>
// ------------------------------------------------------------------------------
#region Designer generated code
#pragma warning disable
namespace Lombard.Adapters.DipsAdapter.IntegrationTests.Features
{
    using TechTalk.SpecFlow;
    
    
    [System.CodeDom.Compiler.GeneratedCodeAttribute("TechTalk.SpecFlow", "1.9.0.77")]
    [System.Runtime.CompilerServices.CompilerGeneratedAttribute()]
    [Microsoft.VisualStudio.TestTools.UnitTesting.TestClassAttribute()]
    public partial class GenerateBulkCreditResponsePollingJobFeature
    {
        
        private static TechTalk.SpecFlow.ITestRunner testRunner;
        
#line 1 "GenerateBulkCreditResponsePollingJob.feature"
#line hidden
        
        [Microsoft.VisualStudio.TestTools.UnitTesting.ClassInitializeAttribute()]
        public static void FeatureSetup(Microsoft.VisualStudio.TestTools.UnitTesting.TestContext testContext)
        {
            testRunner = TechTalk.SpecFlow.TestRunnerManager.GetTestRunner();
            TechTalk.SpecFlow.FeatureInfo featureInfo = new TechTalk.SpecFlow.FeatureInfo(new System.Globalization.CultureInfo("en-US"), "GenerateBulkCreditResponsePollingJob", "Given a valid record in queue database\r\nAnd valid vouchers in database\r\nWhen wait" +
                    " and a GenerateBulkCreditResponsePollingJob executed\r\nThen a GenerateBatchBulkCr" +
                    "editResponse including vouchers is added to the exchange", ProgrammingLanguage.CSharp, ((string[])(null)));
            testRunner.OnFeatureStart(featureInfo);
        }
        
        [Microsoft.VisualStudio.TestTools.UnitTesting.ClassCleanupAttribute()]
        public static void FeatureTearDown()
        {
            testRunner.OnFeatureEnd();
            testRunner = null;
        }
        
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestInitializeAttribute()]
        public virtual void TestInitialize()
        {
            if (((TechTalk.SpecFlow.FeatureContext.Current != null) 
                        && (TechTalk.SpecFlow.FeatureContext.Current.FeatureInfo.Title != "GenerateBulkCreditResponsePollingJob")))
            {
                Lombard.Adapters.DipsAdapter.IntegrationTests.Features.GenerateBulkCreditResponsePollingJobFeature.FeatureSetup(null);
            }
        }
        
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestCleanupAttribute()]
        public virtual void ScenarioTearDown()
        {
            testRunner.OnScenarioEnd();
        }
        
        public virtual void ScenarioSetup(TechTalk.SpecFlow.ScenarioInfo scenarioInfo)
        {
            testRunner.OnScenarioStart(scenarioInfo);
        }
        
        public virtual void ScenarioCleanup()
        {
            testRunner.CollectScenarioErrors();
        }
        
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestMethodAttribute()]
        [Microsoft.VisualStudio.TestTools.UnitTesting.DescriptionAttribute("GenerateBulkCredit response for a clean voucher")]
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestPropertyAttribute("FeatureTitle", "GenerateBulkCreditResponsePollingJob")]
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestCategoryAttribute("GenerateBulkCreditResponsePollingJob")]
        public virtual void GenerateBulkCreditResponseForACleanVoucher()
        {
            TechTalk.SpecFlow.ScenarioInfo scenarioInfo = new TechTalk.SpecFlow.ScenarioInfo("GenerateBulkCredit response for a clean voucher", new string[] {
                        "GenerateBulkCreditResponsePollingJob"});
#line 8
this.ScenarioSetup(scenarioInfo);
#line hidden
            TechTalk.SpecFlow.Table table1 = new TechTalk.SpecFlow.Table(new string[] {
                        "Field",
                        "Value"});
            table1.AddRow(new string[] {
                        "S_LOCATION",
                        "BulkCreditDone"});
            table1.AddRow(new string[] {
                        "S_LOCK",
                        "0"});
            table1.AddRow(new string[] {
                        "S_CLIENT",
                        "NabChq"});
            table1.AddRow(new string[] {
                        "S_JOB_ID",
                        "NabChqPod"});
            table1.AddRow(new string[] {
                        "S_MODIFIED",
                        "0"});
            table1.AddRow(new string[] {
                        "S_COMPLETE",
                        "0"});
            table1.AddRow(new string[] {
                        "S_BATCH",
                        "58300013"});
            table1.AddRow(new string[] {
                        "S_TRACE",
                        "583000026"});
            table1.AddRow(new string[] {
                        "S_PRIORITY",
                        "5"});
            table1.AddRow(new string[] {
                        "S_VERSION",
                        "4.0.2.152"});
            table1.AddRow(new string[] {
                        "S_SDATE",
                        "31/03/15"});
            table1.AddRow(new string[] {
                        "S_STIME",
                        "12:30:00"});
            table1.AddRow(new string[] {
                        "RoutingKey",
                        "NECL"});
#line 9
 testRunner.Given("there are Generate Bulk Credit Voucher database rows for batch number 58300013 in" +
                    " DipsQueue database", ((string)(null)), table1, "Given ");
#line hidden
            TechTalk.SpecFlow.Table table2 = new TechTalk.SpecFlow.Table(new string[] {
                        "S_BATCH",
                        "S_TRACE",
                        "doc_ref_num",
                        "S_LENGTH",
                        "batch",
                        "trace",
                        "proc_date",
                        "ead",
                        "ser_num",
                        "bsb_num",
                        "acc_num",
                        "trancode",
                        "amount",
                        "job_id",
                        "S_STATUS1",
                        "micr_unproc_flag",
                        "doc_type",
                        "balanceReason",
                        "manual_repair",
                        "micr_suspect_fraud_flag",
                        "op_id",
                        "S_SEQUENCE",
                        "sys_date",
                        "captureBSB",
                        "processing_state",
                        "collecting_bank",
                        "unit_id",
                        "isGeneratedVoucher",
                        "delay_ind",
                        "tpcResult",
                        "fxa_unencoded_ECD_return",
                        "fxa_tpc_suspense_pool_flag",
                        "highValueFlag"});
            table2.AddRow(new string[] {
                        "58300013",
                        "583000026",
                        "583000026",
                        "01025",
                        "58300013",
                        "583000026",
                        "20150331",
                        "",
                        "001193",
                        "013812",
                        "256902729",
                        "50",
                        "45.67",
                        "NabChqPod",
                        "0",
                        "0",
                        "CRT",
                        "Unbalanced",
                        "0",
                        "0",
                        "abc",
                        "0000",
                        "20150624",
                        "085384",
                        "SA",
                        "123456",
                        "123",
                        "0",
                        "N",
                        "F",
                        "1",
                        "1",
                        "1"});
            table2.AddRow(new string[] {
                        "58300013",
                        "583000027",
                        "583000027",
                        "01025",
                        "58300013",
                        "583000027",
                        "20150331",
                        "",
                        "001193",
                        "092002",
                        "814649",
                        "50",
                        "2341.45",
                        "NabChqPod",
                        "0",
                        "0",
                        "DBT",
                        "HighValue",
                        "0",
                        "0",
                        "def",
                        "0001",
                        "20150625",
                        "085384",
                        "SA",
                        "123456",
                        "123",
                        "1",
                        "",
                        "P",
                        "0",
                        "0",
                        "0"});
#line 24
 testRunner.And("there are Generate Bulk Credit database rows for batch number 58300013 in DipsNab" +
                    "Chq database", ((string)(null)), table2, "And ");
#line 28
 testRunner.When("wait for 10 seconds until GenerateBulkCreditResponsePollingJob executed", ((string)(null)), ((TechTalk.SpecFlow.Table)(null)), "When ");
#line hidden
            TechTalk.SpecFlow.Table table3 = new TechTalk.SpecFlow.Table(new string[] {
                        "documentReferenceNumber",
                        "bsbNumber",
                        "accountNumber",
                        "auxDom",
                        "extraAuxDom",
                        "transactionCode",
                        "amount"});
            table3.AddRow(new string[] {
                        "583000027",
                        "092002",
                        "814649",
                        "001193",
                        "",
                        "50",
                        "2341.45"});
#line 29
 testRunner.Then("a GenerateBulkCreditResponse with batch number 58300013 is added to the exchange " +
                    "and contains these generated vouchers:", ((string)(null)), table3, "Then ");
#line hidden
            TechTalk.SpecFlow.Table table4 = new TechTalk.SpecFlow.Table(new string[] {
                        "documentReferenceNumber",
                        "bsbNumber",
                        "accountNumber",
                        "auxDom",
                        "extraAuxDom",
                        "transactionCode",
                        "amount"});
            table4.AddRow(new string[] {
                        "583000026",
                        "013812",
                        "256902729",
                        "001193",
                        "",
                        "50",
                        "45.67"});
#line 32
 testRunner.And("a GenerateBulkCreditResponse with batch number 58300013 is added to the exchange " +
                    "and contains these non-generated vouchers:", ((string)(null)), table4, "And ");
#line hidden
            TechTalk.SpecFlow.Table table5 = new TechTalk.SpecFlow.Table(new string[] {
                        "Field",
                        "Value"});
            table5.AddRow(new string[] {
                        "workType",
                        "NABCHQ_POD"});
            table5.AddRow(new string[] {
                        "processingState",
                        "SA"});
            table5.AddRow(new string[] {
                        "scannedBatchNumber",
                        "58300013"});
            table5.AddRow(new string[] {
                        "captureBSB",
                        "085384"});
            table5.AddRow(new string[] {
                        "collectingBank",
                        "123456"});
            table5.AddRow(new string[] {
                        "unitID",
                        "123"});
#line 35
 testRunner.And("a GenerateBulkCreditResponse with batch number 58300013 contains this voucher bat" +
                    "ch:", ((string)(null)), table5, "And ");
#line hidden
            this.ScenarioCleanup();
        }
    }
}
#pragma warning restore
#endregion
