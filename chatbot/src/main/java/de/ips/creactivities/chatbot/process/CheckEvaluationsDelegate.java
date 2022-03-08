package de.ips.creactivities.chatbot.process;

import de.ips.creactivities.chatbot.ChatbotProperties;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.dm.EvaluationEntity;
import de.ips.creactivities.chatbot.dm.SolutionEntity;
import de.ips.creactivities.chatbot.repo.SolutionRepository;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class CheckEvaluationsDelegate implements JavaDelegate {

    private SolutionRepository solutionRepository;

    private ChatbotProperties properties;

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        long solutionId = (long) execution.getVariable(IProcessVariables.SOLUTION_ID);

        Optional<SolutionEntity> solution = solutionRepository.findById(solutionId);

        if (solution.isPresent()) {

            int evalLoopCounter = 0;

            if (execution.hasVariable(IProcessVariables.EVALUATION_LOOP_COUNTER)) {
                evalLoopCounter = (int) execution.getVariable(IProcessVariables.EVALUATION_LOOP_COUNTER);
                evalLoopCounter++;
            }

            List<EvaluationEntity> evaluations = solution.get().getEvaluations();

            if (evaluations != null && evaluations.size() >= properties.getNumberOfRequiredEvaluations()) {
                execution.setVariable(IProcessVariables.ENOUGH_EVALUATIONS, 1);
            } else if (maxWaitTimeReached(evalLoopCounter)) {
                execution.setVariable(IProcessVariables.ENOUGH_EVALUATIONS, 2);
            } else {
                execution.setVariable(IProcessVariables.ENOUGH_EVALUATIONS, 0);
            }
            execution.setVariable(IProcessVariables.EVALUATION_LOOP_COUNTER, evalLoopCounter);
        } else {
            log.error("Solution with id '" + solutionId + "' not found.");
            throw new IllegalStateException();
        }


    }

    /**
     * Compares the current waiting time against the configured max evaluations wait time in seconds.
     * The current wait time is the loop counter multiplied by 10 (because we wait 10 seconds with every loop in the process).
     *
     * @param loopCount loop counter of the current process instance.
     * @return whether the maximum waiting time is reached or not.
     */
    private boolean maxWaitTimeReached(int loopCount) {
        int currentWaitTime = loopCount * 10;
        return currentWaitTime >= properties.getEvaluationsMaxWaitTime() * 60;

    }

    @Autowired
    public void setSolutionRepository(SolutionRepository solutionRepository) {
        this.solutionRepository = solutionRepository;
    }

    @Autowired
    public void setProperties(ChatbotProperties properties) {
        this.properties = properties;
    }
}
