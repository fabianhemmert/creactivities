package de.ips.creactivities.chatbot.process.admin;

import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.dm.SolutionEntity;
import de.ips.creactivities.chatbot.repo.SolutionRepository;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SetSolutionBlockStateDelegate implements JavaDelegate {

    private SolutionRepository solutionRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
       String blockState = (String) execution.getVariableLocal("blockState");
       long reportedSolution = (long) execution.getVariable(IProcessVariables.REPORTED_SOLUTION_ID);

        SolutionEntity sol = solutionRepository.findById(reportedSolution).get();
        sol.setBlocked("true".equals(blockState));
        solutionRepository.save(sol);
    }

    @Autowired
    public void setSolutionRepository(SolutionRepository solutionRepository) {
        this.solutionRepository = solutionRepository;
    }
}
