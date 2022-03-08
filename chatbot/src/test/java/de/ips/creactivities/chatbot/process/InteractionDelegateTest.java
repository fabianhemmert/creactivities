package de.ips.creactivities.chatbot.process;

import de.ips.creactivities.chatbot.basemocks.CmsServiceMockBase;
import de.ips.creactivities.chatbot.basemocks.CreactivitiesBotMock;
import de.ips.creactivities.chatbot.basemocks.MessageSenderMockBase;
import de.ips.creactivities.chatbot.cms.ICmsService;
import de.ips.creactivities.chatbot.cms.dm.Challenge;
import de.ips.creactivities.chatbot.cms.dm.Course;
import de.ips.creactivities.chatbot.cms.dm.InteractionElement;
import de.ips.creactivities.chatbot.cms.dm.Level;
import de.ips.creactivities.chatbot.constants.IMessageEvents;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.dm.UserEntity;
import de.ips.creactivities.chatbot.repo.UserRepository;
import de.ips.creactivities.chatbot.telegram.CreactivitiesBot;
import de.ips.creactivities.chatbot.telegram.DecorationAction;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.stream.Stream;

public class InteractionDelegateTest {

    private static final String IMAGE_URL = "https://www.djgummikuh.de/wp-content/uploads/2022/01/image-1.png";

    private static final String INSTANCE_ID = "instance1";

    private static final String USER_1_ID = "user1";
    private static final String CHAT_ID = "chat1";

    private InteractionDelegate delegate;

    private ICmsService cmsService;

    private UserRepository userRepository;

    private MockDelegateExecution execution;

    private Map<String, Course> courses;
    private Map<String, Level> levels;
    private Map<String, Challenge> challenges;

    private CreactivitiesBotMock botMock;


    @BeforeEach
    public void prepare() {

        botMock = new CreactivitiesBotMock();
        CreactivitiesBot.setInstance(botMock);
        courses = new HashMap<>();
        levels = new HashMap<>();
        challenges = new HashMap<>();
        Map<String, UserEntity> users = new HashMap<>();
        users.put(USER_1_ID, new UserEntity(USER_1_ID, true, null, null, CHAT_ID, "de", null, null, false, null, null));
        userRepository = new MockUserRepo(users);

        Map<String, Object> variables = new HashMap<>();
        variables.put(IProcessVariables.USER_ID, USER_1_ID);
        variables.put(IProcessVariables.CHAT_ID, CHAT_ID);
        execution = new MockDelegateExecution(variables);
        execution.setProcessInstanceId(INSTANCE_ID);

        cmsService = new MockCmsService(courses, levels, challenges);

        delegate = new InteractionDelegate();
        delegate.setMessageSender(new MockMessageSender());
        delegate.setCmsService(cmsService);
        delegate.setUserRepository(userRepository);
    }

    @Test
    public void testInteractionWithImage() throws Exception {

        execution.setVariable(IProcessVariables.INTERACTION_WORLD_DEPTH, "course");
        execution.setVariable(IProcessVariables.INTERACTION_TYPE, "prologue");

        InteractionElement prologue1 = generateInteractionElement("This is a prologue text", null);
        InteractionElement prologue2 = generateInteractionElement("This is a text with image", IMAGE_URL, "Yes", "No");

        Course course = new Course();
        course.setIdentifier("1");
        course.setActive(true);
        course.setPrologue(Arrays.asList(prologue1, prologue2));
        courses.put(course.getIdentifier(), course);

        execution.setVariable(IProcessVariables.COURSE_ID, course.getIdentifier());

        delegate.execute(execution);

        Queue<PartialBotApiMethod> calledMethods = botMock.getCalledMethods();

        Assertions.assertEquals(2, calledMethods.size());
        PartialBotApiMethod firstCall = calledMethods.poll();
        PartialBotApiMethod secondCall = calledMethods.poll();

        Assertions.assertTrue(firstCall instanceof SendMessage);
        Assertions.assertTrue(secondCall instanceof SendPhoto);

        SendMessage firstMessage = (SendMessage) firstCall;
        Assertions.assertEquals("This is a prologue text", firstMessage.getText());
        Assertions.assertNull(firstMessage.getReplyMarkup());

        SendPhoto secondMessage = (SendPhoto) secondCall;
        Assertions.assertEquals("This is a text with image", secondMessage.getCaption());
        Assertions.assertNotNull(secondMessage.getReplyMarkup());

        InlineKeyboardMarkup replyMarkup = (InlineKeyboardMarkup) secondMessage.getReplyMarkup();
        Assertions.assertEquals(1, replyMarkup.getKeyboard().size());
        Assertions.assertEquals(2, replyMarkup.getKeyboard().get(0).size());
        Assertions.assertEquals(INSTANCE_ID + "_" + IMessageEvents.INTERACTION_RESPONSE +"_a_0", replyMarkup.getKeyboard().get(0).get(0).getCallbackData());
        Assertions.assertEquals("Yes", replyMarkup.getKeyboard().get(0).get(0).getText());
        Assertions.assertEquals(INSTANCE_ID + "_" + IMessageEvents.INTERACTION_RESPONSE  +"_a_1", replyMarkup.getKeyboard().get(0).get(1).getCallbackData());
        Assertions.assertEquals("No", replyMarkup.getKeyboard().get(0).get(1).getText());

    }


    @Test
    public void testMissingInteraction() throws Exception {

        execution.setVariable(IProcessVariables.INTERACTION_WORLD_DEPTH, "course");
        execution.setVariable(IProcessVariables.INTERACTION_TYPE, "prologue");

        Course course = new Course();
        course.setIdentifier("1");
        courses.put(course.getIdentifier(), course);

        execution.setVariable(IProcessVariables.COURSE_ID, course.getIdentifier());

        Assertions.assertThrows(BpmnError.class, () -> delegate.execute(execution));
        Assertions.assertEquals(0, botMock.getCalledMethods().size());
    }

    @Test
    public void testNoAnswers() throws Exception {

        execution.setVariable(IProcessVariables.INTERACTION_WORLD_DEPTH, "course");
        execution.setVariable(IProcessVariables.INTERACTION_TYPE, "prologue");

        InteractionElement prologue1 = generateInteractionElement("This is a prologue text without any answer", null);
        InteractionElement prologue2 = generateInteractionElement("This is a second text without any answer", null);

        Course course = new Course();
        course.setIdentifier("1");
        course.setActive(true);
        course.setPrologue(Arrays.asList(prologue1, prologue2));
        courses.put(course.getIdentifier(), course);
        execution.setVariable(IProcessVariables.COURSE_ID, course.getIdentifier());
        BpmnError bpmnError = Assertions.assertThrows(BpmnError.class, () -> delegate.execute(execution));
        Assertions.assertEquals(IMessageEvents.NO_INTERACTION_OR_RESPONSE, bpmnError.getErrorCode());

        Queue<PartialBotApiMethod> calledMethods = botMock.getCalledMethods();

        Assertions.assertEquals(2, calledMethods.size());
        PartialBotApiMethod firstCall = calledMethods.poll();
        PartialBotApiMethod secondCall = calledMethods.poll();

        Assertions.assertTrue(firstCall instanceof SendMessage);
        Assertions.assertTrue(secondCall instanceof SendMessage);

        SendMessage firstMessage = (SendMessage) firstCall;
        Assertions.assertEquals("This is a prologue text without any answer", firstMessage.getText());
        Assertions.assertNull(firstMessage.getReplyMarkup());

        SendMessage secondMessage = (SendMessage) secondCall;
        Assertions.assertEquals("This is a second text without any answer", secondMessage.getText());
        Assertions.assertNull(secondMessage.getReplyMarkup());
    }

    @ParameterizedTest
    @MethodSource("provideMultiInteractionTestdata")
    public void testMultiInteraction(String worldDepth, boolean isPrologue, List<InteractionElement> interactions) throws Exception {

        // PREPARATION:

        execution.setVariable(IProcessVariables.COURSE_ID, "1");
        execution.setVariable(IProcessVariables.LEVEL_ID, "2");
        execution.setVariable(IProcessVariables.CHALLENGE_ID, "3");

        execution.setVariable(IProcessVariables.INTERACTION_WORLD_DEPTH, worldDepth);
        execution.setVariable(IProcessVariables.INTERACTION_TYPE, isPrologue ?
                IProcessVariables.INTERACTION_TYPE_PROLOGUE : IProcessVariables.INTERACTION_TYPE_EPILOGUE);

        if (IProcessVariables.INTERACTION_WORLD_DEPTH_COURSE.equals(worldDepth)) {
            Course course = new Course();
            course.setIdentifier("1");
            course.setActive(true);
            if (isPrologue) {
                course.setPrologue(interactions);
            } else {
                course.setEpilogue(interactions);
            }
            courses.put(course.getIdentifier(), course);
        } else if (IProcessVariables.INTERACTION_WORLD_DEPTH_LEVEL.equals(worldDepth)) {
            Level level = new Level();
            level.setIdentifier("2");
            level.setActive(true);
            if (isPrologue) {
                level.setPrologue(interactions);
            } else {
                level.setEpilogue(interactions);
            }
            levels.put(level.getIdentifier(), level);
        } else {
            Challenge challenge = new Challenge();
            challenge.setIdentifier("3");
            challenge.setActive(true);
            if (isPrologue) {
                challenge.setPrologue(interactions);
            } else {
                challenge.setEpilogue(interactions);
            }
            challenges.put(challenge.getIdentifier(), challenge);
        }

        // EXECUTION:
        delegate.execute(execution);

        // ASSERTION:
        Queue<PartialBotApiMethod> calledMethods = botMock.getCalledMethods();

        Assertions.assertEquals(2, calledMethods.size());
        PartialBotApiMethod firstCall = calledMethods.poll();
        PartialBotApiMethod secondCall = calledMethods.poll();

        Assertions.assertTrue(firstCall instanceof SendMessage);
        Assertions.assertTrue(secondCall instanceof SendMessage);

        SendMessage firstMessage = (SendMessage) firstCall;
        Assertions.assertEquals("This is a prologue text without any answer", firstMessage.getText());
        Assertions.assertNull(firstMessage.getReplyMarkup());

        SendMessage secondMessage = (SendMessage) secondCall;
        Assertions.assertEquals("This is a text with 2 answers", secondMessage.getText());
        Assertions.assertNotNull(secondMessage.getReplyMarkup());
        InlineKeyboardMarkup replyMarkup = (InlineKeyboardMarkup) secondMessage.getReplyMarkup();
        Assertions.assertEquals(1, replyMarkup.getKeyboard().size());
        Assertions.assertEquals(2, replyMarkup.getKeyboard().get(0).size());
        Assertions.assertEquals(INSTANCE_ID + "_" + IMessageEvents.INTERACTION_RESPONSE  +"_a_0", replyMarkup.getKeyboard().get(0).get(0).getCallbackData());
        Assertions.assertEquals("Yes", replyMarkup.getKeyboard().get(0).get(0).getText());
        Assertions.assertEquals(INSTANCE_ID + "_" + IMessageEvents.INTERACTION_RESPONSE  +"_a_1", replyMarkup.getKeyboard().get(0).get(1).getCallbackData());
        Assertions.assertEquals("No", replyMarkup.getKeyboard().get(0).get(1).getText());
    }

    /**
     * Test data for testMultiInteraction. Interaction list has 3 elements. The first one without any answer.
     * The second one with 2 answers and the last one without any answer. When the {@link InteractionDelegate} is called,
     * we expect the first 2 messages to be sent to the bot and the third one not. The test is triggered 6 times with this interaction list: <br>
     * 1. Course Prologue <br>
     * 2. Course Epilogue <br>
     * 3. Level Prologue <br>
     * 4. Level Epilogue <br>
     * 5. Challenge Prologue <br>
     * 6. Challenge Epilogue <br>
     *
     * @return the test data.
     */
    private static Stream<Arguments> provideMultiInteractionTestdata() {

        InteractionElement interaction1 = generateInteractionElement("This is a prologue text without any answer", null);
        InteractionElement interaction2 = generateInteractionElement("This is a text with 2 answers", null, "Yes", "No");
        InteractionElement interaction3 = generateInteractionElement("This text should not be sent.", null, null);

        List<InteractionElement> list = Arrays.asList(interaction1, interaction2, interaction3);

        return Stream.of(
                Arguments.of(IProcessVariables.INTERACTION_WORLD_DEPTH_COURSE, true, list),
                Arguments.of(IProcessVariables.INTERACTION_WORLD_DEPTH_COURSE, false, list),
                Arguments.of(IProcessVariables.INTERACTION_WORLD_DEPTH_LEVEL, true, list),
                Arguments.of(IProcessVariables.INTERACTION_WORLD_DEPTH_LEVEL, false, list),
                Arguments.of(IProcessVariables.INTERACTION_WORLD_DEPTH_CHALLENGE, true, list),
                Arguments.of(IProcessVariables.INTERACTION_WORLD_DEPTH_CHALLENGE, false, list)
        );
    }

    private static InteractionElement generateInteractionElement(String text, String image, String... userAnswers) {

        InteractionElement element = new InteractionElement();
        element.setText(text);
        element.setImage(image);
        if (userAnswers != null) {
            element.setUserAnswers(Arrays.stream(userAnswers).toList());
        }

        return element;
    }

    private static class MockCmsService extends CmsServiceMockBase {
        private final Map<String, Course> courseMap;
        private final Map<String, Level> levelMap;
        private final Map<String, Challenge> challengeMap;

        public MockCmsService(Map<String, Course> courseMap, Map<String, Level> levelMap, Map<String, Challenge> challengeMap) {
            this.courseMap = courseMap;
            this.levelMap = levelMap;
            this.challengeMap = challengeMap;
        }

        @Override
        public Optional<Course> findCourseById(String languageCode, String courseIdentifier) {
            return Optional.ofNullable(courseMap.get(courseIdentifier));
        }

        @Override
        public Optional<Challenge> findChallengeById(String languageCode, String challengeIdentifier) {
            return Optional.ofNullable(challengeMap.get(challengeIdentifier));
        }

        @Override
        public Optional<Level> findLevelById(String languageCode, String levelIdentifier) {
            return Optional.ofNullable(levelMap.get(levelIdentifier));
        }

    }
    private static class MockMessageSender extends MessageSenderMockBase {
        @Override
        public void setAction(String chatId, DecorationAction action) throws TelegramApiException {
            // Do nothing
        }
    }

}
