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
    public partial class GenerateBulkCreditRequestFeature
    {
        
        private static TechTalk.SpecFlow.ITestRunner testRunner;
        
#line 1 "GenerateBulkCreditRequest.feature"
#line hidden
        
        [Microsoft.VisualStudio.TestTools.UnitTesting.ClassInitializeAttribute()]
        public static void FeatureSetup(Microsoft.VisualStudio.TestTools.UnitTesting.TestContext testContext)
        {
            testRunner = TechTalk.SpecFlow.TestRunnerManager.GetTestRunner();
            TechTalk.SpecFlow.FeatureInfo featureInfo = new TechTalk.SpecFlow.FeatureInfo(new System.Globalization.CultureInfo("en-US"), "Generate Bulk Credit Request", "Given a valid request\r\nWhen a generate bulk credit request is issued to DIPS\r\nThe" +
                    " correct bulk credit vouchers should be viewable in DIPS", ProgrammingLanguage.CSharp, ((string[])(null)));
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
                        && (TechTalk.SpecFlow.FeatureContext.Current.FeatureInfo.Title != "Generate Bulk Credit Request")))
            {
                Lombard.Adapters.DipsAdapter.IntegrationTests.Features.GenerateBulkCreditRequestFeature.FeatureSetup(null);
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
        [Microsoft.VisualStudio.TestTools.UnitTesting.DescriptionAttribute("Generate Bulk Credit Requests")]
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestPropertyAttribute("FeatureTitle", "Generate Bulk Credit Request")]
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestCategoryAttribute("GenerateBulkCreditRequest")]
        public virtual void GenerateBulkCreditRequests()
        {
            TechTalk.SpecFlow.ScenarioInfo scenarioInfo = new TechTalk.SpecFlow.ScenarioInfo("Generate Bulk Credit Requests", new string[] {
                        "GenerateBulkCreditRequest"});
#line 7
this.ScenarioSetup(scenarioInfo);
#line 8
 testRunner.Given("there are no GenerateBatchBulkCreditRequest rows for batch number 96384477", ((string)(null)), ((TechTalk.SpecFlow.Table)(null)), "Given ");
#line hidden
            TechTalk.SpecFlow.Table table1 = new TechTalk.SpecFlow.Table(new string[] {
                        "captureBsb",
                        "documentReferenceNumber",
                        "processingDate"});
            table1.AddRow(new string[] {
                        "085384",
                        "895214001",
                        "2015/10/20"});
            table1.AddRow(new string[] {
                        "085385",
                        "895214003",
                        "2015/10/20"});
#line 9
 testRunner.And("a GenerateBatchBulkCreditRequest is added to the queue for the following BulkCred" +
                    "itRequest vouchers:", ((string)(null)), table1, "And ");
#line hidden
            TechTalk.SpecFlow.Table table2 = new TechTalk.SpecFlow.Table(new string[] {
                        "Field",
                        "Value"});
            table2.AddRow(new string[] {
                        "CorrelationId",
                        "NECL-47887b18-e731-4839-b77a-d48c00003328/NGBC-04d91de6-5051-4a17-ab70-b296bb1c4c" +
                            "c4"});
            table2.AddRow(new string[] {
                        "RoutingKey",
                        "NECL"});
            table2.AddRow(new string[] {
                        "PublishTimeOutSeconds",
                        "3"});
#line 13
 testRunner.When("the message is published to the queue and GenerateBatchBulkCreditRequest process " +
                    "the message with this information:", ((string)(null)), table2, "When ");
#line hidden
            this.ScenarioCleanup();
        }
    }
}
#pragma warning restore
#endregion
