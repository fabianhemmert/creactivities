package de.ips.creactivities.chatbot.process;

import de.ips.creactivities.chatbot.cms.ICmsService;
import de.ips.creactivities.chatbot.cms.dm.Challenge;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.dm.ChallengeEntity;
import de.ips.creactivities.chatbot.dm.UserEntity;
import de.ips.creactivities.chatbot.repo.ChallengeRepository;
import de.ips.creactivities.chatbot.repo.UserRepository;
import de.ips.creactivities.chatbot.telegram.CreactivitiesBot;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.net.URL;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Component
public class SendChallengeToUserDelegate implements JavaDelegate {

    private UserRepository userRepository;

    private ChallengeRepository challengeRepository;

    private ICmsService cmsService;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setChallengeRepository(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    @Autowired
    public void setCmsService(ICmsService cmsService) {
        this.cmsService = cmsService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        log.info("In Send Challenge to User Delegate");

        String chatId = (String) execution.getVariable(IProcessVariables.CHAT_ID);
        String userId = (String) execution.getVariable(IProcessVariables.USER_ID);
        String challengeId = (String) execution.getVariable(IProcessVariables.CHALLENGE_ID);

        UserEntity user = this.userRepository.findById(userId).get();
        String languageCode = user.getLanguageId();

        Optional<Challenge> challengeOptional = cmsService.findChallengeById(languageCode, challengeId);

        if (challengeOptional.isPresent()) {
            Challenge challenge = challengeOptional.get();
            String text = challenge.getDescription();
            String imageUrl = challenge.getImage();

            Optional<ChallengeEntity> result = challengeRepository.findById(challenge.getIdentifier());
            if (result.isEmpty()) {
                ChallengeEntity entity = new ChallengeEntity();
                entity.setId(challenge.getIdentifier());
                entity.setSolutions(Collections.emptyList());
                challengeRepository.save(entity);
            }
            user.setActiveChallenge(challenge.getIdentifier());
            userRepository.save(user);

            if (imageUrl != null && !imageUrl.isEmpty() && !"false".equals(imageUrl)) {
                SendPhoto message = SendPhoto.builder().chatId(chatId).caption(text)
                        .photo(new InputFile(new URL(imageUrl).openStream(), "image.jpg"))
                        .build();
                CreactivitiesBot.getInstance().customExecute(message);
            } else {
                SendMessage message = SendMessage.builder().chatId(chatId).text(text)
                        .build();
                CreactivitiesBot.getInstance().execute(message);
            }
        } else {
            throw new IllegalStateException("Challenge with id: " + challengeId + " is not present in CMS!" +
                    " Please ensure that there is at least one challenge in every level!");
        }
    }
}
