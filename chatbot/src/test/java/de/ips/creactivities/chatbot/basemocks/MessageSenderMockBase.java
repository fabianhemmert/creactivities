package de.ips.creactivities.chatbot.basemocks;

import de.ips.creactivities.chatbot.cms.dm.Challenge;
import de.ips.creactivities.chatbot.dm.SolutionEntity;
import de.ips.creactivities.chatbot.telegram.DecorationAction;
import de.ips.creactivities.chatbot.telegram.MessageSender;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Optional;

public class MessageSenderMockBase extends MessageSender {
    @Override
    public void sendImage(String chatId, @Nullable InlineKeyboardMarkup replyMarkup, String caption, String image) throws TelegramApiException, IOException {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public void sendSolutionToChat(String chatId, SolutionEntity solution, InlineKeyboardMarkup replyMarkup) throws TelegramApiException, IOException {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public void sendSolutionWithMarkup(String chatId, String callbackMessage, SolutionEntity solution, Optional<Challenge> challenge) throws TelegramApiException, IOException {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public void sendTemplateSolution(String chatId, Challenge challenge, String callbackMessage) throws TelegramApiException, IOException, InterruptedException {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public void sendTextMessage(String chatId, @Nullable InlineKeyboardMarkup replyMarkup, String text) throws TelegramApiException {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public void sendTextMessage(String chatId, @Nullable InlineKeyboardMarkup replyMarkup, String text, @Nullable Integer replyToId) throws TelegramApiException {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public void setAction(String chatId, DecorationAction action) throws TelegramApiException {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }
}
