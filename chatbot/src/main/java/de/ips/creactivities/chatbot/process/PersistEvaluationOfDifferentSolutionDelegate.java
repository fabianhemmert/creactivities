package de.ips.creactivities.chatbot.process;

import de.ips.creactivities.chatbot.constants.IProcessKeys;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.dm.SolutionEntity;
import de.ips.creactivities.chatbot.repo.SolutionRepository;
import de.ips.creactivities.chatbot.telegram.EvaluationHandling;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class PersistEvaluationOfDifferentSolutionDelegate implements JavaDelegate {

    private SolutionRepository solutionRepository;
    private EvaluationHandling evaluationhandling;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        long evaluatedSolutionid = (long) execution.getVariable(IProcessVariables.DIFFERENT_SOLUTION_ID_TO_EVALUATE);
        String score = (String) execution.getVariable(IProcessVariables.SOLUTION_SCORE);
        String userId = (String) execution.getVariable(IProcessVariables.USER_ID);

        if (-1 == evaluatedSolutionid) {
            // The user evaluated a template solution. We do not need to persist this.
            return;
        }

        Optional<SolutionEntity> entity = solutionRepository.findById(evaluatedSolutionid);

        if (entity.isPresent()) {

            if ("0".equals(score)) {
                RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
                Map<String, Object> variables = new HashMap<>();
                variables.put(IProcessVariables.USER_ID, entity.get().getUser().getId());
                variables.put(IProcessVariables.REPORTED_SOLUTION_ID, entity.get().getId());
                runtimeService.startProcessInstanceByKey(IProcessKeys.adminEvaluationProcess, variables);
            } else {
                evaluationhandling.commitEvaluationToSolution(score, userId, entity);
            }
        } else {
            log.error("Solution with id '" + evaluatedSolutionid + "' not found.");
            throw new IllegalStateException();
        }

    }


    @Autowired
    public void setSolutionRepository(SolutionRepository solutionRepository) {
        this.solutionRepository = solutionRepository;
    }

    @Autowired
    public void setEvaluationhandling(EvaluationHandling evaluationhandling) {
        this.evaluationhandling = evaluationhandling;
    }

}
