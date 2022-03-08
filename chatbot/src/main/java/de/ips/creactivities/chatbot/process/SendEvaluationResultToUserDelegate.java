package de.ips.creactivities.chatbot.process;

import de.ips.creactivities.chatbot.cms.ICmsService;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.dm.EvaluationEntity;
import de.ips.creactivities.chatbot.dm.SolutionEntity;
import de.ips.creactivities.chatbot.i18n.I18n;
import de.ips.creactivities.chatbot.i18n.I18nService;
import de.ips.creactivities.chatbot.repo.SolutionRepository;
import de.ips.creactivities.chatbot.repo.UserRepository;
import de.ips.creactivities.chatbot.telegram.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class SendEvaluationResultToUserDelegate implements JavaDelegate {

    private SolutionRepository solutionRepository;

    private MessageSender messageSender;

    private UserRepository userRepo;

    private I18nService i18n;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        long solutionId = (long) execution.getVariable(IProcessVariables.SOLUTION_ID);
        String chatId = (String) execution.getVariable(IProcessVariables.CHAT_ID);

        Optional<SolutionEntity> solution = solutionRepository.findById(solutionId);

        if (solution.isPresent()) {
            String languageCode = userRepo.findById((String) execution.getVariable(IProcessVariables.USER_ID)).get().getLanguageId();

            if (solution.get().isBlocked()) {
                messageSender.sendTextMessage(chatId, null, i18n.localize(languageCode, I18n.PROCESS_SEND_EVALUATION_RESULT_TO_USER_BLOCKED));
                return;
            }

            List<EvaluationEntity> evaluations = solution.get().getEvaluations();
            int size = evaluations.size();
            int sum = 0;

            for (EvaluationEntity e : evaluations) {
                sum += e.getEvaluationScore();
            }

            double avg = sum / size;

            String eval = getEvaluationString(Math.round(avg), languageCode);
            messageSender.sendTextMessage(chatId, null, i18n.localize(languageCode, I18n.PROCESS_SEND_EVALUATION_RESULT_TO_USER_RESULT) + " " + eval);
        } else {
            log.error("Solution with id '" + solutionId + "' not found.");
            throw new IllegalStateException();
        }
    }

    private String getEvaluationString(long round, String languageCode) {
        String toReturn = "";

        if (round == 1) {
            toReturn = i18n.localize(languageCode, I18n.MESSAGE_EVALUATION_SCORE_ONE);
        } else if (round == 2) {
            toReturn = i18n.localize(languageCode, I18n.MESSAGE_EVALUATION_SCORE_TWO);
        } else if (round == 3) {
            toReturn = i18n.localize(languageCode, I18n.MESSAGE_EVALUATION_SCORE_THREE);
        } else if (round == 4) {
            toReturn = i18n.localize(languageCode, I18n.MESSAGE_EVALUATION_SCORE_FOUR);
        } else if (round == 5) {
            toReturn = i18n.localize(languageCode, I18n.MESSAGE_EVALUATION_SCORE_FIVE);
        }

        return toReturn;
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
    public void setUserRepository(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Autowired
    public void setI18nService(I18nService i18nService) {
        this.i18n = i18nService;
    }

}