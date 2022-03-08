package de.ips.creactivities.chatbot.process;

import de.ips.creactivities.chatbot.cms.ICmsService;
import de.ips.creactivities.chatbot.cms.dm.Level;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LevelLoaderDelegate implements JavaDelegate {

    private final UserRepository userRepo;

    private final ICmsService cmsService;


    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("In Level Loader Delegate");
        String courseId = (String) execution.getVariable(IProcessVariables.COURSE_ID);
        Object levelIdObj = execution.getVariable(IProcessVariables.LEVEL_ID);
        String languageCode = userRepo.findById((String) execution.getVariable(IProcessVariables.USER_ID)).get().getLanguageId();
        List<String> levelIds = cmsService.getLevelsForCourse(languageCode, courseId);

        String currentLevelId = null;
        // Boolean that signals that we found another LEVEL after the current (so that the process knows it has to come back here after the customer solved the current level).
        boolean foundFollower = false;
        // Boolean that is used in the loop to skip all levels we already visited.
        boolean afterCurrent = false;
        // Boolean that determines if we found a new level to give to the user.
        boolean foundNewcurrent = false;

        if (levelIdObj == null) {
            afterCurrent = true;
        } else {
            currentLevelId = (String) levelIdObj;
        }

        for (String levelId : levelIds) {
            if (!levelId.equals(currentLevelId) && !afterCurrent) {
                // Skip all already passed levels.
                continue;
            } else if (!afterCurrent) {
                afterCurrent = true;
                continue;
            } else if (!foundNewcurrent) {
                Level level = cmsService.findLevelById(languageCode, levelId).get();
                if (level.getActive()) {
                    currentLevelId = levelId;
                    foundNewcurrent = true;
                }
            } else {
                Level level = cmsService.findLevelById(languageCode, levelId).get();
                if (level.getActive()) {
                    foundFollower = true;
                    break;
                }
            }
        }

        execution.setVariable(IProcessVariables.LEVEL_ID, currentLevelId);
        execution.setVariable(IProcessVariables.END_OF_LEVELS, !foundFollower);
    }
}
