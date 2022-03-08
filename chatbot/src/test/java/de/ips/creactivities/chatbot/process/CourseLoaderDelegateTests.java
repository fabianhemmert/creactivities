package de.ips.creactivities.chatbot.process;

import de.ips.creactivities.chatbot.basemocks.CmsServiceMockBase;
import de.ips.creactivities.chatbot.basemocks.UserRepositoryMockBase;
import de.ips.creactivities.chatbot.cms.ICmsService;
import de.ips.creactivities.chatbot.cms.dm.Course;
import de.ips.creactivities.chatbot.cms.dm.Level;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.dm.UserEntity;
import de.ips.creactivities.chatbot.dm.UserState;
import de.ips.creactivities.chatbot.repo.UserRepository;
import lombok.AllArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

public class CourseLoaderDelegateTests {

    private CourseLoaderDelegate delegate;
    private DelegateExecution execution;
    @BeforeEach
    private void setup() {

        List<String> courses = Arrays.asList("course1", "course2", "course3", "course4");
        Map<String, Course> courseMap = new HashMap<>();
        courseMap.put("course1", new Course("course1", true, null, null, null));
        courseMap.put("course2", new Course("course2", true, null, null, null));
        courseMap.put("course3", new Course("course3", false, null, null, null));
        courseMap.put("course4", new Course("course4", true, null, null, null));
        ICmsService cmsService = new MockCmsService(courseMap, courses);

        Map<String, UserEntity> users = new HashMap<>();
        users.put("user_1", new UserEntity("user_1", true, UserState.IDLE, null, "123", "de", null, null, false, null, null));
        UserRepository userRepo = new MockUserRepo(users);
        Map<String, Object> variables = new HashMap<>();
        variables.put(IProcessVariables.USER_ID, "user_1");
        delegate = new CourseLoaderDelegate();
        delegate.setUserRepo(userRepo);
        delegate.setCmsService(cmsService);
        execution = new MockDelegateExecution(variables);
    }
    @Test
    void testFirst() throws Exception {
        delegate.execute(execution);
        Assertions.assertEquals("course1", execution.getVariable(IProcessVariables.COURSE_ID));
        Assertions.assertFalse((Boolean) execution.getVariable(IProcessVariables.END_OF_COURSES));
    }

    @Test
    void testSecond() throws Exception {
        delegate.execute(execution);
        Assertions.assertEquals("course1", execution.getVariable(IProcessVariables.COURSE_ID));
        Assertions.assertFalse((Boolean) execution.getVariable(IProcessVariables.END_OF_COURSES));
        delegate.execute(execution);
        Assertions.assertEquals("course2", execution.getVariable(IProcessVariables.COURSE_ID));
        Assertions.assertFalse((Boolean) execution.getVariable(IProcessVariables.END_OF_COURSES));

    }

    @Test
    void testSkipThird() throws Exception {
        delegate.execute(execution);
        delegate.execute(execution);
        delegate.execute(execution);
        Assertions.assertEquals("course4", execution.getVariable(IProcessVariables.COURSE_ID));
        // Course4 is the end so this should be true now.
        Assertions.assertTrue((Boolean) execution.getVariable(IProcessVariables.END_OF_COURSES));

    }

    @AllArgsConstructor
    private class MockCmsService extends CmsServiceMockBase {

        Map<String, Course> courseMap;
        List<String> courseList;

        @Override
        public Optional<Course> findCourseById(String languageCode, String courseIdentifier) {
            return Optional.ofNullable(courseMap.get(courseIdentifier));
        }

        @Override
        public Optional<Level> findLevelById(String languageCode, String levelIdentifier) {
            return Optional.empty();
        }

        @Override
        public List<String> getCourseIdentifiers(String languageCode) {
            return courseList;
        }

    }

    private static class MockUserRepo extends UserRepositoryMockBase {
        Map<String, UserEntity> users;

        public MockUserRepo(Map<String, UserEntity> users) {
            this.users = users;
        }

        @Override
        public Optional<UserEntity> findById(String s) {
            return(Optional.ofNullable(users.get(s)));
        }

        @Override
        public <S extends UserEntity> S save(S entity) {
            users.put(entity.getId(), entity);
            return entity;
        }
    }
}
