package de.ips.creactivities.chatbot.telegram.commands;

import de.ips.creactivities.chatbot.dm.ChallengeEntity;
import de.ips.creactivities.chatbot.dm.SolutionEntity;
import de.ips.creactivities.chatbot.repo.ChallengeRepository;
import de.ips.creactivities.chatbot.repo.SolutionRepository;
import de.ips.creactivities.chatbot.telegram.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Optional;


//@Component
public class AdminShowSolutionsForChallenge implements ICommand {

    private ChallengeRepository challengeRepo;
    private SolutionRepository solutionRepo;
    private MessageSender messageSender;

    @Autowired
    public void setSolutionRepo(SolutionRepository solutionRepo) {
        this.solutionRepo = solutionRepo;
    }

    @Autowired
    public void setChallengeRepo(ChallengeRepository challengeRepo) {
        this.challengeRepo = challengeRepo;
    }

    @Autowired
    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    public String getCommand() {
        return "showsolutionsforchallenge";
    }

    @Override
    public String getDescription() {
        return "Displays all solutions for a given challenge.";
    }

    @Override
    public void execute(Message message) throws TelegramApiException, IOException {
        Long chatId = message.getChatId();
        String challengeId = message.getText().replace("/" + getCommand(), "").trim();
        Optional<ChallengeEntity> challengeEntity = challengeRepo.findById(challengeId);
        if (challengeEntity.isPresent()) {
            for (SolutionEntity solution : challengeEntity.get().getSolutions()) {
                InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder().build();
                messageSender.sendSolutionToChat(chatId + "", solutionRepo.findById(solution.getId()).get(), keyboard);
            }
        }
    }
}
