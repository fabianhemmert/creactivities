package de.ips.creactivities.chatbot.basemocks;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.batch.Batch;
import org.camunda.bpm.engine.history.HistoricProcessInstanceQuery;
import org.camunda.bpm.engine.migration.MigrationPlan;
import org.camunda.bpm.engine.migration.MigrationPlanBuilder;
import org.camunda.bpm.engine.migration.MigrationPlanExecutionBuilder;
import org.camunda.bpm.engine.runtime.*;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.value.TypedValue;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class RuntimeServiceMockBase implements RuntimeService {
    @Override
    public ProcessInstance startProcessInstanceByKey(String processDefinitionKey) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public ProcessInstance startProcessInstanceByKey(String processDefinitionKey, String businessKey) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public ProcessInstance startProcessInstanceByKey(String processDefinitionKey, String businessKey, String caseInstanceId) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public ProcessInstance startProcessInstanceByKey(String processDefinitionKey, Map<String, Object> variables) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public ProcessInstance startProcessInstanceByKey(String processDefinitionKey, String businessKey, Map<String, Object> variables) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public ProcessInstance startProcessInstanceByKey(String processDefinitionKey, String businessKey, String caseInstanceId, Map<String, Object> variables) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public ProcessInstance startProcessInstanceById(String processDefinitionId) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public ProcessInstance startProcessInstanceById(String processDefinitionId, String businessKey) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public ProcessInstance startProcessInstanceById(String processDefinitionId, String businessKey, String caseInstanceId) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public ProcessInstance startProcessInstanceById(String processDefinitionId, Map<String, Object> variables) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public ProcessInstance startProcessInstanceById(String processDefinitionId, String businessKey, Map<String, Object> variables) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public ProcessInstance startProcessInstanceById(String processDefinitionId, String businessKey, String caseInstanceId, Map<String, Object> variables) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public ProcessInstance startProcessInstanceByMessage(String messageName) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public ProcessInstance startProcessInstanceByMessage(String messageName, String businessKey) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public ProcessInstance startProcessInstanceByMessage(String messageName, Map<String, Object> processVariables) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public ProcessInstance startProcessInstanceByMessage(String messageName, String businessKey, Map<String, Object> processVariables) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public ProcessInstance startProcessInstanceByMessageAndProcessDefinitionId(String messageName, String processDefinitionId) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public ProcessInstance startProcessInstanceByMessageAndProcessDefinitionId(String messageName, String processDefinitionId, String businessKey) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public ProcessInstance startProcessInstanceByMessageAndProcessDefinitionId(String messageName, String processDefinitionId, Map<String, Object> processVariables) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public ProcessInstance startProcessInstanceByMessageAndProcessDefinitionId(String messageName, String processDefinitionId, String businessKey, Map<String, Object> processVariables) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void deleteProcessInstance(String processInstanceId, String deleteReason) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public Batch deleteProcessInstancesAsync(List<String> processInstanceIds, ProcessInstanceQuery processInstanceQuery, String deleteReason) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public Batch deleteProcessInstancesAsync(List<String> processInstanceIds, ProcessInstanceQuery processInstanceQuery, String deleteReason, boolean skipCustomListeners) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public Batch deleteProcessInstancesAsync(List<String> processInstanceIds, ProcessInstanceQuery processInstanceQuery, String deleteReason, boolean skipCustomListeners, boolean skipSubprocesses) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public Batch deleteProcessInstancesAsync(List<String> processInstanceIds, ProcessInstanceQuery processInstanceQuery, HistoricProcessInstanceQuery historicProcessInstanceQuery, String deleteReason, boolean skipCustomListeners, boolean skipSubprocesses) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public Batch deleteProcessInstancesAsync(ProcessInstanceQuery processInstanceQuery, String deleteReason) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public Batch deleteProcessInstancesAsync(List<String> processInstanceIds, String deleteReason) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void deleteProcessInstance(String processInstanceId, String deleteReason, boolean skipCustomListeners) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void deleteProcessInstance(String processInstanceId, String deleteReason, boolean skipCustomListeners, boolean externallyTerminated) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void deleteProcessInstances(List<String> processInstanceIds, String deleteReason, boolean skipCustomListeners, boolean externallyTerminated) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void deleteProcessInstances(List<String> processInstanceIds, String deleteReason, boolean skipCustomListeners, boolean externallyTerminated, boolean skipSubprocesses) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void deleteProcessInstancesIfExists(List<String> processInstanceIds, String deleteReason, boolean skipCustomListeners, boolean externallyTerminated, boolean skipSubprocesses) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void deleteProcessInstance(String processInstanceId, String deleteReason, boolean skipCustomListeners, boolean externallyTerminated, boolean skipIoMappings) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void deleteProcessInstance(String processInstanceId, String deleteReason, boolean skipCustomListeners, boolean externallyTerminated, boolean skipIoMappings, boolean skipSubprocesses) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void deleteProcessInstanceIfExists(String processInstanceId, String deleteReason, boolean skipCustomListeners, boolean externallyTerminated, boolean skipIoMappings, boolean skipSubprocesses) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public List<String> getActiveActivityIds(String executionId) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public ActivityInstance getActivityInstance(String processInstanceId) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void signal(String executionId) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void signal(String executionId, String signalName, Object signalData, Map<String, Object> processVariables) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void signal(String executionId, Map<String, Object> processVariables) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public Map<String, Object> getVariables(String executionId) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public VariableMap getVariablesTyped(String executionId) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public VariableMap getVariablesTyped(String executionId, boolean deserializeValues) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public Map<String, Object> getVariablesLocal(String executionId) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public VariableMap getVariablesLocalTyped(String executionId) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public VariableMap getVariablesLocalTyped(String executionId, boolean deserializeValues) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public Map<String, Object> getVariables(String executionId, Collection<String> variableNames) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public VariableMap getVariablesTyped(String executionId, Collection<String> variableNames, boolean deserializeValues) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public Map<String, Object> getVariablesLocal(String executionId, Collection<String> variableNames) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public VariableMap getVariablesLocalTyped(String executionId, Collection<String> variableNames, boolean deserializeValues) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public Object getVariable(String executionId, String variableName) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public <T extends TypedValue> T getVariableTyped(String executionId, String variableName) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public <T extends TypedValue> T getVariableTyped(String executionId, String variableName, boolean deserializeValue) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public Object getVariableLocal(String executionId, String variableName) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public <T extends TypedValue> T getVariableLocalTyped(String executionId, String variableName) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public <T extends TypedValue> T getVariableLocalTyped(String executionId, String variableName, boolean deserializeValue) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void setVariable(String executionId, String variableName, Object value) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void setVariableLocal(String executionId, String variableName, Object value) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void setVariables(String executionId, Map<String, ?> variables) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void setVariablesLocal(String executionId, Map<String, ?> variables) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public Batch setVariablesAsync(List<String> processInstanceIds, ProcessInstanceQuery processInstanceQuery, HistoricProcessInstanceQuery historicProcessInstanceQuery, Map<String, ?> variables) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public Batch setVariablesAsync(List<String> processInstanceIds, Map<String, ?> variables) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public Batch setVariablesAsync(ProcessInstanceQuery processInstanceQuery, Map<String, ?> variables) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public Batch setVariablesAsync(HistoricProcessInstanceQuery historicProcessInstanceQuery, Map<String, ?> variables) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void removeVariable(String executionId, String variableName) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void removeVariableLocal(String executionId, String variableName) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void removeVariables(String executionId, Collection<String> variableNames) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void removeVariablesLocal(String executionId, Collection<String> variableNames) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public ExecutionQuery createExecutionQuery() {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public NativeExecutionQuery createNativeExecutionQuery() {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public ProcessInstanceQuery createProcessInstanceQuery() {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public NativeProcessInstanceQuery createNativeProcessInstanceQuery() {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public IncidentQuery createIncidentQuery() {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public EventSubscriptionQuery createEventSubscriptionQuery() {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public VariableInstanceQuery createVariableInstanceQuery() {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void suspendProcessInstanceById(String processInstanceId) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void suspendProcessInstanceByProcessDefinitionId(String processDefinitionId) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void suspendProcessInstanceByProcessDefinitionKey(String processDefinitionKey) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void activateProcessInstanceById(String processInstanceId) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void activateProcessInstanceByProcessDefinitionId(String processDefinitionId) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void activateProcessInstanceByProcessDefinitionKey(String processDefinitionKey) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public UpdateProcessInstanceSuspensionStateSelectBuilder updateProcessInstanceSuspensionState() {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void signalEventReceived(String signalName) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void signalEventReceived(String signalName, Map<String, Object> processVariables) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void signalEventReceived(String signalName, String executionId) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void signalEventReceived(String signalName, String executionId, Map<String, Object> processVariables) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public SignalEventReceivedBuilder createSignalEvent(String signalName) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void messageEventReceived(String messageName, String executionId) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void messageEventReceived(String messageName, String executionId, Map<String, Object> processVariables) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public MessageCorrelationBuilder createMessageCorrelation(String messageName) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void correlateMessage(String messageName) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void correlateMessage(String messageName, String businessKey) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void correlateMessage(String messageName, Map<String, Object> correlationKeys) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void correlateMessage(String messageName, String businessKey, Map<String, Object> processVariables) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void correlateMessage(String messageName, Map<String, Object> correlationKeys, Map<String, Object> processVariables) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void correlateMessage(String messageName, String businessKey, Map<String, Object> correlationKeys, Map<String, Object> processVariables) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public MessageCorrelationAsyncBuilder createMessageCorrelationAsync(String messageName) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public ProcessInstanceModificationBuilder createProcessInstanceModification(String processInstanceId) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public ProcessInstantiationBuilder createProcessInstanceById(String processDefinitionId) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public ProcessInstantiationBuilder createProcessInstanceByKey(String processDefinitionKey) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public MigrationPlanBuilder createMigrationPlan(String sourceProcessDefinitionId, String targetProcessDefinitionId) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public MigrationPlanExecutionBuilder newMigration(MigrationPlan migrationPlan) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public ModificationBuilder createModification(String processDefinitionId) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public RestartProcessInstanceBuilder restartProcessInstances(String processDefinitionId) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public Incident createIncident(String incidentType, String executionId, String configuration) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public Incident createIncident(String incidentType, String executionId, String configuration, String message) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void resolveIncident(String incidentId) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void setAnnotationForIncidentById(String incidentId, String annotation) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public void clearAnnotationForIncidentById(String incidentId) {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }

    @Override
    public ConditionEvaluationBuilder createConditionEvaluation() {
        throw new NoSuchMethodError("Not implemented in base mock.");
    }
}
