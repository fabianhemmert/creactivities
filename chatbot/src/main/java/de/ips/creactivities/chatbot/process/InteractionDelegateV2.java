package de.ips.creactivities.chatbot.process;

import de.ips.creactivities.chatbot.cms.ICmsService;
import de.ips.creactivities.chatbot.cms.dm.Challenge;
import de.ips.creactivities.chatbot.cms.dm.Course;
import de.ips.creactivities.chatbot.cms.dm.InteractionElement;
import de.ips.creactivities.chatbot.cms.dm.Level;
import de.ips.creactivities.chatbot.constants.IMessageEvents;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.repo.UserRepository;
import de.ips.creactivities.chatbot.spiderplot.PersonalStrengthDiagram;
import de.ips.creactivities.chatbot.telegram.CreactivitiesBot;
import de.ips.creactivities.chatbot.telegram.DecorationAction;
import de.ips.creactivities.chatbot.telegram.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class InteractionDelegateV2 implements JavaDelegate {

    private ICmsService cmsService;

    private UserRepository userRepository;

    private MessageSender messageSender;

    private PersonalStrengthDiagram psd;

    @Autowired
    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Autowired
    public void setCmsService(ICmsService cmsService) {
        this.cmsService = cmsService;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setPsd(PersonalStrengthDiagram psd) {
        this.psd = psd;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        String chatId = (String) execution.getVariable(IProcessVariables.CHAT_ID);
        String processInstanceId = execution.getProcessInstanceId();
        String userId = (String) execution.getVariable(IProcessVariables.USER_ID);

        String languageCode = this.userRepository.findById(userId).get().getLanguageId();

        List<InteractionElement> interactions = getInteraction(languageCode, execution);

        if (interactions == null || interactions.isEmpty()) {
            throw new BpmnError(IMessageEvents.NO_INTERACTION_OR_RESPONSE);
        }

        int index = 0;
        if (execution.hasVariable(IProcessVariables.INTERACTION_ELEMENT_INDEX)) {
            index = (int) execution.getVariable(IProcessVariables.INTERACTION_ELEMENT_INDEX);
        }

        InteractionElement currentInteraction = interactions.get(index);

        messageSender.setAction(chatId, ("false".equals(currentInteraction.getImage()) || currentInteraction.getImage() == null) ? DecorationAction.TYPING : DecorationAction.UPLOAD_PHOTO);


        int delay = currentInteraction.getDelay() != null ? currentInteraction.getDelay() : 3;
        boolean longDelay = delay > 300; // Long delays are handled within the diagram, only sleep on short delays

        if (!longDelay) {
            Thread.sleep(delay * 1000);
        }
        sendMessageToChat(chatId, processInstanceId, currentInteraction);

        boolean requiresUserAnswers = currentInteraction.getUserAnswers() != null && !currentInteraction.getUserAnswers().isEmpty();

        execution.setVariable(IProcessVariables.INTERACTION_REQUIRES_USER_RESPONSE, requiresUserAnswers);
        execution.setVariable(IProcessVariables.INTERACTION_ELEMENT_INDEX, ++index);
        execution.setVariable(IProcessVariables.END_OF_INTERACTION, index >= interactions.size());
    }

    private void sendMessageToChat(String chatId, String processInstanceId, InteractionElement interactionElement) throws IOException, TelegramApiException {

        if (interactionElement != null) {
            String text = interactionElement.getText();
            InlineKeyboardMarkup replyMarkup = getReplyMarkup(processInstanceId, interactionElement.getUserAnswers());

            if (text.contains("{diagram}") || text.contains("{DIAGRAM}")) { // Send Staerkenprofil
                text = text.replace("{diagram}", "").replace("{DIAGRAM}", "");
                byte[] byteImage = psd.createPersonalStrengthDiagram(chatId);
                log.info("In Command: Image rendered.");
                SendPhoto message = SendPhoto.builder().chatId(chatId).caption(text)
                        .photo(new InputFile(new ByteArrayInputStream(byteImage), "Staerkenprofil.png"))
                        .replyMarkup(replyMarkup)
                        .build();
                CreactivitiesBot.getInstance().execute(message);
            } else { // Send provided image
                String image = interactionElement.getImage();
                if (isNotNullEmptyOrFalse(image)) {
                    SendPhoto message = SendPhoto.builder().chatId(chatId).caption(text)
                            .photo(new InputFile(new URL(image).openStream(), "image.jpg"))
                            .replyMarkup(replyMarkup)
                            .build();
                    CreactivitiesBot.getInstance().customExecute(message);
                } else {
                    SendMessage message = SendMessage.builder().chatId(chatId).text(text)
                            .replyMarkup(replyMarkup)
                            .build();
                    CreactivitiesBot.getInstance().execute(message);
                }
            }

        }
    }

    private InlineKeyboardMarkup getReplyMarkup(String processInstanceId, List<String> userAnswers) {
        if (userAnswers == null || userAnswers.isEmpty()) {
            return null;
        }

        List<InlineKeyboardButton> list = new LinkedList<>();

        int count = 0;
        for (String answer : userAnswers) {
            String callback = processInstanceId + "_" + IMessageEvents.INTERACTION_RESPONSE;
            if (isNotNullEmptyOrFalse(answer)) {
                callback += "_a_" + count++;
                list.add(InlineKeyboardButton.builder().text(answer).callbackData(callback).build());
            }
        }

        return InlineKeyboardMarkup.builder().keyboardRow(list).build();
    }

    private boolean isNotNullEmptyOrFalse(String string) {
        return string != null && !string.isEmpty() && !"false".equals(string);
    }

    private List<InteractionElement> getInteraction(String languageCode, DelegateExecution execution) {

        List<InteractionElement> result = new LinkedList<>();
        String courseId = (String) execution.getVariable(IProcessVariables.COURSE_ID);
        String levelId = (String) execution.getVariable(IProcessVariables.LEVEL_ID);
        String challengeId = (String) execution.getVariable(IProcessVariables.CHALLENGE_ID);
        String interactionType = (String) execution.getVariable(IProcessVariables.INTERACTION_TYPE);
        String depth = (String) execution.getVariable(IProcessVariables.INTERACTION_WORLD_DEPTH);

        boolean isPrologue = IProcessVariables.INTERACTION_TYPE_PROLOGUE.equals(interactionType);

        switch (depth) {
            case IProcessVariables.INTERACTION_WORLD_DEPTH_COURSE:
                Optional<Course> course = cmsService.findCourseById(languageCode, courseId);
                if (course.isPresent()) {
                    if (isPrologue) {
                        result = course.get().getPrologue();
                    } else {
                        result = course.get().getEpilogue();
                    }
                }
                break;
            case IProcessVariables.INTERACTION_WORLD_DEPTH_LEVEL:
                Optional<Level> level = cmsService.findLevelById(languageCode, levelId);
                if (level.isPresent()) {
                    if (isPrologue) {
                        result = level.get().getPrologue();
                    } else {
                        result = level.get().getEpilogue();
                    }
                }
                break;
            case IProcessVariables.INTERACTION_WORLD_DEPTH_CHALLENGE:
                Optional<Challenge> challenge = cmsService.findChallengeById(languageCode, challengeId);
                if (challenge.isPresent()) {
                    if (isPrologue) {
                        result = challenge.get().getPrologue();
                    } else {
                        result = challenge.get().getEpilogue();
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown Interaction depth: '" + depth + "'.");
        }
        return result;
    }
}
