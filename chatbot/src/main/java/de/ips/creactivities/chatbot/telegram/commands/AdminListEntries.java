package de.ips.creactivities.chatbot.telegram.commands;

import de.ips.creactivities.chatbot.cms.ICmsService;
import de.ips.creactivities.chatbot.dm.ChallengeEntity;
import de.ips.creactivities.chatbot.repo.ChallengeRepository;
import de.ips.creactivities.chatbot.repo.UserRepository;
import de.ips.creactivities.chatbot.telegram.CreactivitiesBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

//@Component
public class AdminListEntries implements ICommand {

    private ICmsService cmsService;

    private ChallengeRepository challengeRepo;

    private UserRepository userRepo;

    @Autowired
    public void setChallengeRepo(ChallengeRepository challengeRepo) {
        this.challengeRepo = challengeRepo;
    }

    @Autowired
    public void setCmsService(ICmsService cmsService) {
        this.cmsService = cmsService;
    }

    @Autowired
    public void setUserRepo(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public String getCommand() {
        return "listcontent";
    }

    @Override
    public String getDescription() {
        return "Lists all currently known content elements.";
    }

    @Override
    public void execute(Message message) throws TelegramApiException, IOException {
        String languageCode = userRepo.findById(String.valueOf(message.getFrom().getId())).get().getLanguageId();
        List<String> courses = cmsService.getCourseIdentifiers(languageCode);
        StringBuilder sb = new StringBuilder();
        for (String course : courses) {
            sb.append("Course: ").append(course).append("\n");
            List<String> levels = cmsService.getLevelsForCourse(languageCode, course);
            for (String level : levels) {
                sb.append("+--> Level: ").append(level).append("\n");
                List<String> challenges = cmsService.getChallengesForLevel(languageCode, level);
                for (String challenge : challenges) {
                    sb.append("+----> Challenge: ").append(challenge).append("\n");
                    Optional<ChallengeEntity> challengeEntity = challengeRepo.findById(challenge);
                    if (challengeEntity.isPresent()) {
                        sb.append("+------> Number of Solutions: ").append(challengeEntity.get().getSolutions().size()).append("\n");
                    }
                }
            }
        }
        SendMessage m = SendMessage.builder().chatId(message.getChatId() + "").text(sb.toString()).build();
        CreactivitiesBot.getInstance().execute(m);
    }
}
