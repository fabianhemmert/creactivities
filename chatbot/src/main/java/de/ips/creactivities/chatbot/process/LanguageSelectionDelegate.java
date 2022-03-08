package de.ips.creactivities.chatbot.process;

import de.ips.creactivities.chatbot.constants.IMessageEvents;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.telegram.MessageSender;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class LanguageSelectionDelegate implements JavaDelegate {

    private MessageSender messageSender;

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        String chatId = (String) execution.getVariable(IProcessVariables.CHAT_ID);
        String processInstanceId = execution.getProcessInstanceId();

        String callbackMessage = processInstanceId + "_" + IMessageEvents.LANGUAGE_SELECTED + "_" + IProcessVariables.LANGUAGE;

        List<InlineKeyboardButton> row1 = List.of(InlineKeyboardButton.builder().text("Deutsch").callbackData(callbackMessage + "_de").build(),
                InlineKeyboardButton.builder().text("English").callbackData(callbackMessage + "_en").build());

        InlineKeyboardMarkup replyMarkup = InlineKeyboardMarkup.builder().keyboardRow(row1).build();
        String text = "Sprache auswählen / Select language";
        messageSender.sendTextMessage(chatId, replyMarkup, text);
    }

    @Autowired
    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }
}