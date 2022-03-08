package de.ips.creactivities.chatbot.basemocks;

import de.ips.creactivities.chatbot.ChatbotProperties;
import de.ips.creactivities.chatbot.repo.UserRepository;
import de.ips.creactivities.chatbot.telegram.CreactivitiesBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.stickers.AddStickerToSet;
import org.telegram.telegrambots.meta.api.methods.stickers.CreateNewStickerSet;
import org.telegram.telegrambots.meta.api.methods.stickers.SetStickerSetThumb;
import org.telegram.telegrambots.meta.api.methods.stickers.UploadStickerFile;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CreactivitiesBotMock extends CreactivitiesBot {

    private Queue<PartialBotApiMethod> calledMethods = new LinkedList<>();

    public CreactivitiesBotMock() {
        super(null, null, null, null);
    }

    public CreactivitiesBotMock(ChatbotProperties properties, UserRepository userRepo) {
        super(properties, userRepo, null, null);
    }

    public Queue<PartialBotApiMethod> getCalledMethods() {
        return calledMethods;
    }

    @Override
    public String getBotToken() {
        return "";
    }

    @Override
    public Message customExecute(SendPhoto sendPhoto) throws TelegramApiException {
        this.calledMethods.offer(sendPhoto);
        return null;
    }

    @Override
    public Boolean execute(SetChatPhoto setChatPhoto) throws TelegramApiException {
        return true;
    }

    @Override
    public Message execute(SendAnimation sendAnimation) throws TelegramApiException {
        return null;
    }

    @Override
    public Boolean execute(AddStickerToSet addStickerToSet) throws TelegramApiException {
        return null;
    }

    @Override
    public Boolean execute(SetStickerSetThumb setStickerSetThumb) throws TelegramApiException {
        return null;
    }

    @Override
    public Boolean execute(CreateNewStickerSet createNewStickerSet) throws TelegramApiException {
        return null;
    }

    @Override
    public List<Message> execute(SendMediaGroup sendMediaGroup) throws TelegramApiException {
        return null;
    }

    @Override
    public File execute(UploadStickerFile uploadStickerFile) throws TelegramApiException {
        return null;
    }

    @Override
    public Serializable execute(EditMessageMedia editMessageMedia) throws TelegramApiException {
        return null;
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) throws TelegramApiException {
        this.calledMethods.offer(method);
        return null;
    }

}
