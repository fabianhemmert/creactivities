package de.ips.creactivities.chatbot.telegram.commands;

import de.ips.creactivities.chatbot.dm.UserEntity;
import de.ips.creactivities.chatbot.i18n.I18n;
import de.ips.creactivities.chatbot.i18n.I18nService;
import de.ips.creactivities.chatbot.telegram.MessageSender;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

public class CommandHelper {

    public static boolean checkUserAndConsent(Optional<UserEntity> user, MessageSender sender, Message message, I18nService i18n) throws TelegramApiException {

        boolean userExistsAndConsentIsGiven = false;
        String chatId = message.getChatId() + "";
        if (user.isEmpty()) {
            String lang = message.getFrom().getLanguageCode();
            sender.sendTextMessage(chatId, null, i18n.localize(lang, I18n.COMMAND_USERNOTEXISTING));
        } else if (!user.get().isConsentGiven()) {
            String lang = user.get().getLanguageId();
            sender.sendTextMessage(chatId, null, i18n.localize(lang, I18n.COMMAND_CONSENTNOTGIVEN));
        } else {
            userExistsAndConsentIsGiven = true;
        }
       return userExistsAndConsentIsGiven;
    }
}
