package de.ips.creactivities.chatbot.process;

import de.ips.creactivities.chatbot.cms.ICmsService;
import de.ips.creactivities.chatbot.cms.dm.Challenge;
import de.ips.creactivities.chatbot.constants.IMessageEvents;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
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

import java.util.Optional;

@Component
@Slf4j
public class SendSolutionToAdminsDelegate implements JavaDelegate {

    private ICmsService cmsService;

    private SolutionRepository solutionRepository;

    private MessageSender messageSender;

    private UserRepository userRepository;

    private I18nService i18n;

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        String userId = (String) execution.getVariable(IProcessVariables.USER_ID);
        long solutionId = (long) execution.getVariable(IProcessVariables.SOLUTION_ID);
        String adminChatId = cmsService.getAdminGroupChatId();

        SolutionEntity solution = solutionRepository.findById(solutionId).get();

        String challengeIdOfSolution = solution.getChallenge().getId();
        String language = userRepository.findById(userId).get().getLanguageId();
        Optional<Challenge> challenge = cmsService.findChallengeById(language, challengeIdOfSolution);

        synchronized (messageSender) {
            messageSender.sendTextMessage(adminChatId, null, i18n.localize("de", I18n.PROCESS_SEND_SOLUTION_TO_ADMINS));
            String callbackMessage = execution.getProcessInstanceId() + "_" + IMessageEvents.ADMIN_EVALUATION + "_" + IProcessVariables.ADMIN_EVAL_VALUE;
            messageSender.sendSolutionWithMarkup(adminChatId, userId, callbackMessage, solution, challenge);
        }
    }


    @Autowired
    public void setCmsService(ICmsService cmsService) {
        this.cmsService = cmsService;
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
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setI18n(I18nService i18n) {
        this.i18n = i18n;
    }
}
