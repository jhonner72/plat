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
    public partial class CheckThirdPartyFeature
    {
        
        private static TechTalk.SpecFlow.ITestRunner testRunner;
        
#line 1 "CheckThirdParty.feature"
#line hidden
        
        [Microsoft.VisualStudio.TestTools.UnitTesting.ClassInitializeAttribute()]
        public static void FeatureSetup(Microsoft.VisualStudio.TestTools.UnitTesting.TestContext testContext)
        {
            testRunner = TechTalk.SpecFlow.TestRunnerManager.GetTestRunner();
            TechTalk.SpecFlow.FeatureInfo featureInfo = new TechTalk.SpecFlow.FeatureInfo(new System.Globalization.CultureInfo("en-US"), "CheckThirdParty", "Given a valid batch\r\nWhen a check third party request is issued to DIPS\r\nThe code" +
                    "line correction request batch should be viewable in DIPS", ProgrammingLanguage.CSharp, ((string[])(null)));
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
                        && (TechTalk.SpecFlow.FeatureContext.Current.FeatureInfo.Title != "CheckThirdParty")))
            {
                Lombard.Adapters.DipsAdapter.IntegrationTests.Features.CheckThirdPartyFeature.FeatureSetup(null);
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
        [Microsoft.VisualStudio.TestTools.UnitTesting.DescriptionAttribute("Check Third Party")]
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestPropertyAttribute("FeatureTitle", "CheckThirdParty")]
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestCategoryAttribute("CheckThirdParty")]
        public virtual void CheckThirdParty()
        {
            TechTalk.SpecFlow.ScenarioInfo scenarioInfo = new TechTalk.SpecFlow.ScenarioInfo("Check Third Party", new string[] {
                        "CheckThirdParty"});
#line 7
this.ScenarioSetup(scenarioInfo);
#line 8
 testRunner.Given("there are no CheckThirdParty rows for batch number 58800018", ((string)(null)), ((TechTalk.SpecFlow.Table)(null)), "Given ");
#line hidden
            TechTalk.SpecFlow.Table table1 = new TechTalk.SpecFlow.Table(new string[] {
                        "auxDom",
                        "extraAuxDom",
                        "bsbNumber",
                        "accountNumber",
                        "transactionCode",
                        "documentReferenceNumber",
                        "imageRotation",
                        "capturedAmount",
                        "amountConfidenceLevel",
                        "amountRegionOfInterest",
                        "workType",
                        "processingDate",
                        "preAdjustmentAmount",
                        "adjustedFlag",
                        "thirdPartyCheckFailed",
                        "thirdPartyPoolFlag",
                        "highValueFlag",
                        "voucherDelayedIndicator",
                        "thirdPartyMixedDepositReturnFlag",
                        "dipsSequenceNumber"});
            table1.AddRow(new string[] {
                        "001193",
                        "",
                        "013812",
                        "256902729",
                        "50",
                        "000111222",
                        "",
                        "45.67",
                        "",
                        "",
                        "NABCHQ_POD",
                        "2015/03/17",
                        "55",
                        "true",
                        "true",
                        "true",
                        "true",
                        "1",
                        "true",
                        "0000"});
            table1.AddRow(new string[] {
                        "001193",
                        "",
                        "092002",
                        "814649",
                        "50",
                        "000111223",
                        "",
                        "2341.45",
                        "",
                        "",
                        "NABCHQ_POD",
                        "2015/03/17",
                        "55",
                        "false",
                        "false",
                        "false",
                        "false",
                        "0",
                        "false",
                        "0001"});
#line 9
 testRunner.And("a CheckThirdPartyBatchRequest with batch number 58800018 is added to the queue fo" +
                    "r the following CheckThirdParty vouchers:", ((string)(null)), table1, "And ");
#line hidden
            TechTalk.SpecFlow.Table table2 = new TechTalk.SpecFlow.Table(new string[] {
                        "Field",
                        "Value"});
            table2.AddRow(new string[] {
                        "workType",
                        "NABCHQ_POD"});
            table2.AddRow(new string[] {
                        "processingState",
                        "SA"});
            table2.AddRow(new string[] {
                        "scannedBatchNumber",
                        "58800018"});
            table2.AddRow(new string[] {
                        "captureBSB",
                        "085384"});
            table2.AddRow(new string[] {
                        "collectingBank",
                        "123456"});
            table2.AddRow(new string[] {
                        "unitID",
                        "123"});
#line 13
 testRunner.And("a CheckThirdPartyBatchRequest with batch number 58800018 contains this voucher ba" +
                    "tch:", ((string)(null)), table2, "And ");
#line hidden
            TechTalk.SpecFlow.Table table3 = new TechTalk.SpecFlow.Table(new string[] {
                        "Field",
                        "Value"});
            table3.AddRow(new string[] {
                        "CorrelationId",
                        "NSBD-6e5bc63b-be84-4053-a4ce-191abbd69f27"});
            table3.AddRow(new string[] {
                        "RoutingKey",
                        "NSBD"});
            table3.AddRow(new string[] {
                        "PublishTimeOutSeconds",
                        "3"});
#line 21
 testRunner.When("the message is published to the queue and CheckThirdPartyBatchRequest process the" +
                    " message with this information:", ((string)(null)), table3, "When ");
#line hidden
            TechTalk.SpecFlow.Table table4 = new TechTalk.SpecFlow.Table(new string[] {
                        "Field",
                        "Value"});
            table4.AddRow(new string[] {
                        "S_LOCATION",
                        "ThirdPartyCheck"});
            table4.AddRow(new string[] {
                        "S_LOCK",
                        "0"});
            table4.AddRow(new string[] {
                        "S_CLIENT",
                        "NabChq"});
            table4.AddRow(new string[] {
                        "S_JOB_ID",
                        "NabChqPod"});
            table4.AddRow(new string[] {
                        "S_MODIFIED",
                        "0"});
            table4.AddRow(new string[] {
                        "S_COMPLETE",
                        "0"});
            table4.AddRow(new string[] {
                        "S_BATCH",
                        "58800018"});
            table4.AddRow(new string[] {
                        "S_TRACE",
                        "000111222"});
            table4.AddRow(new string[] {
                        "S_PRIORITY",
                        "5"});
            table4.AddRow(new string[] {
                        "S_VERSION",
                        "4.0.2.152"});
            table4.AddRow(new string[] {
                        "S_SDATE",
                        "17/03/15"});
            table4.AddRow(new string[] {
                        "RoutingKey",
                        "NSBD"});
#line 26
 testRunner.Then("a DipsQueue table row will exist with the following CheckThirdParty values", ((string)(null)), table4, "Then ");
#line hidden
            TechTalk.SpecFlow.Table table5 = new TechTalk.SpecFlow.Table(new string[] {
                        "S_BATCH",
                        "S_TRACE",
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
                        "processing_state",
                        "captureBSB",
                        "collecting_bank",
                        "unit_id",
                        "tpcMixedDepRet",
                        "fxa_tpc_suspense_pool_flag",
                        "highValueFlag"});
            table5.AddRow(new string[] {
                        "58800018",
                        "000111222",
                        "01025",
                        "58800018",
                        "000111222",
                        "20150317",
                        "",
                        "001193",
                        "013812",
                        "256902729",
                        "50",
                        "",
                        "NabChqPod",
                        "SA",
                        "085384",
                        "123456",
                        "123",
                        "1",
                        "1",
                        "1"});
            table5.AddRow(new string[] {
                        "58800018",
                        "000111223",
                        "01025",
                        "58800018",
                        "000111223",
                        "20150317",
                        "",
                        "001193",
                        "092002",
                        "814649",
                        "50",
                        "",
                        "NabChqPod",
                        "SA",
                        "085384",
                        "123456",
                        "123",
                        "0",
                        "0",
                        "0"});
#line 40
 testRunner.And("DipsNabChq table rows will exist with the following CheckThirdParty values", ((string)(null)), table5, "And ");
#line hidden
            TechTalk.SpecFlow.Table table6 = new TechTalk.SpecFlow.Table(new string[] {
                        "BATCH",
                        "TRACE",
                        "TABLE_NO"});
            table6.AddRow(new string[] {
                        "58800018",
                        "000111222",
                        "0"});
            table6.AddRow(new string[] {
                        "58800018",
                        "000111223",
                        "0"});
#line 44
 testRunner.And("DipsDbIndex table rows will exist with the following CheckThirdParty values", ((string)(null)), table6, "And ");
#line hidden
            this.ScenarioCleanup();
        }
    }
}
#pragma warning restore
#endregion