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
import org.springframework.util.Assert;

import java.util.*;

public class LevelLoaderDelegateTests {

    private LevelLoaderDelegate lld;
    private DelegateExecution execution;

    @BeforeEach
    private void setup() {

        List<String> levels = Arrays.asList("level1", "level2", "level3", "level4");
        Map<String, Level> levelMap = new HashMap<>();
        levelMap.put("level1", new Level("level1", "Level 1 Name", "Level 1 Description", true, null, null, null, null));
        levelMap.put("level2", new Level("level2", "Level 2 Name", "Level 2 Description", true, null, null, null, null));
        levelMap.put("level3", new Level("level3", "Level 3 Name", "Level 3 Description", false, null, null, null, null));
        levelMap.put("level4", new Level("level4", "Level 4 Name", "Level 4 Description", true, null, null, null, null));
        ICmsService cmsService = new MockCmsService(levelMap, levels);

        Map<String, UserEntity> users = new HashMap<>();
        users.put("user_1", new UserEntity("user_1", true, UserState.IDLE, "course_1", "123", "de", null, null, false, null, null));
        UserRepository userRepo = new MockUserRepo(users);
        Map<String, Object> delegationValues = new HashMap<>();
        delegationValues.put(IProcessVariables.COURSE_ID, "course 1");
        delegationValues.put(IProcessVariables.USER_ID, "user_1");
        lld = new LevelLoaderDelegate(userRepo, cmsService);
        execution = new MockDelegateExecution(delegationValues);
    }

    @Test
    void testFirst() throws Exception {
        lld.execute(execution);
        Assertions.assertEquals("level1", execution.getVariable(IProcessVariables.LEVEL_ID));
        Assertions.assertFalse((Boolean) execution.getVariable(IProcessVariables.END_OF_LEVELS));
    }

    @Test
    void testSecond() throws Exception {
        lld.execute(execution);
        Assertions.assertEquals("level1", execution.getVariable(IProcessVariables.LEVEL_ID));
        Assertions.assertFalse((Boolean) execution.getVariable(IProcessVariables.END_OF_LEVELS));
        lld.execute(execution);
        Assertions.assertEquals("level2", execution.getVariable(IProcessVariables.LEVEL_ID));
        Assertions.assertFalse((Boolean) execution.getVariable(IProcessVariables.END_OF_LEVELS));

    }

    @Test
    void testSkipThird() throws Exception {
        lld.execute(execution);
        lld.execute(execution);
        lld.execute(execution);
        Assertions.assertEquals("level4", execution.getVariable(IProcessVariables.LEVEL_ID));
        // Level4 is the end so this should be true now.
        Assertions.assertTrue((Boolean) execution.getVariable(IProcessVariables.END_OF_LEVELS));

    }

    @AllArgsConstructor
    private class MockCmsService extends CmsServiceMockBase {

        Map<String, Level> levelMap;
        List<String> levelList;

        @Override
        public List<String> getLevelsForCourse(String languageCode, String courseIdentifier) {
            return levelList;
        }

        @Override
        public Optional<Course> findCourseById(String languageCode, String courseIdentifier) {
            return Optional.empty();
        }


        @Override
        public Optional<Level> findLevelById(String languageCode, String levelIdentifier) {
            return Optional.ofNullable(levelMap.get(levelIdentifier));
        }

    }

    private static class MockUserRepo extends UserRepositoryMockBase {
        Map<String, UserEntity> users;

        public MockUserRepo(Map<String, UserEntity> users) {
            this.users = users;
        }

        @Override
        public Optional<UserEntity> findById(String s) {
            return (Optional.ofNullable(users.get(s)));
        }
    }
}
