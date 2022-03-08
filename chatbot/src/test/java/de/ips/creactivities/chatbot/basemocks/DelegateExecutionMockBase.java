package de.ips.creactivities.chatbot.basemocks;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineServices;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.runtime.Incident;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowElement;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public abstract class DelegateExecutionMockBase implements DelegateExecution {
    @Override
    public String getProcessInstanceId() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public String getProcessBusinessKey() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public void setProcessBusinessKey(String s) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public String getProcessDefinitionId() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public String getParentId() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public String getCurrentActivityId() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public String getCurrentActivityName() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public String getActivityInstanceId() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public String getParentActivityInstanceId() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public String getCurrentTransitionId() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public DelegateExecution getProcessInstance() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public DelegateExecution getSuperExecution() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public boolean isCanceled() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public String getTenantId() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public void setVariable(String s, Object o, String s1) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public Incident createIncident(String s, String s1) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public Incident createIncident(String s, String s1, String s2) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public void resolveIncident(String s) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public String getId() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public String getEventName() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public String getBusinessKey() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public BpmnModelInstance getBpmnModelInstance() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public FlowElement getBpmnModelElementInstance() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public ProcessEngineServices getProcessEngineServices() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public ProcessEngine getProcessEngine() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public String getVariableScopeKey() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public Map<String, Object> getVariables() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public VariableMap getVariablesTyped() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public VariableMap getVariablesTyped(boolean b) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public Map<String, Object> getVariablesLocal() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public VariableMap getVariablesLocalTyped() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public VariableMap getVariablesLocalTyped(boolean b) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public Object getVariable(String s) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public Object getVariableLocal(String s) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public <T extends TypedValue> T getVariableTyped(String s) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public <T extends TypedValue> T getVariableTyped(String s, boolean b) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public <T extends TypedValue> T getVariableLocalTyped(String s) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public <T extends TypedValue> T getVariableLocalTyped(String s, boolean b) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public Set<String> getVariableNames() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public Set<String> getVariableNamesLocal() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public void setVariable(String s, Object o) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public void setVariableLocal(String s, Object o) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public void setVariables(Map<String, ?> map) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public void setVariablesLocal(Map<String, ?> map) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public boolean hasVariables() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public boolean hasVariablesLocal() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public boolean hasVariable(String s) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public boolean hasVariableLocal(String s) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public void removeVariable(String s) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public void removeVariableLocal(String s) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public void removeVariables(Collection<String> collection) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public void removeVariablesLocal(Collection<String> collection) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public void removeVariables() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public void removeVariablesLocal() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }
}
