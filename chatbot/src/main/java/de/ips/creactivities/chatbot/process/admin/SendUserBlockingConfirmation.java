package de.ips.creactivities.chatbot.process.admin;

import de.ips.creactivities.chatbot.cms.ICmsService;
import de.ips.creactivities.chatbot.constants.IMessageEvents;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.i18n.I18n;
import de.ips.creactivities.chatbot.i18n.I18nService;
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
public class SendUserBlockingConfirmation implements JavaDelegate {

    private MessageSender sender;

    private ICmsService cmsService;


    private I18nService i18n;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String userId = (String) execution.getVariable(IProcessVariables.USER_ID);
        String adminChatId = cmsService.getAdminGroupChatId();

        String callbackMessage = execution.getProcessInstanceId() + "_" + IMessageEvents.USER_BLOCK_RESPONSE + "_" + IProcessVariables.ADMIN_BLOCKUSER_VALUE;
        InlineKeyboardMarkup replyMarkup = generateMarkup("de", callbackMessage);
        sender.sendTextMessage(adminChatId, replyMarkup, i18n.localize("de", I18n.MESSAGE_EVALUATION_ADMIN_USER_BLOCK_CONFIRMATION) + " " + userId);
    }

    private InlineKeyboardMarkup generateMarkup(String languageCode, String callbackMessage) {

        List<InlineKeyboardButton> row0 = List.of(
                InlineKeyboardButton.builder()
                        .text(i18n.localize(languageCode, I18n.MESSAGE_EVALUATION_ADMIN_YES))
                        .callbackData(callbackMessage + "_1").build(),
                InlineKeyboardButton.builder()
                        .text(i18n.localize(languageCode, I18n.MESSAGE_EVALUATION_ADMIN_NO))
                        .callbackData(callbackMessage + "_0").build());

        InlineKeyboardMarkup replyMarkup = InlineKeyboardMarkup.builder().keyboardRow(row0).build();
        return replyMarkup;
    }

    @Autowired
    public void setSender(MessageSender sender) {
        this.sender = sender;
    }

    @Autowired
    public void setCmsService(ICmsService cmsService) {
        this.cmsService = cmsService;
    }

    @Autowired
    public void setI18n(I18nService i18n) {
        this.i18n = i18n;
    }
}
