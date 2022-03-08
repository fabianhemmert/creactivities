package de.ips.creactivities.chatbot.telegram.commands;

import de.ips.creactivities.chatbot.dm.UserEntity;
import de.ips.creactivities.chatbot.i18n.I18n;
import de.ips.creactivities.chatbot.i18n.I18nService;
import de.ips.creactivities.chatbot.repo.UserRepository;
import de.ips.creactivities.chatbot.spiderplot.PersonalStrengthDiagram;
import de.ips.creactivities.chatbot.telegram.CreactivitiesBot;
import de.ips.creactivities.chatbot.telegram.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PersonalPerformanceCommand implements ICommand {

    private final PersonalStrengthDiagram psd;
    private final I18nService i18n;
    private final UserRepository userRepo;
    private final MessageSender sender;

    @Override
    public String getCommand() {
        return "chart";
    }

    @Override
    public boolean isAdminCommand() {
        return false;
    }

    @Override
    public String getDescription() {
        return "View your chart.";
    }

    @Override
    public void execute(Message message) throws TelegramApiException, IOException {

        Optional<UserEntity> user = userRepo.findById(message.getFrom().getId() + "");
        if (!CommandHelper.checkUserAndConsent(user, sender, message, i18n)) {
            return;
        }
        String lang = user.isPresent() ? user.get().getLanguageId() : message.getFrom().getLanguageCode();

        try {
            long now = System.currentTimeMillis();
            byte[] image = psd.createPersonalStrengthDiagram(message.getFrom().getId() + "");
            log.info("In Command: Image rendered.");
            SendPhoto responseMessage = SendPhoto.builder().chatId(message.getChatId() + "").caption(i18n.localize(lang, I18n.COMMAND_STAERKENDIAGRAMM_ANSWER))
                    .photo(new InputFile(new ByteArrayInputStream(image), "Staerkenprofil.png"))
                    .build();
            long then = System.currentTimeMillis();
            log.info("Diagram Generation for " + message.getFrom().getId() + " took " + (then - now) + "ms.");
            CreactivitiesBot.getInstance().customExecute(responseMessage);
        } catch (Throwable t) {
            log.error("Caught something! ", t);
            if (t instanceof Error) {
                throw (Error) t;
            }
        } finally {
            log.info("In finally for PersonalPerformanceCommand");
        }
    }
}
