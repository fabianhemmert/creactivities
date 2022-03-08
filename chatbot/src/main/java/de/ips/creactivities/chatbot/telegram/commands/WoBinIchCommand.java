package de.ips.creactivities.chatbot.telegram.commands;

import de.ips.creactivities.chatbot.cms.CmsService;
import de.ips.creactivities.chatbot.cms.dm.Level;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.dm.UserEntity;
import de.ips.creactivities.chatbot.i18n.I18n;
import de.ips.creactivities.chatbot.i18n.I18nService;
import de.ips.creactivities.chatbot.process.CamundaService;
import de.ips.creactivities.chatbot.repo.UserRepository;
import de.ips.creactivities.chatbot.telegram.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class WoBinIchCommand implements ICommand {

    private I18nService i18n;
    private CamundaService camundaService;

    private UserRepository userRepo;

    private MessageSender messageSender;

    private CmsService cmsService;

    @Override
    public String getCommand() {
        return "whereami";
    }

    @Override
    public boolean isAdminCommand() {
        return false;
    }

    @Override
    public String getDescription() {
        return "See your location.";
    }

    @Override
    public void execute(Message message) throws TelegramApiException, IOException {

        Optional<UserEntity> entity = userRepo.findById(message.getFrom().getId() + "");
        if (!CommandHelper.checkUserAndConsent(entity, messageSender, message, i18n)) {
            return;
        }
        //noinspection OptionalGetWithoutIsPresent implicitly checked in checkUserAndConsent
        UserEntity user = entity.get();
        String language = user.getLanguageId();
        String textToSend = i18n.localize(language, I18n.COMMAND_WOBINICH_REFUSAL);
        String imageToSend = null;
        ProcessInstance level = camundaService.findActiveLevelProcessInstance(user.getId());
        if (level != null) {

            Map<String, Object> variables = camundaService.getRuntimeService().getVariables(level.getProcessInstanceId());
            String levelId = (String) variables.get(IProcessVariables.LEVEL_ID);
            Optional<Level> cmsLevelOpt = cmsService.findLevelById(language, levelId);
            if (cmsLevelOpt.isEmpty()) {
                log.error("Could not find level for Id " + levelId);
                return;
            }
            Level cmsLevel = cmsLevelOpt.get();

            imageToSend = cmsLevel.getImage();
            textToSend = getLevelStatus(language, level, cmsLevel);
            ProcessInstance instance = camundaService.findActiveChallengeProcessInstance(user.getId());
            if (instance != null) {
                textToSend += getChallengeStatus(language, instance);
            } else {
                textToSend += i18n.localize(language, I18n.COMMAND_WOBINICH_NOTINCHALLENGE);
            }
        }

        if (imageToSend != null && !imageToSend.isEmpty() && !"false".equals(imageToSend)) {
            messageSender.sendImage(message.getChatId() + "", null, textToSend, imageToSend, message.getMessageId());
        } else {
            messageSender.sendTextMessage(message.getChatId() + "", null, textToSend, message.getMessageId());
        }


    }

    private String getLevelStatus(String language, ProcessInstance instance, Level level) {
        String toReturn = "";
        ActivityInstance[] activities = camundaService.getRuntimeService().getActivityInstance(instance.getProcessInstanceId())
                .getChildActivityInstances();

        if (activities != null && activities.length == 1) {
            ActivityInstance currentActivity = activities[0];

            String id = currentActivity.getActivityId();

            switch (id) {
                case "Act_Level_Prologue":
                    toReturn = i18n.localize(language, I18n.COMMAND_WOBINICH_INLEVELPROLOGUE) + level.getName() + "'.";
                    break;
                case "Act_Level_Epilogue":
                    toReturn = i18n.localize(language, I18n.COMMAND_WOBINICH_INLEVELEPILOGUE) + level.getName() + "'.";
                    break;
                case "Act_Level_InChallenge":
                default:
                    toReturn = i18n.localize(language, I18n.COMMAND_WOBINICH_INLEVEL) + level.getName() + "'.\n";

            }
        }
        return toReturn + "\n";
    }

    private String getChallengeStatus(String language, ProcessInstance instance) {
        String toReturn = i18n.localize(language, I18n.COMMAND_WOBINICH_INCHALLENGE);
        ActivityInstance[] activities = camundaService.getRuntimeService().getActivityInstance(instance.getProcessInstanceId())
                .getChildActivityInstances();

        if (activities != null && activities.length == 1) {
            ActivityInstance currentActivity = activities[0];

            String id = currentActivity.getActivityId();

            switch (id) {
                case "Act_Challenge_Prologue":
                    toReturn = i18n.localize(language, I18n.COMMAND_WOBINICH_INCHALLENGEPROLOGUE);
                    break;
                case "Act_Challenge_InProgress":
                    toReturn = i18n.localize(language, I18n.COMMAND_WOBINICH_INCHALLENGEWAITINGFORSOLUTION);
                    break;
                case "Act_Evaluate_Other":
                    toReturn = i18n.localize(language, I18n.COMMAND_WOBINICH_INCHALLENGEEVALUATING);
                    break;
                case "Act_Wait_For_Evaluations":
                    toReturn = i18n.localize(language, I18n.COMMAND_WOBINICH_INCHALLENGEAWAITINGRESULT);
                    break;
                case "Act_Challenge_Epilogue":
                    toReturn = i18n.localize(language, I18n.COMMAND_WOBINICH_INCHALLENGEEPILOGUE);
                    break;
                default:
                    break;
            }

        }
        return toReturn;
    }

    @Autowired
    public void setCamundaService(CamundaService camundaService) {
        this.camundaService = camundaService;
    }

    @Autowired
    public void setUserRepo(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Autowired
    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Autowired
    public void setCmsService(CmsService cmsService) {
        this.cmsService = cmsService;
    }

    @Autowired
    public void setI18n(I18nService i18n) {
        this.i18n = i18n;
    }
}
