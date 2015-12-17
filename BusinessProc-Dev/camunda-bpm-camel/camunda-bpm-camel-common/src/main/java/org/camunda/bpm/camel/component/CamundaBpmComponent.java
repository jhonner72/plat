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
package org.camunda.bpm.camel.component;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.camunda.bpm.engine.ProcessEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * camunda BPM Apache Camel component
 * 
 * @author Ryan Johnston (@rjfsu)
 * @author Tijs Rademakers (@tijsrademakers)
 * @author Rafael Cordones (@rafacm)
 */
public class CamundaBpmComponent extends DefaultComponent {

  final Logger log = LoggerFactory.getLogger(CamundaBpmComponent.class);

  protected ProcessEngine processEngine;

  public CamundaBpmComponent() {
  }

  public CamundaBpmComponent(ProcessEngine processEngine) {
    super();
    this.processEngine = processEngine;
  }

  @Override
  protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
    return new CamundaBpmEndpointDefaultImpl(uri, this, parameters);
  }

  public ProcessEngine getProcessEngine() {
    return this.processEngine;
  }

  public void setProcessEngine(ProcessEngine processEngine) {
    this.processEngine = processEngine;
  }
}
