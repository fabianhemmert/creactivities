package de.ips.creactivities.chatbot.process.admin;

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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
@Slf4j
public class SendReportedSolutionToAdminDelegate implements JavaDelegate {

    private ICmsService cmsService;

    private SolutionRepository solutionRepository;

    private MessageSender messageSender;

    private UserRepository userRepository;

    private I18nService i18n;

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        String reportedUserId = (String) execution.getVariable(IProcessVariables.USER_ID); // THIS IS THE USER WHOSE SOLUTION WAS REPORTED
        long reportedSolutionId = (long) execution.getVariable(IProcessVariables.REPORTED_SOLUTION_ID);
        String adminChatId = cmsService.getAdminGroupChatId();

        SolutionEntity reportedSolution = solutionRepository.findById(reportedSolutionId).get();
        String challengeIdOfSolution = reportedSolution.getChallenge().getId();
        String langOfReportedUser = userRepository.findById(reportedUserId).get().getLanguageId();

        Challenge challenge = cmsService.findChallengeById(langOfReportedUser, challengeIdOfSolution).get();


        String callbackMessage = execution.getProcessInstanceId() + "_" + IMessageEvents.ADMIN_REPORTED_EVALUATION + "_" + IProcessVariables.ADMIN_EVAL_VALUE;
        InlineKeyboardMarkup replyMarkup = generateMarkup("de", callbackMessage);

        messageSender.sendTextMessage(adminChatId, null, i18n.localize("de", I18n.MESSAGE_EVALUATION_ADMIN_REPORTED_SOLUTION));
        messageSender.sendTextMessage(adminChatId, null, i18n.localize("de", I18n.MESSAGE_EVALUATION_CHALLENGE));
        Thread.sleep(1500);

        String challengeText = challenge.getDescription();
        String image = challenge.getImage();
        if (image != null && !image.isEmpty() && !"false".equals(image)) {
            messageSender.sendImage(adminChatId, null, challengeText, image);
        } else {
            messageSender.sendTextMessage(adminChatId, null, challengeText);
        }

        messageSender.sendTextMessage(adminChatId, null, i18n.localize("de", I18n.MESSAGE_EVALUATION_ADMIN_SOLUTION_FROM_USER) + " " + reportedUserId);
        messageSender.sendSolutionToChat(adminChatId, reportedSolution, replyMarkup);
    }

    private InlineKeyboardMarkup generateMarkup(String languageCode, String callbackMessage) {

        List<InlineKeyboardButton> row0 = List.of(
                InlineKeyboardButton.builder()
                        .text(i18n.localize(languageCode, I18n.MESSAGE_EVALUATION_ADMIN_APPROVED))
                        .callbackData(callbackMessage + "_0").build());

        List<InlineKeyboardButton> row1 = List.of(
                InlineKeyboardButton.builder()
                        .text(i18n.localize(languageCode, I18n.MESSAGE_EVALUATION_ADMIN_SOLUTION_BLOCKED))
                        .callbackData(callbackMessage + "_1").build());

        List<InlineKeyboardButton> row2 = List.of(
                InlineKeyboardButton.builder()
                        .text(i18n.localize(languageCode, I18n.MESSAGE_EVALUATION_ADMIN_USER_BLOCKED))
                        .callbackData(callbackMessage + "_2").build());

        InlineKeyboardMarkup replyMarkup = InlineKeyboardMarkup.builder().keyboardRow(row0).keyboardRow(row1).keyboardRow(row2).build();
        return replyMarkup;
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
