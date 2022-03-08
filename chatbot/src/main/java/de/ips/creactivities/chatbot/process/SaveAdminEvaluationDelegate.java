package de.ips.creactivities.chatbot.process;

import de.ips.creactivities.chatbot.ChatbotProperties;
import de.ips.creactivities.chatbot.constants.IProcessKeys;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.dm.EvaluationEntity;
import de.ips.creactivities.chatbot.dm.EvaluationType;
import de.ips.creactivities.chatbot.dm.SolutionEntity;
import de.ips.creactivities.chatbot.dm.UserEntity;
import de.ips.creactivities.chatbot.repo.SolutionRepository;
import de.ips.creactivities.chatbot.repo.UserRepository;
import de.ips.creactivities.chatbot.telegram.EvaluationHandling;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class SaveAdminEvaluationDelegate implements JavaDelegate {

    private SolutionRepository solutionRepository;
    private UserRepository userRepository;
    private EvaluationHandling evaluationHandling;

    private ChatbotProperties properties;

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        String userId = (String) execution.getVariable(IProcessVariables.USER_ID);
        long solutionId = (long) execution.getVariable(IProcessVariables.SOLUTION_ID);
        String value = (String) execution.getVariable(IProcessVariables.ADMIN_EVAL_VALUE);

        SolutionEntity solution = solutionRepository.findById(solutionId).get();

        List<EvaluationEntity> evaluations = solution.getEvaluations();
        Optional<UserEntity> user = userRepository.findById("0");

        if ("0".equals(value)) { // Solution was reported
            RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
            Map<String, Object> variables = new HashMap<>();
            variables.put(IProcessVariables.USER_ID, userId);
            variables.put(IProcessVariables.REPORTED_SOLUTION_ID, solutionId);
            runtimeService.startProcessInstanceByKey(IProcessKeys.adminEvaluationProcess, variables);
            return;
        }

        for(int i = (evaluations != null ? evaluations.size() : 0); i <= properties.getNumberOfRequiredEvaluations(); i++) {
            evaluationHandling.commitEvaluationToSolution(value,"0", Optional.of(solution));
        }
    }

    @Autowired
    public void setSolutionRepository(SolutionRepository solutionRepository) {
        this.solutionRepository = solutionRepository;
    }

    @Autowired
    public void setProperties(ChatbotProperties properties) {
        this.properties = properties;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setEvaluationHandling(EvaluationHandling evaluationHandling ) {
        this.evaluationHandling = evaluationHandling;
    }
}
