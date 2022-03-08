package de.ips.creactivities.chatbot.process;

import de.ips.creactivities.chatbot.basemocks.RuntimeServiceMockBase;
import org.camunda.bpm.engine.runtime.MessageCorrelationBuilder;
import org.camunda.bpm.engine.runtime.MessageCorrelationResult;
import org.camunda.bpm.engine.runtime.MessageCorrelationResultWithVariables;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MockCamundaRuntimeService extends RuntimeServiceMockBase {

    private Map<String, Object> processVariables = new HashMap<>();

    private List<String> correlatedMessages = new LinkedList<>();

    private MessageCorrelationBuilder mockMessageCorrelationBuilder;

    public MockCamundaRuntimeService() {
        mockMessageCorrelationBuilder = new MockMessageCorrelationBuilder();
    }

    public Map<String, Object> getProcessVariables() {
        return processVariables;
    }

    public List<String> getCorrelatedMessages() {
        return correlatedMessages;
    }

    @Override
    public void setVariable(String executionId, String variableName, Object value) {
        processVariables.put(variableName, value);
    }


    @Override
    public void setVariables(String executionId, Map<String, ?> variables) {
        processVariables.putAll(variables);
    }

    @Override
    public MessageCorrelationBuilder createMessageCorrelation(String messageName) {
        this.correlatedMessages.add(messageName);
        return mockMessageCorrelationBuilder;
    }

    public class MockMessageCorrelationBuilder implements MessageCorrelationBuilder {


        @Override
        public MessageCorrelationBuilder processInstanceBusinessKey(String businessKey) {
            return null;
        }

        @Override
        public MessageCorrelationBuilder processInstanceVariableEquals(String variableName, Object variableValue) {
            return null;
        }

        @Override
        public MessageCorrelationBuilder processInstanceVariablesEqual(Map<String, Object> variables) {
            return null;
        }

        @Override
        public MessageCorrelationBuilder localVariableEquals(String variableName, Object variableValue) {
            return null;
        }

        @Override
        public MessageCorrelationBuilder localVariablesEqual(Map<String, Object> variables) {
            return null;
        }

        @Override
        public MessageCorrelationBuilder processInstanceId(String id) {

            return this;
        }

        @Override
        public MessageCorrelationBuilder processDefinitionId(String processDefinitionId) {
            return null;
        }

        @Override
        public MessageCorrelationBuilder setVariable(String variableName, Object variableValue) {
            return null;
        }

        @Override
        public MessageCorrelationBuilder setVariableLocal(String variableName, Object variableValue) {
            return null;
        }

        @Override
        public MessageCorrelationBuilder setVariables(Map<String, Object> variables) {
            return null;
        }

        @Override
        public MessageCorrelationBuilder setVariablesLocal(Map<String, Object> variables) {
            return null;
        }

        @Override
        public MessageCorrelationBuilder tenantId(String tenantId) {
            return null;
        }

        @Override
        public MessageCorrelationBuilder withoutTenantId() {
            return null;
        }

        @Override
        public MessageCorrelationBuilder startMessageOnly() {
            return null;
        }

        @Override
        public void correlate() {

        }

        @Override
        public MessageCorrelationResult correlateWithResult() {
            return null;
        }

        @Override
        public MessageCorrelationResultWithVariables correlateWithResultAndVariables(boolean deserializeValues) {
            return null;
        }

        @Override
        public void correlateExclusively() {

        }

        @Override
        public void correlateAll() {

        }

        @Override
        public List<MessageCorrelationResult> correlateAllWithResult() {
            return null;
        }

        @Override
        public List<MessageCorrelationResultWithVariables> correlateAllWithResultAndVariables(boolean deserializeValues) {
            return null;
        }

        @Override
        public ProcessInstance correlateStartMessage() {
            return null;
        }
    }

}
