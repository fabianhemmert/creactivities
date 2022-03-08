package de.ips.creactivities.chatbot.process;

import de.ips.creactivities.chatbot.cms.ICmsService;
import de.ips.creactivities.chatbot.cms.dm.Challenge;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.dm.UserEntity;
import de.ips.creactivities.chatbot.repo.UserRepository;
import de.ips.creactivities.chatbot.telegram.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class ThanksGivingDelegate implements JavaDelegate {

    private UserRepository userRepo;

    private ICmsService cmsService;

    private MessageSender sender;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String chatId = (String) execution.getVariable(IProcessVariables.CHAT_ID);
        String userId = (String) execution.getVariable(IProcessVariables.USER_ID);
        String challengeId = (String) execution.getVariable(IProcessVariables.CHALLENGE_ID);

        // Workaround because of a bug in the persistence (see ChatbotApplicationTests)
        Iterable<UserEntity> users = userRepo.findAllById(List.of(userId));
        UserEntity user = users.iterator().next();

        Optional<Challenge> c = cmsService.findChallengeById(user.getLanguageId(), challengeId);

        if (c.isPresent() && hasThirdPartyEvaluation(c.get())) {
            sender.sendTextMessage(chatId, null, c.get().getThirdpartyEvaluation().getLastThanks());
        }
    }

    private boolean hasThirdPartyEvaluation(Challenge challenge) {
        return challenge.getThirdpartyEvaluation() != null && !"false".equals(challenge.getThirdpartyEvaluation());
    }

    @Autowired
    public void setUserRepo(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Autowired
    public void setCmsService(ICmsService cmsService) {
        this.cmsService = cmsService;
    }

    @Autowired
    public void setSender(MessageSender sender) {
        this.sender = sender;
    }
}
