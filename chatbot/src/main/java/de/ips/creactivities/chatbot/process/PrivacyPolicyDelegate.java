package de.ips.creactivities.chatbot.process;

import de.ips.creactivities.chatbot.cms.ICmsService;
import de.ips.creactivities.chatbot.constants.IMessageEvents;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.i18n.I18n;
import de.ips.creactivities.chatbot.i18n.I18nService;
import de.ips.creactivities.chatbot.repo.UserRepository;
import de.ips.creactivities.chatbot.telegram.CreactivitiesBot;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.List;

@Component
public class PrivacyPolicyDelegate implements JavaDelegate {

    private ICmsService cmsService;

    private I18nService i18n;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String chatId = (String) execution.getVariable(IProcessVariables.CHAT_ID);
        String languageCode = (String) execution.getVariable(IProcessVariables.LANGUAGE);
        String processInstanceId = execution.getProcessInstanceId();

        String imageUrl = cmsService.getPrivacyPolicyImage(languageCode);
        String text = getPolicyContent(languageCode);

        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder().keyboardRow(List.of(
                        InlineKeyboardButton.builder()
                                .text(i18n.localize(languageCode, I18n.PROCESS_PRIVACY_POLICY_ACCEPT))
                                .callbackData(processInstanceId + "_" + IMessageEvents.PRIVACY_POLICY_ACCEPTED).build()))
                .build();

        if (isNotNullEmptyOrFalse(imageUrl)) {
            SendPhoto policy = SendPhoto.builder().chatId(chatId)
                    .caption(text)
                    .photo(new InputFile(new URL(imageUrl).openStream(), "image.jpg"))
                    .replyMarkup(keyboard)
                    .build();
            CreactivitiesBot.getInstance().execute(policy);
        } else {
            SendMessage policy = SendMessage.builder().chatId(chatId)
                    .text(text)
                    .replyMarkup(keyboard)
                    .build();
            CreactivitiesBot.getInstance().execute(policy);
        }

    }

    private String getPolicyContent(String languageCode) {
        return cmsService.getPrivacyPolicy(languageCode);
    }

    private boolean isNotNullEmptyOrFalse(String string) {
        return string != null && !string.isEmpty() && !"false".equals(string);
    }

    @Autowired
    public void setCmsService(ICmsService cmsService) {
        this.cmsService = cmsService;
    }

    @Autowired
    public void setI18nService(I18nService i18nService) {
        this.i18n = i18nService;
    }

}
