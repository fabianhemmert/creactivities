package de.ips.creactivities.chatbot.process;

import de.ips.creactivities.chatbot.basemocks.CmsServiceMockBase;
import de.ips.creactivities.chatbot.basemocks.UserRepositoryMockBase;
import de.ips.creactivities.chatbot.cms.ICmsService;
import de.ips.creactivities.chatbot.cms.dm.Challenge;
import de.ips.creactivities.chatbot.cms.dm.Course;
import de.ips.creactivities.chatbot.cms.dm.Level;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.dm.UserEntity;
import de.ips.creactivities.chatbot.dm.UserState;
import de.ips.creactivities.chatbot.repo.UserRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

public class ChallengeLoaderDelegateTests {

    private ICmsService cmsService;
    private UserRepository userRepo;
    private DelegateExecution execution;
    private ChallengeLoaderDelegate challengeLoaderDelegate;

    @BeforeEach
    public void prepare() {
        List<String> challenges = Arrays.asList("challenge_1", "challenge_2", "challenge_3");
        Map<String, Challenge> challengeMap = new HashMap<>();
        challengeMap.put("challenge_1", new Challenge(null, null, null, true, null, null, null, null, null, null, null));
        challengeMap.put("challenge_2", new Challenge(null, null, null, false, null, null, null, null, null, null, null));
        challengeMap.put("challenge_3", new Challenge(null, null, null, true, null, null, null, null, null, null, null));
        cmsService = new MockCmsService(challenges, challengeMap);
        Map<String, UserEntity> users = new HashMap<>();
        users.put("user_1", new UserEntity("user_1", true, UserState.IDLE, "course_1", "123", "de", null, null, false, null, null));

        Map<String, Object> delegationValues = new HashMap<>();
        delegationValues.put(IProcessVariables.COURSE_ID, "course_1");
        delegationValues.put(IProcessVariables.LEVEL_ID, "level_1");
        delegationValues.put(IProcessVariables.USER_ID, "user_1");
        execution = new MockDelegateExecution(delegationValues);

        userRepo = new MockUserRepo(users);
        challengeLoaderDelegate = new ChallengeLoaderDelegate();
        challengeLoaderDelegate.setCmsService(cmsService);
        challengeLoaderDelegate.setUserRepository(userRepo);
    }

    @Test
    public void testFirstId() throws Exception {
        challengeLoaderDelegate.execute(execution);
        Assertions.assertEquals("challenge_1", execution.getVariable(IProcessVariables.CHALLENGE_ID));
        Assertions.assertFalse((Boolean) execution.getVariable(IProcessVariables.END_OF_CHALLENGES));
    }

    @Test
    public void testSecond() throws Exception {
        execution.setVariable(IProcessVariables.CHALLENGE_ID, "challenge_1");
        execution.setVariable(IProcessVariables.END_OF_CHALLENGES, false);
        challengeLoaderDelegate.execute(execution);
        Assertions.assertEquals("challenge_3", execution.getVariable(IProcessVariables.CHALLENGE_ID));
        Assertions.assertTrue((Boolean) execution.getVariable(IProcessVariables.END_OF_CHALLENGES));
    }

    @Test
    public void testCurrentChallengeDeactivated() throws Exception {
        execution.setVariable(IProcessVariables.CHALLENGE_ID, "challenge_2");
        execution.setVariable(IProcessVariables.END_OF_CHALLENGES, false);
        challengeLoaderDelegate.execute(execution);
        Assertions.assertEquals("challenge_3", execution.getVariable(IProcessVariables.CHALLENGE_ID));
        Assertions.assertTrue((Boolean) execution.getVariable(IProcessVariables.END_OF_CHALLENGES));
    }

    private static class MockCmsService extends CmsServiceMockBase {
        private final Map<String, Challenge> challengeMap;
        List<String> challenges;

        public MockCmsService(List<String> challenges, Map<String, Challenge> challengeMap) {
            this.challenges = challenges;
            this.challengeMap = challengeMap;
        }

        @Override
        public List<String> getChallengesForLevel(String languageCode, String levelIdentifier) {
            return challenges;
        }

        @Override
        public Optional<Course> findCourseById(String languageCode, String courseIdentifier) {
            return Optional.empty();
        }

        @Override
        public Optional<Level> findLevelById(String languageCode, String levelIdentifier) {
            return Optional.empty();
        }

        @Override
        public Optional<Challenge> findChallengeById(String languageCode, String challengeIdentifier) {
            return Optional.ofNullable(challengeMap.get(challengeIdentifier));
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