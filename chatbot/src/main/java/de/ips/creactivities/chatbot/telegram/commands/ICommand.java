package de.ips.creactivities.chatbot.telegram.commands;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

public interface ICommand {

    String getCommand();

    String getDescription();

    default boolean isAdminCommand() {
        return true;
    }

    void execute(Message message) throws TelegramApiException, IOException;

    default void executeCallback(Message message) throws TelegramApiException, IOException {

    }

    default void executeQuery(CallbackQuery query) throws TelegramApiException, IOException {

    }
}
