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
namespace Lombard.Adapters.MftAdapter.IntegrationTests.Features
{
    using TechTalk.SpecFlow;
    
    
    [System.CodeDom.Compiler.GeneratedCodeAttribute("TechTalk.SpecFlow", "1.9.0.77")]
    [System.Runtime.CompilerServices.CompilerGeneratedAttribute()]
    [Microsoft.VisualStudio.TestTools.UnitTesting.TestClassAttribute()]
    public partial class CreateCopyImageFromRequestFeature
    {
        
        private static TechTalk.SpecFlow.ITestRunner testRunner;
        
#line 1 "CreateCopyImageFromRequest.feature"
#line hidden
        
        [Microsoft.VisualStudio.TestTools.UnitTesting.ClassInitializeAttribute()]
        public static void FeatureSetup(Microsoft.VisualStudio.TestTools.UnitTesting.TestContext testContext)
        {
            testRunner = TechTalk.SpecFlow.TestRunnerManager.GetTestRunner();
            TechTalk.SpecFlow.FeatureInfo featureInfo = new TechTalk.SpecFlow.FeatureInfo(new System.Globalization.CultureInfo("en-US"), "Create CopyImage From Request", "When an HTTP request for a new DIPS package transfer is received \r\nThen a new cop" +
                    "y image message will be sent to the Job Service Exchange", ProgrammingLanguage.CSharp, ((string[])(null)));
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
                        && (TechTalk.SpecFlow.FeatureContext.Current.FeatureInfo.Title != "Create CopyImage From Request")))
            {
                Lombard.Adapters.MftAdapter.IntegrationTests.Features.CreateCopyImageFromRequestFeature.FeatureSetup(null);
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
        [Microsoft.VisualStudio.TestTools.UnitTesting.DescriptionAttribute("A valid copy image NFVX message request is received via HTTP")]
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestPropertyAttribute("FeatureTitle", "Create CopyImage From Request")]
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestCategoryAttribute("CopyImage")]
        public virtual void AValidCopyImageNFVXMessageRequestIsReceivedViaHTTP()
        {
            TechTalk.SpecFlow.ScenarioInfo scenarioInfo = new TechTalk.SpecFlow.ScenarioInfo("A valid copy image NFVX message request is received via HTTP", new string[] {
                        "CopyImage"});
#line 6
this.ScenarioSetup(scenarioInfo);
#line 7
 testRunner.Given("a new processing ID from file name is NFVX-2cbabea0-43f9-4554-b118-c80c487d97c3", ((string)(null)), ((TechTalk.SpecFlow.Table)(null)), "Given ");
#line 8
 testRunner.When("a new copy image message request body is NFVX-2cbabea0-43f9-4554-b118-c80c487d97c" +
                    "3 with RoutingKey NFVX", ((string)(null)), ((TechTalk.SpecFlow.Table)(null)), "When ");
#line 9
 testRunner.Then("a copy image message with id NFVX-2cbabea0-43f9-4554-b118-c80c487d97c3 is sent to" +
                    " the CopyImage Service Exchange with RoutingKey NFVX", ((string)(null)), ((TechTalk.SpecFlow.Table)(null)), "Then ");
#line hidden
            this.ScenarioCleanup();
        }
        
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestMethodAttribute()]
        [Microsoft.VisualStudio.TestTools.UnitTesting.DescriptionAttribute("A valid copy image NECL message request is received via HTTP")]
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestPropertyAttribute("FeatureTitle", "Create CopyImage From Request")]
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestCategoryAttribute("CopyImage")]
        public virtual void AValidCopyImageNECLMessageRequestIsReceivedViaHTTP()
        {
            TechTalk.SpecFlow.ScenarioInfo scenarioInfo = new TechTalk.SpecFlow.ScenarioInfo("A valid copy image NECL message request is received via HTTP", new string[] {
                        "CopyImage"});
#line 12
this.ScenarioSetup(scenarioInfo);
#line 13
 testRunner.Given("a new processing ID from file name is NSBP-2cbabea0-43f9-4554-b118-c80c487d97c3", ((string)(null)), ((TechTalk.SpecFlow.Table)(null)), "Given ");
#line 14
 testRunner.When("a new copy image message request body is NSBP-2cbabea0-43f9-4554-b118-c80c487d97c" +
                    "3 with RoutingKey NECL", ((string)(null)), ((TechTalk.SpecFlow.Table)(null)), "When ");
#line 15
 testRunner.Then("a copy image message with id NSBP-2cbabea0-43f9-4554-b118-c80c487d97c3 is sent to" +
                    " the CopyImage Service Exchange with RoutingKey NECL", ((string)(null)), ((TechTalk.SpecFlow.Table)(null)), "Then ");
#line hidden
            this.ScenarioCleanup();
        }
    }
}
#pragma warning restore
#endregion