package de.ips.creactivities.chatbot.process;

import de.ips.creactivities.chatbot.basemocks.DelegateExecutionMockBase;

import java.util.Map;

public class MockDelegateExecution extends DelegateExecutionMockBase {

    private String processInstanceId;

    Map<String, Object> vals;

    public MockDelegateExecution(Map<String, Object> values) {
        this.vals = values;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    @Override
    public Object getVariable(String s) {
        return vals.get(s);
    }

    @Override
    public void setVariable(String s, Object o) {
        vals.put(s, o);
    }

    @Override
    public boolean hasVariable(String s) {
        return vals.containsKey(s);
    }

    @Override
    public String getProcessInstanceId() {
        return processInstanceId;
    }
}

