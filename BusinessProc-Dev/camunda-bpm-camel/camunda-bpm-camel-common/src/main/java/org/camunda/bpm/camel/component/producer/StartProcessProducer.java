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
package org.camunda.bpm.camel.component.producer;

import org.apache.camel.Exchange;
import org.camunda.bpm.camel.common.ExchangeUtils;
import org.camunda.bpm.camel.component.CamundaBpmEndpoint;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import java.util.HashMap;
import java.util.Map;

import static org.camunda.bpm.camel.component.CamundaBpmConstants.*;

/**
 * Starts a process instance given a process definition key.
 * <p/>
 * Example: camunda-bpm://start?processDefinitionKey=aProcessDefinitionKey
 *
 * @author Ryan Johnston (@rjfsu)
 * @author Tijs Rademakers (@tijsrademakers)
 * @author Rafael Cordones (@rafacm)
 * @author Bernd Ruecker
 */
public class StartProcessProducer extends CamundaBpmProducer {

  private final String processDefinitionKey;

  public StartProcessProducer(CamundaBpmEndpoint endpoint, Map<String, Object> parameters) {
    super(endpoint, parameters);

    if (parameters.containsKey(PROCESS_DEFINITION_KEY_PARAMETER)) {
      this.processDefinitionKey = (String) parameters.get(PROCESS_DEFINITION_KEY_PARAMETER);
    } else {
        processDefinitionKey = null;
      // throw new IllegalArgumentException("You need to pass the '" + PROCESS_DEFINITION_KEY_PARAMETER + "' parameter! Parameters received: " + parameters);
    }
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    Map<String, Object> processVariables = new HashMap<String, Object>();
    if (parameters.containsKey(COPY_MESSAGE_PROPERTIES_PARAMETER)) {
      processVariables.putAll(exchange.getProperties());
    }
    if (parameters.containsKey(COPY_MESSAGE_HEADERS_PARAMETER)) {
      processVariables.putAll(exchange.getIn().getHeaders());
    }

    processVariables.putAll(ExchangeUtils.prepareVariables(exchange, parameters));

    /*
     * If the exchange contains the CAMUNDA_BPM_BUSINESS_KEY then we pass it to the engine
     */
    String processDefinitionKey = this.processDefinitionKey != null ? this.processDefinitionKey : exchange.getIn().getHeader(CAMUNDA_BPM_PROCESS_DEFINITION_KEY, String.class);

    ProcessInstance instance = null;
    if (exchange.getProperties().containsKey(CAMUNDA_BPM_BUSINESS_KEY)) {
      instance = runtimeService.startProcessInstanceByKey(processDefinitionKey,
                                                          exchange.getProperty(CAMUNDA_BPM_BUSINESS_KEY, String.class),
                                                          processVariables);
      exchange.setProperty(CAMUNDA_BPM_BUSINESS_KEY, instance.getBusinessKey());
    } else {
      instance = runtimeService.startProcessInstanceByKey(processDefinitionKey, processVariables);
    }

    exchange.setProperty(CAMUNDA_BPM_PROCESS_DEFINITION_ID, instance.getProcessDefinitionId());
    exchange.setProperty(CAMUNDA_BPM_PROCESS_INSTANCE_ID, instance.getProcessInstanceId());
    exchange.getOut().setBody(instance.getProcessInstanceId());
  }

}