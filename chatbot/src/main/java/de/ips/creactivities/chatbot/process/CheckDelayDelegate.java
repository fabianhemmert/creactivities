package de.ips.creactivities.chatbot.process;

import de.ips.creactivities.chatbot.cms.ICmsService;
import de.ips.creactivities.chatbot.cms.dm.Challenge;
import de.ips.creactivities.chatbot.cms.dm.Course;
import de.ips.creactivities.chatbot.cms.dm.InteractionElement;
import de.ips.creactivities.chatbot.cms.dm.Level;
import de.ips.creactivities.chatbot.constants.IMessageEvents;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.repo.UserRepository;
import de.ips.creactivities.chatbot.telegram.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class CheckDelayDelegate implements JavaDelegate {

    private ICmsService cmsService;

    private UserRepository userRepository;

    private MessageSender messageSender;

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

        int delay = currentInteraction.getDelay() != null ? currentInteraction.getDelay() : 3;

        boolean requiresLongDelay = delay > 300;

        if (requiresLongDelay) {
            String delayDuration = "PT" + delay + "S";
            execution.setVariable(IProcessVariables.INTERACTION_CURRENT_DELAY, delayDuration);
        }

        execution.setVariable(IProcessVariables.INTERACTION_REQUIRES_LONG_DELAY, requiresLongDelay);
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
