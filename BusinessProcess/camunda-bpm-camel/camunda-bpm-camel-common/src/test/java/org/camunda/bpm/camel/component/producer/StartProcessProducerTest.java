package org.camunda.bpm.camel.component.producer;

import org.apache.camel.Exchange;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultExchange;
import org.camunda.bpm.camel.BaseCamelTest;
import org.camunda.bpm.camel.component.CamundaBpmEndpoint;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.Test;

import static org.camunda.bpm.camel.component.CamundaBpmConstants.*;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class StartProcessProducerTest extends BaseCamelTest {

  @Test
  public void getStartProcessProducerFromUri() throws Exception {
    CamundaBpmEndpoint endpoint = (CamundaBpmEndpoint) camelContext.getEndpoint(camundaBpmUri("start?" + PROCESS_DEFINITION_KEY_PARAMETER + "=" + "aProcessDefinitionKey"));
    Producer producer = endpoint.createProducer();
    assertThat(producer).isInstanceOf(StartProcessProducer.class);
  }

// No longer valid - The process definition key may be past at execution of the route
//  @Test(expected = IllegalArgumentException.class)
//  public void noProcessDefinitionKeyParameterShouldThrowException() throws Exception {
//    Endpoint endpoint = camelContext.getEndpoint(camundaBpmUri("start"));
//    endpoint.createProducer(); // This triggers the exception
//  }

  @Test
  public void startProcessInstanceByKeyShouldBeCalled() throws Exception {
    ProcessInstance processInstance = mock(ProcessInstance.class);
    when(processInstance.getProcessInstanceId()).thenReturn("theProcessInstanceId");
    when(processInstance.getProcessDefinitionId()).thenReturn("theProcessDefinitionId");
    when(runtimeService.startProcessInstanceByKey(eq("aProcessDefinitionKey"), anyMap())).thenReturn(processInstance);

    CamundaBpmEndpoint endpoint = (CamundaBpmEndpoint) camelContext.getEndpoint(camundaBpmUri("start?" + PROCESS_DEFINITION_KEY_PARAMETER + "=" + "aProcessDefinitionKey"));
    StartProcessProducer producer = (StartProcessProducer) endpoint.createProducer();
    Exchange exchange = new DefaultExchange(camelContext);
    producer.process(exchange);

    verify(runtimeService, times(1)).startProcessInstanceByKey(eq("aProcessDefinitionKey"), anyMap());
    assertThat(exchange.getProperty(CAMUNDA_BPM_PROCESS_DEFINITION_ID)).isEqualTo("theProcessDefinitionId");
    assertThat(exchange.getProperty(CAMUNDA_BPM_PROCESS_INSTANCE_ID)).isEqualTo("theProcessInstanceId");
  }

  @Test
  public void startProcessInstanceByKeyWithBusinessKeyShouldBeCalled() throws Exception {
    ProcessInstance processInstance = mock(ProcessInstance.class);
    when(processInstance.getProcessInstanceId()).thenReturn("theProcessInstanceId");
    when(processInstance.getProcessDefinitionId()).thenReturn("theProcessDefinitionId");
    when(processInstance.getBusinessKey()).thenReturn("aBusinessKey");
    when(runtimeService.startProcessInstanceByKey(eq("aProcessDefinitionKey"), eq("aBusinessKey"), anyMap())).thenReturn(processInstance);

    CamundaBpmEndpoint endpoint = (CamundaBpmEndpoint) camelContext.getEndpoint(camundaBpmUri("start?" + PROCESS_DEFINITION_KEY_PARAMETER + "=" + "aProcessDefinitionKey"));
    StartProcessProducer producer = (StartProcessProducer) endpoint.createProducer();
    Exchange exchange = new DefaultExchange(camelContext);
    exchange.setProperty(CAMUNDA_BPM_BUSINESS_KEY, "aBusinessKey");
    producer.process(exchange);

    verify(runtimeService, times(1)).startProcessInstanceByKey(eq("aProcessDefinitionKey"), eq("aBusinessKey"), anyMap());
    assertThat(exchange.getProperty(CAMUNDA_BPM_PROCESS_DEFINITION_ID)).isEqualTo("theProcessDefinitionId");
    assertThat(exchange.getProperty(CAMUNDA_BPM_PROCESS_INSTANCE_ID)).isEqualTo("theProcessInstanceId");
    assertThat(exchange.getProperty(CAMUNDA_BPM_BUSINESS_KEY)).isEqualTo("aBusinessKey");
  }
}
