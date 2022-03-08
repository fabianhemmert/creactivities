package de.ips.creactivities.chatbot.telegram;

import de.ips.creactivities.chatbot.cms.ICmsService;
import de.ips.creactivities.chatbot.cms.dm.Challenge;
import de.ips.creactivities.chatbot.dm.*;
import de.ips.creactivities.chatbot.repo.SolutionRepository;
import de.ips.creactivities.chatbot.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EvaluationHandling {

    private final SolutionRepository solutionRepository;
    private final UserRepository userRepository;
    private final ICmsService cmsService;

    public void commitEvaluationToSolution(String score, String userId, Optional<SolutionEntity> entity) {
        SolutionEntity solution = entity.get();
        UserEntity user = userRepository.findById(userId).get();

        if (user == null) {
            Optional<UserEntity> dummyUser = userRepository.findById("0");
            if (dummyUser.isEmpty()) {
                // we need to add the dummy user so we can hang solutions there (solutions without a user cause issues in other parts of the code!!!)
                UserEntity dummyUserEntity = new UserEntity("0", true, UserState.IDLE, null, "0", "de", null, null, false, null, null);
                user = userRepository.save(dummyUserEntity);
            } else {
                user = dummyUser.get();
            }
        }

        EvaluationType evaluationType = EvaluationType.MACHEN;
        if(!"0".equals(solution.getUser().getId())) {
            Challenge c = cmsService.findChallengeById(solution.getUser().getLanguageId(), solution.getChallenge().getId()).get();
            evaluationType = EvaluationType.findByCmsString(c.getEvaluationAspect());
        }


        List<EvaluationEntity> evaluations = solution.getEvaluations() != null ? solution.getEvaluations() : Collections.emptyList();
        EvaluationEntity evaluation = new EvaluationEntity();
        evaluation.setUser(user);
        evaluation.setEvaluationType(evaluationType);
        evaluation.setIssuedOn(System.currentTimeMillis());
        evaluation.setEvaluationScore(Integer.valueOf(score));
        evaluations.add(evaluation);
        solution.setEvaluations(evaluations);
        solutionRepository.save(solution);
    }
}
