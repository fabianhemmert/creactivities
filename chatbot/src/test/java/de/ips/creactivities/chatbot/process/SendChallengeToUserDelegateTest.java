package de.ips.creactivities.chatbot.process;

import de.ips.creactivities.chatbot.basemocks.ChallengeRepositoryMockBase;
import de.ips.creactivities.chatbot.basemocks.CmsServiceMockBase;
import de.ips.creactivities.chatbot.basemocks.CreactivitiesBotMock;
import de.ips.creactivities.chatbot.cms.ICmsService;
import de.ips.creactivities.chatbot.cms.dm.Challenge;
import de.ips.creactivities.chatbot.cms.dm.Course;
import de.ips.creactivities.chatbot.cms.dm.Level;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.dm.ChallengeEntity;
import de.ips.creactivities.chatbot.dm.UserEntity;
import de.ips.creactivities.chatbot.repo.UserRepository;
import de.ips.creactivities.chatbot.telegram.CreactivitiesBot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SendChallengeToUserDelegateTest {

    private static final String IMAGE_URL = "https://www.djgummikuh.de/wp-content/uploads/2022/01/image-1.png";

    private static final String INSTANCE_ID = "instance1";

    private static final String USER_1_ID = "user1";
    private static final String CHAT_ID = "chat1";
    private static final String CHALLENGE_ID = "1";

    private SendChallengeToUserDelegate delegate;

    private ICmsService cmsService;

    private UserRepository userRepository;

    private MockChallengeRepo challengeRepository;

    private MockDelegateExecution execution;

    private Map<String, Challenge> challenges;

    private Map<String, ChallengeEntity> challengeEntities;

    private CreactivitiesBotMock botMock;


    @BeforeEach
    public void prepare() {

        botMock = new CreactivitiesBotMock();
        CreactivitiesBot.setInstance(botMock);
        challenges = new HashMap<>();
        challengeEntities = new HashMap<>();
        Map<String, UserEntity> users = new HashMap<>();
        users.put(USER_1_ID, new UserEntity(USER_1_ID, true, null, null, CHAT_ID, "de", null, null, false, null, null));
        userRepository = new MockUserRepo(users);
        challengeRepository = new MockChallengeRepo(challengeEntities);


        Map<String, Object> variables = new HashMap<>();
        variables.put(IProcessVariables.USER_ID, USER_1_ID);
        variables.put(IProcessVariables.CHAT_ID, CHAT_ID);
        variables.put(IProcessVariables.CHALLENGE_ID, CHALLENGE_ID);
        execution = new MockDelegateExecution(variables);
        execution.setProcessInstanceId(INSTANCE_ID);

        cmsService = new MockCmsService(challenges);

        delegate = new SendChallengeToUserDelegate();

        delegate.setCmsService(cmsService);
        delegate.setUserRepository(userRepository);
        delegate.setChallengeRepository(challengeRepository);
    }

    @Test
    public void testWithoutImage() throws Exception {

        Challenge c = new Challenge(CHALLENGE_ID, "Challenge 1", "This is the first challenge...", true, null, null, null, null, null, null, null);
        challenges.put(c.getIdentifier(), c);

        delegate.execute(execution);

        Assertions.assertEquals(1, botMock.getCalledMethods().size());
        SendMessage sendMessage = (SendMessage) botMock.getCalledMethods().poll();
        Assertions.assertEquals(c.getDescription(), sendMessage.getText());
        Assertions.assertTrue(challengeRepository.getChallenges().containsKey(CHALLENGE_ID));
    }

    @Test
    public void testWithImage() throws Exception {

        Challenge c = new Challenge(CHALLENGE_ID, "Challenge 1", "This is the first challenge...", true, null, null, null, IMAGE_URL, null, null, null);
        challenges.put(c.getIdentifier(), c);

        delegate.execute(execution);

        Assertions.assertEquals(1, botMock.getCalledMethods().size());

        PartialBotApiMethod calledMethod = botMock.getCalledMethods().poll();
        Assertions.assertTrue(calledMethod instanceof SendPhoto);
        SendPhoto sendMessage = (SendPhoto) calledMethod;
        Assertions.assertEquals(c.getDescription(), sendMessage.getCaption());
        Assertions.assertTrue(challengeRepository.getChallenges().containsKey(CHALLENGE_ID));
    }

    private static class MockCmsService extends CmsServiceMockBase {
        private final Map<String, Challenge> challengeMap;

        public MockCmsService(Map<String, Challenge> challengeMap) {
            this.challengeMap = challengeMap;
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

    private static class MockChallengeRepo extends ChallengeRepositoryMockBase {

        private Map<String, ChallengeEntity> challenges;

        public MockChallengeRepo(Map<String, ChallengeEntity> challenges) {
            this.challenges = challenges;
        }

        @Override
        public <S extends ChallengeEntity> S save(S entity) {
            challenges.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public Optional<ChallengeEntity> findById(String s) {
            return Optional.ofNullable(challenges.get(s));
        }

        public Map<String, ChallengeEntity> getChallenges() {
            return challenges;
        }
    }
}
