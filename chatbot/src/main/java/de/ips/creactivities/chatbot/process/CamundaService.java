package de.ips.creactivities.chatbot.process;

import de.ips.creactivities.chatbot.constants.IProcessKeys;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.MismatchingMessageCorrelationException;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CamundaService {

    public RuntimeService runtimeService;

    @Autowired
    public void setRuntimeService(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    public RuntimeService getRuntimeService() {
        return runtimeService;
    }


    public ProcessInstance getProcessInstance(String processInstanceId) {
        return runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    }

    public Map<String, Object> handleCamundaEvent(String messageKey, String processInstanceId, String processVariableData) {

        // processvariable can be "" or "_<process variable name>_<process variable value>"
        Map<String, Object> variableMap = extractVariables(processVariableData);

        handleCamundaEvent(messageKey, processInstanceId, variableMap);

        return variableMap;
    }

    public Map<String, Object> extractVariables(String processVariableData) {
        Map<String, Object> variableMap = new HashMap<>();
        if (processVariableData != null && !processVariableData.isEmpty()) {
            String variableData = processVariableData.substring(1);

            // Currently we only support 1 variable here
            String[] split = variableData.split("_");

            for (int i = 0; i < split.length; i = i + 2) {
                String variableName = split[i];
                String variableValue = split[i + 1];
                variableMap.put(variableName, variableValue);
            }
        }
        return variableMap;
    }

    public void handleCamundaEvent(String messageKey, String processInstanceId, Map<String, Object> variableMap) {

        if (variableMap != null && !variableMap.isEmpty()) {
            runtimeService.setVariables(processInstanceId, variableMap);
        }
        try {
            log.info("Sending Event '" + messageKey + "' to Camunda.");
            runtimeService.createMessageCorrelation(messageKey).processInstanceId(processInstanceId).correlate();
        } catch (MismatchingMessageCorrelationException e) {
            log.info("Received an Event that is already completed in the process.");
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void deleteProcessInstances(String userId) {

        ProcessInstance instance = runtimeService.createProcessInstanceQuery().processDefinitionKey(IProcessKeys.userFlow).processInstanceBusinessKey(userId).singleResult();
        if (instance != null ){
            runtimeService.deleteProcessInstance(instance.getProcessInstanceId(), "/deleteme command");
        }
    }

    public ProcessInstance findActiveLevelProcessInstance(String userId) {
        return runtimeService.createProcessInstanceQuery().processDefinitionKey(IProcessKeys.levelProcess).variableValueEquals(IProcessVariables.USER_ID, userId).singleResult();
    }

    public ProcessInstance findActiveChallengeProcessInstance(String userId) {
        return runtimeService.createProcessInstanceQuery().processDefinitionKey(IProcessKeys.challengeProcess).variableValueEquals(IProcessVariables.USER_ID, userId).singleResult();
    }
}
