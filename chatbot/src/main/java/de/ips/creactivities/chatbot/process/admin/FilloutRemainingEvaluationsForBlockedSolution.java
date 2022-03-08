package de.ips.creactivities.chatbot.process.admin;

import de.ips.creactivities.chatbot.ChatbotProperties;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.dm.EvaluationEntity;
import de.ips.creactivities.chatbot.dm.SolutionEntity;
import de.ips.creactivities.chatbot.dm.UserEntity;
import de.ips.creactivities.chatbot.repo.SolutionRepository;
import de.ips.creactivities.chatbot.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class FilloutRemainingEvaluationsForBlockedSolution implements JavaDelegate {

    private SolutionRepository solutionRepository;

    private UserRepository userRepository;

    private ChatbotProperties properties;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        long id = (long) execution.getVariable(IProcessVariables.REPORTED_SOLUTION_ID);
        SolutionEntity solution = solutionRepository.findById(id).get();

        List<EvaluationEntity> evaluations = solution.getEvaluations();
        Optional<UserEntity> user = userRepository.findById("0");

        while (evaluations != null && evaluations.size() < properties.getNumberOfRequiredEvaluations()) {
            EvaluationEntity dummyEval = new EvaluationEntity();
            dummyEval.setEvaluationScore(0);
            if (user.isPresent()) {
                dummyEval.setUser(user.get());
            }
            evaluations.add(dummyEval);
        }

        solutionRepository.save(solution);
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
}
