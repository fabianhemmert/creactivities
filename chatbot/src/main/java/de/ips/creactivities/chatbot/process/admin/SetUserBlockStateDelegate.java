package de.ips.creactivities.chatbot.process.admin;

import de.ips.creactivities.chatbot.cms.ICmsService;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.dm.UserEntity;
import de.ips.creactivities.chatbot.i18n.I18n;
import de.ips.creactivities.chatbot.i18n.I18nService;
import de.ips.creactivities.chatbot.repo.UserRepository;
import de.ips.creactivities.chatbot.telegram.CreactivitiesBot;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@Slf4j
public class SetUserBlockStateDelegate implements JavaDelegate {

    private ICmsService cmsService;

    private I18nService i18n;

    private UserRepository userRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String userId = (String) execution.getVariable(IProcessVariables.USER_ID);
        UserEntity user = userRepository.findById(userId).get();
        user.setBlocked(true);
        userRepository.save(user);

        String chatId = user.getChatId();
        String languageCode = user.getLanguageId();

        SendMessage blockMessage = SendMessage.builder().chatId(chatId)
                .text(i18n.localize(languageCode, I18n.PROCESS_SEND_SET_USER_BLOCK_STATE_MESSAGE))
                .build();
        CreactivitiesBot.getInstance().execute(blockMessage);
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setCmsService(ICmsService cmsService) {
        this.cmsService = cmsService;
    }

    @Autowired
    public void setI18nService(I18nService i18nService) {
        this.i18n = i18nService;
    }

}
