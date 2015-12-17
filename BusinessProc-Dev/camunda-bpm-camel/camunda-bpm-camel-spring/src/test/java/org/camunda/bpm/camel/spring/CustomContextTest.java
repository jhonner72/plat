/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.camunda.bpm.camel.spring;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineTestCase;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.Map;
import static org.camunda.bpm.camel.component.CamundaBpmConstants.*;

@ContextConfiguration("classpath:custom-camel-activiti-context.xml")
public class CustomContextTest extends ProcessEngineTestCase {

  MockEndpoint service1;

  MockEndpoint service2;

    @Autowired
    CamelContext ctx;

  public void setUp() {

    //CamelContext ctx = applicationContext.getBean(CamelContext.class);
    service1 = (MockEndpoint) ctx.getEndpoint("mock:service1");
    service1.reset();
    service2 = (MockEndpoint) ctx.getEndpoint("mock:service2");
    service2.reset();

  }

  @Deployment(resources = {"process/custom.bpmn20.xml"})
  public void testRunProcess() throws Exception {
    //CamelContext ctx = applicationContext.getBean(CamelContext.class);
    ProducerTemplate tpl = ctx.createProducerTemplate();
    service1.expectedBodiesReceived("ala");

    String instanceId = (String) tpl.requestBody("direct:start", Collections.singletonMap("var1", "ala"));


    tpl.sendBodyAndProperty("direct:receive", null, CAMUNDA_BPM_PROCESS_INSTANCE_ID, instanceId);

    assertProcessEnded(instanceId);

    service1.assertIsSatisfied();
    Map m = service2.getExchanges().get(0).getIn().getBody(Map.class);
    assertEquals("ala", m.get("var1"));
    assertEquals("var2", m.get("var2"));

  }
}
