package de.ips.creactivities.chatbot.process;

import de.ips.creactivities.chatbot.cms.ICmsService;
import de.ips.creactivities.chatbot.cms.dm.Course;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.dm.UserEntity;
import de.ips.creactivities.chatbot.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CourseLoaderDelegate implements JavaDelegate {

    private UserRepository userRepo;

    private ICmsService cmsService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("In Course Loader Delegate");
        Object courseObj = execution.getVariable(IProcessVariables.COURSE_ID);

        UserEntity user = userRepo.findById((String) execution.getVariable(IProcessVariables.USER_ID)).get();
        String languageCode = user.getLanguageId();
        List<String> courseIds = cmsService.getCourseIdentifiers(languageCode);

        String currentCourseId = null;
        // Boolean that signals that we found another COURSE after the current (so that the process knows it has to come back here after the customer solved the current course).
        boolean foundFollower = false;
        // Boolean that is used in the loop to skip all courses we already visited.
        boolean afterCurrent = false;
        // Boolean that determines if we found a new course to give to the user.
        boolean foundNewcurrent = false;

        if(courseObj == null) {
            afterCurrent = true;
        } else {
            currentCourseId = (String) courseObj;
        }

        for(String courseId: courseIds) {
            if(!courseId.equals(currentCourseId) && !afterCurrent) {
                // Skip all already passed courses.
                continue;
            } else if(!afterCurrent) {
                afterCurrent = true;
                continue;
            } else if (!foundNewcurrent) {
                Course course = cmsService.findCourseById(languageCode, courseId).get();
                if(course.getActive()) {
                    currentCourseId = courseId;
                    foundNewcurrent = true;
                }
            } else {
                Course course = cmsService.findCourseById(languageCode, courseId).get();
                if(course.getActive()) {
                    foundFollower = true;
                    break;
                }
            }
        }

        user.setActiveWorld(currentCourseId);
        userRepo.save(user);

        execution.setVariable(IProcessVariables.COURSE_ID, currentCourseId);
        execution.setVariable(IProcessVariables.END_OF_COURSES, !foundFollower);
    }

    @Autowired
    public void setUserRepo(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Autowired
    public void setCmsService(ICmsService cmsService) {
        this.cmsService = cmsService;
    }
}
