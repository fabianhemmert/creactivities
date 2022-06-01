package de.ips.creactivities.chatbot.process;

import de.ips.creactivities.chatbot.ChatbotProperties;
import de.ips.creactivities.chatbot.cms.ICmsService;
import de.ips.creactivities.chatbot.cms.dm.Challenge;
import de.ips.creactivities.chatbot.cms.dm.ThirdPartyEvaluation;
import de.ips.creactivities.chatbot.constants.IMessageEvents;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.dm.ChallengeEntity;
import de.ips.creactivities.chatbot.dm.EvaluationEntity;
import de.ips.creactivities.chatbot.dm.SolutionEntity;
import de.ips.creactivities.chatbot.dm.UserEntity;
import de.ips.creactivities.chatbot.repo.SolutionRepository;
import de.ips.creactivities.chatbot.repo.UserRepository;
import de.ips.creactivities.chatbot.telegram.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/*
 * Schicke dem User eine andere Lösung mit folgenden Kriterien:
 * 1.)  Die Lösung darf nicht die eigene Lösung vom Benutzer sein.
 * 2.) Der Benutzer muss die Challenge schon bearbeitet haben
 * 3.) Von diesem benutzer darf die Lösung noch nicht bewertet worden sein
 * 3.) Die Challenge hat noch weniger als "numberOfRequiredEvaluations" Bewertungen
 *   3.a) Wenn es keine gibt: Nimm eine Moderator Lösung, die der Benutzer noch nicht bewertet hat
 *
 */
@Slf4j
@Component
public class SendDifferentSolutionToUserDelegate implements JavaDelegate {

    private UserRepository userRepository;

    private SolutionRepository solutionRepository;

    private MessageSender messageSender;

    private ChatbotProperties config;

    private ICmsService cmsService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        String userId = (String) execution.getVariable(IProcessVariables.USER_ID);
        String challengeId = (String) execution.getVariable(IProcessVariables.CHALLENGE_ID);

        // Workaround because of a bug in the persistence (see ChatbotApplicationTests)
        Iterable<UserEntity> users = userRepository.findAllById(Arrays.asList(userId));
        UserEntity user = users.iterator().next();


        Challenge currentChallenge = cmsService.findChallengeById(user.getLanguageId(), challengeId).get();

        if (hasThirdPartyEvaluation(currentChallenge)) {
            Integer thirdPartyEvaluationIndex = (Integer) execution.getVariable(IProcessVariables.THIRD_PARTY_EVALUATION_INDEX);

            // FIRST EVALUATION
            if (thirdPartyEvaluationIndex == null) {
                messageSender.sendTextMessage(userId, null, currentChallenge.getThirdpartyEvaluation().getInitialThanks());
                Thread.sleep(3000);
                messageSender.sendTextMessage(userId, null, currentChallenge.getThirdpartyEvaluation().getEvaluationCallToAction());
                thirdPartyEvaluationIndex = 0;
                execution.setVariable(IProcessVariables.END_OF_THIRD_PARTY_EVALUATIONS, !hasTransitions(currentChallenge.getThirdpartyEvaluation()));
            } else if (hasTransitions(currentChallenge.getThirdpartyEvaluation())) {
                List<String> transitions = currentChallenge.getThirdpartyEvaluation().getTransitions();

                String currentTransition = transitions.get(thirdPartyEvaluationIndex);
                messageSender.sendTextMessage(userId, null, currentTransition);

                thirdPartyEvaluationIndex++;
                boolean endOfTransitions = thirdPartyEvaluationIndex >= transitions.size();
                execution.setVariable(IProcessVariables.END_OF_THIRD_PARTY_EVALUATIONS, endOfTransitions);

            }
            execution.setVariable(IProcessVariables.THIRD_PARTY_EVALUATION_INDEX, thirdPartyEvaluationIndex);
        }


        List<ChallengeEntity> myCompletedChallenges = getMyCompletedChallenges(user);

        List<SolutionEntity> result = solutionRepository.findAllByUserIsNotAndChallengeIsIn(user, myCompletedChallenges);

        if (result == null) {
            result = Collections.emptyList();
        }

        // After sorting, the first element will be the one with the least number of evaluations
        result.sort((o1, o2) -> {
            int size1 = o1.getEvaluations() != null ? o1.getEvaluations().size() : 0;
            int size2 = o2.getEvaluations() != null ? o2.getEvaluations().size() : 0;
            return size1 - size2;
        });

        SolutionEntity solutionToSend = null;

        for (SolutionEntity solution : result) {
            if(solution.getEvaluations() != null && solution.getEvaluations().size() >= config.getNumberOfRequiredEvaluations()) {
                // we can cancel running through this list. Due to the sorting, the rest of the list has equal or more evaluations.
                break;
            }
            if (!alreadyEvaluatedByMe(user, solution) && !solution.isBlocked()) {
                solutionToSend = solution;
                break;
            }
        }

        String callbackMessage = execution.getProcessInstanceId() + "_" + IMessageEvents.SOLUTION_EVALUATED + "_" + IProcessVariables.SOLUTION_SCORE;

        // Fallback in case the user already evaluated ALL user solutions....
        if (solutionToSend == null) {
            // If there is no admin solution in our database, get the default solution from cms
            if (currentChallenge.getTemplateSolutions() != null && !currentChallenge.getTemplateSolutions().isEmpty()) {
                Thread.sleep(3000);
                messageSender.sendTemplateSolution(userId, currentChallenge, callbackMessage);
                execution.setVariable(IProcessVariables.DIFFERENT_SOLUTION_ID_TO_EVALUATE, -1L);
                // the cms based solution is definitely the last one to evaluate, so break the loop!
                execution.setVariable(IProcessVariables.END_OF_THIRD_PARTY_EVALUATIONS, true);
                return;
            } else {
                messageSender.sendTextMessage(userId, null, "Es gibt keine offenen Lösungen zu bewerten und auch keine Musterlösung zu dieser Challenge.");
            }

        }

        String challengeIdOfSolution = solutionToSend.getChallenge().getId();

        Optional<Challenge> challenge = cmsService.findChallengeById(user.getLanguageId(), challengeIdOfSolution);
        messageSender.sendSolutionWithMarkup(userId, callbackMessage, solutionToSend, challenge);
        execution.setVariable(IProcessVariables.DIFFERENT_SOLUTION_ID_TO_EVALUATE, solutionToSend.getId());
    }

    private boolean hasTransitions(ThirdPartyEvaluation thirdpartyEvaluation) {
        return thirdpartyEvaluation != null && thirdpartyEvaluation.getTransitions() != null && !"false".equals(thirdpartyEvaluation.getTransitions()) && !thirdpartyEvaluation.getTransitions().isEmpty();
    }

    private boolean alreadyEvaluatedByMe(UserEntity me, SolutionEntity solution) {
        boolean evaluatedByMe = false;

        List<EvaluationEntity> evaluations = solution.getEvaluations() != null ? solution.getEvaluations() : new ArrayList<>();

        for (EvaluationEntity evaluation : evaluations) {
            if (me.equals(evaluation.getUser())) {
                evaluatedByMe = true;
                break;
            }
        }
        return evaluatedByMe;
    }

    private List<ChallengeEntity> getMyCompletedChallenges(UserEntity user) {

        List<ChallengeEntity> challenges = new ArrayList<>();
        if (user.getSolutions() != null) {
            for (SolutionEntity solution : user.getSolutions()) {
                challenges.add(solution.getChallenge());
            }
        }
        return challenges;
    }

    private boolean hasThirdPartyEvaluation(Challenge challenge) {
        return challenge.getThirdpartyEvaluation() != null && !"false".equals(challenge.getThirdpartyEvaluation());
    }


    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setSolutionRepository(SolutionRepository solutionRepository) {
        this.solutionRepository = solutionRepository;
    }


    @Autowired
    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Autowired
    public void setCmsService(ICmsService cmsService) {
        this.cmsService = cmsService;
    }

    @Autowired
    public void setChatbotProperties(ChatbotProperties props) { this.config = props; }
}
