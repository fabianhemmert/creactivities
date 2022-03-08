package de.ips.creactivities.chatbot.telegram.commands;

import de.ips.creactivities.chatbot.dm.UserEntity;
import de.ips.creactivities.chatbot.i18n.I18n;
import de.ips.creactivities.chatbot.i18n.I18nService;
import de.ips.creactivities.chatbot.repo.UserRepository;
import de.ips.creactivities.chatbot.telegram.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

//@Component
@RequiredArgsConstructor
public class SetLanguageCommand implements ICommand {

    private final UserRepository userRepo;
    private final MessageSender sender;
    private final I18nService i18n;

    @Override
    public String getCommand() {
        return "changelanguage";
    }

    @Override
    public String getDescription() {
        return "Adjust your language preference.";
    }

    @Override
    public boolean isAdminCommand() {
        return false;
    }

    @Override
    public void execute(Message message) throws TelegramApiException, IOException {
        Optional<UserEntity> user = userRepo.findById(message.getFrom().getId() + "");
        String language = message.getFrom().getLanguageCode();
        if (user.isPresent()) {
            language = user.get().getLanguageId();
        }
        sender.sendTextMessage(message.getChatId() + "", generateMarkup("cmd_" + getCommand()), i18n.localize(language, I18n.COMMAND_LANGUAGE_CHOOSELANGUAGE));
    }

    @Override
    public void executeQuery(CallbackQuery query) throws TelegramApiException, IOException {
        String userId = query.getFrom().getId() + "";
        String language = query.getData().replace("cmd_" + getCommand() + "_", "");
        Optional<UserEntity> user = userRepo.findById(userId);
        if (user.isPresent()) {
            user.get().setLanguageId(language);
            userRepo.save(user.get());
        }
        sender.sendTextMessage(query.getMessage().getChatId() + "", null, i18n.localize(language, I18n.COMMAND_LANGUAGE_CONFIRMLANGUAGE) + language + "'.");
    }

    private InlineKeyboardMarkup generateMarkup(String callbackMessage) {
        List<InlineKeyboardButton> row1 = List.of(InlineKeyboardButton.builder().text("Deutsch").callbackData(callbackMessage + "_de").build(),
                InlineKeyboardButton.builder().text("English").callbackData(callbackMessage + "_en").build());

        InlineKeyboardMarkup replyMarkup = InlineKeyboardMarkup.builder().keyboardRow(row1).build();
        return replyMarkup;
    }

}
