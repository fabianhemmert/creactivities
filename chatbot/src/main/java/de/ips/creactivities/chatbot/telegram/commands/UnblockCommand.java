package de.ips.creactivities.chatbot.telegram.commands;

import de.ips.creactivities.chatbot.dm.UserEntity;
import de.ips.creactivities.chatbot.i18n.I18n;
import de.ips.creactivities.chatbot.i18n.I18nService;
import de.ips.creactivities.chatbot.repo.UserRepository;
import de.ips.creactivities.chatbot.telegram.CreactivitiesBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
public class UnblockCommand implements ICommand {

    private final UserRepository userRepo;
    private final I18nService i18n;

    @Override
    public String getCommand() {
        return "unblock";
    }

    @Override
    public String getDescription() {
        return "Unblocks a user. Usage: /unblock <userId>";
    }

    @Override
    public void execute(Message message) throws TelegramApiException, IOException {
        Optional<UserEntity> issuer = userRepo.findById(message.getFrom().getId() + "");
        String userId = message.getText().replace("/unblock", "").trim();
        if(issuer.isEmpty())
        {
            log.error("Did not find the user (" + message.getFrom().getId() + ") that tries to unblock " + userId);
            return;
        }
        Optional<UserEntity> user = userRepo.findById(userId);
        if (user.isPresent()) {
            UserEntity u = user.get();
            if (u.isBlocked()) {
                u.setBlocked(false);
                userRepo.save(u);
                SendMessage sm = SendMessage.builder().chatId(message.getChatId() + "").text("User " + userId + " unblocked.").build();
                SendMessage sendUnblockNotification = SendMessage.builder().chatId(u.getChatId()).text(i18n.localize(u.getLanguageId(), I18n.COMMAND_UNBLOCKUSER_NOTIFY)).build();

                CreactivitiesBot.getInstance().execute(sm);
                CreactivitiesBot.getInstance().execute(sendUnblockNotification);
            } else {
                SendMessage sm = SendMessage.builder().chatId(message.getChatId() + "").text(i18n.localize(issuer.get().getLanguageId(), I18n.COMMAND_UNBLOCKUSER_NOTBLOCKED)).build();
                CreactivitiesBot.getInstance().execute(sm);
            }
        } else {
            SendMessage sm = SendMessage.builder().chatId(message.getChatId() + "").text(i18n.localize(issuer.get().getLanguageId(), I18n.COMMAND_UNBLOCKUSER_UNKNOWN)).build();
            CreactivitiesBot.getInstance().execute(sm);
        }
    }
}
