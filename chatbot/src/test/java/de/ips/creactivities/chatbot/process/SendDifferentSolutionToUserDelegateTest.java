package de.ips.creactivities.chatbot.process;

import de.ips.creactivities.chatbot.basemocks.CmsServiceMockBase;
import de.ips.creactivities.chatbot.basemocks.CreactivitiesBotMock;
import de.ips.creactivities.chatbot.basemocks.SolutionRepositoryBaseMock;
import de.ips.creactivities.chatbot.cms.ICmsService;
import de.ips.creactivities.chatbot.cms.dm.*;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.dm.ChallengeEntity;
import de.ips.creactivities.chatbot.dm.EvaluationEntity;
import de.ips.creactivities.chatbot.dm.SolutionEntity;
import de.ips.creactivities.chatbot.dm.UserEntity;
import de.ips.creactivities.chatbot.i18n.I18nService;
import de.ips.creactivities.chatbot.repo.UserRepository;
import de.ips.creactivities.chatbot.telegram.CreactivitiesBot;
import de.ips.creactivities.chatbot.telegram.MessageSender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.*;

public class SendDifferentSolutionToUserDelegateTest {

    private static final String INSTANCE_ID = "instance1";

    private static final String USER_1_ID = "user1";
    private static final String CHAT_ID = "chat1";
    private static final String CHALLENGE_ID = "1";

    private SendDifferentSolutionToUserDelegate delegate;

    private UserRepository userRepository;

    private MockDelegateExecution execution;

    private CreactivitiesBotMock botMock;

    private MockSolutionRepository mockSolutionRepository;

    private Map<Long, SolutionEntity> solutions;

    private List<String> adminList;

    private Map<String, UserEntity> users;

    private SolutionEntity adminSolution;

    private ChallengeEntity completedChallenge;

    private ICmsService cmsService;

    private Map<String, Challenge> challengeMap;

    @BeforeEach
    public void prepare() {

        TemplateSolution solution = new TemplateSolution("Musterlösung.", null);
        challengeMap = new HashMap<>();
        challengeMap.put(CHALLENGE_ID, new Challenge(CHALLENGE_ID, "Challenge 1", "Description", true, "Evaluation Question", "understand", Arrays.asList(solution), null, null, null, null));

        adminList = new ArrayList<>();
        cmsService = new MockCmsService(challengeMap, adminList);

        botMock = new CreactivitiesBotMock();
        CreactivitiesBot.setInstance(botMock);
        solutions = new HashMap<>();

        users = new HashMap<>();
        completedChallenge = new ChallengeEntity();
        completedChallenge.setId("1");

        adminSolution = new SolutionEntity();
        UserEntity adminUser = new UserEntity("admin", true, null, null, CHAT_ID, "de", null, null, false,Arrays.asList(adminSolution), null);
        adminSolution.setId(1L);
        adminSolution.setSolutionValues(Arrays.asList("This is a solution by an admin"));
        adminSolution.setUser(adminUser);
        adminSolution.setIssuedOn(1);
        adminSolution.setChallenge(completedChallenge);
        solutions.put(adminSolution.getId(), adminSolution);

        SolutionEntity userSolution = new SolutionEntity();
        UserEntity user = new UserEntity(USER_1_ID, true, null, null, CHAT_ID, "de", null, null, false, Arrays.asList(userSolution), null);
        userSolution.setId(2L);
        userSolution.setSolutionValues(Arrays.asList("Solution by user"));
        userSolution.setUser(user);
        userSolution.setIssuedOn(2);
        userSolution.setChallenge(completedChallenge);
        solutions.put(userSolution.getId(), userSolution);

        completedChallenge.setSolutions(Arrays.asList(adminSolution, userSolution));

        users.put(USER_1_ID, user);
        users.put(adminUser.getId(), adminUser);
        adminList.add(adminUser.getId());

        userRepository = new MockUserRepo(users);
        mockSolutionRepository = new MockSolutionRepository(solutions);

        Map<String, Object> variables = new HashMap<>();
        variables.put(IProcessVariables.USER_ID, USER_1_ID);
        variables.put(IProcessVariables.CHALLENGE_ID, CHALLENGE_ID);
        execution = new MockDelegateExecution(variables);
        execution.setProcessInstanceId(INSTANCE_ID);

        delegate = new SendDifferentSolutionToUserDelegate();

        delegate.setUserRepository(userRepository);
        delegate.setSolutionRepository(mockSolutionRepository);

        MessageSender messageSender = new MessageSender();
        messageSender.setUserRepository(userRepository);
        messageSender.setI18nService(new I18nService());

        delegate.setMessageSender(messageSender);
        delegate.setCmsService(cmsService);

    }


    @Test
    public void testGetAdminSolution() throws Exception {

        // GIVEN: One Completed challenge with 2 Solutions: One admin Solution and one Solution of the current User
        // -> See BeforeEach

        // WHEN: The delegate is called
        delegate.execute(execution);

        // THEN: We expect the Admin Solution to be send to the user
        Assertions.assertEquals(2, botMock.getCalledMethods().size());
        SendMessage sendMessage = (SendMessage) botMock.getCalledMethods().poll();
        Assertions.assertEquals(adminSolution.getSolutionValues().get(0), sendMessage.getText());
        sendMessage = (SendMessage) botMock.getCalledMethods().poll();
        Assertions.assertEquals(challengeMap.get(CHALLENGE_ID).getEvaluationQuestion(), sendMessage.getText());
    }

    @Test
    public void testDoNotTakeSolutionsThatIAlreadyEvaluated() throws Exception {

        // GIVEN: One completed challenge with 3 Solutions: One admin, one of the current user and one of a different user
        // AND: The solution of the different user is already evaluated by me
        SolutionEntity solution = new SolutionEntity();
        EvaluationEntity evaluation = new EvaluationEntity();
        evaluation.setUser(users.get(USER_1_ID));

        UserEntity user = new UserEntity("newUser", true, null, null, CHAT_ID, "de", null, null, false, Arrays.asList(solution), null);
        solution.setId(3L);
        solution.setUser(user);
        solution.setSolutionValues(Arrays.asList("New Solution"));
        solution.setChallenge(completedChallenge);
        solution.setIssuedOn(3);
        solution.setEvaluations(Arrays.asList(evaluation));
        solutions.put(solution.getId(), solution);

        // WHEN: The delegate is called
        delegate.execute(execution);

        // THEN: We expect the Admin Solution to be send to the user, because we already evaluated the other solution
        Assertions.assertEquals(2, botMock.getCalledMethods().size());;
        SendMessage sendMessage = (SendMessage) botMock.getCalledMethods().poll();
        Assertions.assertEquals(adminSolution.getSolutionValues().get(0), sendMessage.getText());
        sendMessage = (SendMessage) botMock.getCalledMethods().poll();
        Assertions.assertEquals(challengeMap.get(CHALLENGE_ID).getEvaluationQuestion(), sendMessage.getText());
    }


    @Test
    public void testWithThirdPartyEvaluationsFirstCall() throws Exception {

        // GIVEN: The challenge has configured third party evaluations
        ThirdPartyEvaluation tpe = new ThirdPartyEvaluation();
        tpe.setInitialThanks("Initial Thanks");
        tpe.setEvaluationCallToAction("Call to Action");
        tpe.setTransitions(Arrays.asList("Transition 1", "Transition 2"));
        challengeMap.get(CHALLENGE_ID).setThirdpartyEvaluation(tpe);

        // WHEN: The delegate is called
        delegate.execute(execution);

        // THEN: We expect the new Solution to be send to the user
        Assertions.assertEquals(4, botMock.getCalledMethods().size());
        SendMessage sendMessage = (SendMessage) botMock.getCalledMethods().poll();
        Assertions.assertEquals(tpe.getInitialThanks(), sendMessage.getText());
        sendMessage = (SendMessage) botMock.getCalledMethods().poll();
        Assertions.assertEquals(tpe.getEvaluationCallToAction(), sendMessage.getText());
        sendMessage = (SendMessage) botMock.getCalledMethods().poll();
        Assertions.assertEquals(adminSolution.getSolutionValues().get(0), sendMessage.getText());
        sendMessage = (SendMessage) botMock.getCalledMethods().poll();
        Assertions.assertEquals(challengeMap.get(CHALLENGE_ID).getEvaluationQuestion(), sendMessage.getText());
    }


    @Test
    public void testWithThirdPartyEvaluationsSecondCall() throws Exception {

        // GIVEN: The challenge has configured third party evaluations
        ThirdPartyEvaluation tpe = new ThirdPartyEvaluation();
        tpe.setInitialThanks("Initial Thanks");
        tpe.setEvaluationCallToAction("Call to Action");
        tpe.setTransitions(Arrays.asList("Transition 1", "Transition 2"));
        challengeMap.get(CHALLENGE_ID).setThirdpartyEvaluation(tpe);

        // WHEN: The delegate is called with index 0
        execution.setVariable(IProcessVariables.THIRD_PARTY_EVALUATION_INDEX, 0);
        delegate.execute(execution);

        // THEN: We expect the new Solution to be send to the user with Transition "Transition 1"
        Assertions.assertEquals(3, botMock.getCalledMethods().size());
        SendMessage sendMessage = (SendMessage) botMock.getCalledMethods().poll();
        Assertions.assertEquals("Transition 1", sendMessage.getText());
        sendMessage = (SendMessage) botMock.getCalledMethods().poll();
        Assertions.assertEquals(adminSolution.getSolutionValues().get(0), sendMessage.getText());
        sendMessage = (SendMessage) botMock.getCalledMethods().poll();
        Assertions.assertEquals(challengeMap.get(CHALLENGE_ID).getEvaluationQuestion(), sendMessage.getText());
    }


    @Test
    public void testWithThirdPartyEvaluationsThirdCall() throws Exception {

        // GIVEN: The challenge has configured third party evaluations
        ThirdPartyEvaluation tpe = new ThirdPartyEvaluation();
        tpe.setInitialThanks("Initial Thanks");
        tpe.setEvaluationCallToAction("Call to Action");
        tpe.setTransitions(Arrays.asList("Transition 1", "Transition 2"));
        challengeMap.get(CHALLENGE_ID).setThirdpartyEvaluation(tpe);

        // WHEN: The delegate is called with index 1
        execution.setVariable(IProcessVariables.THIRD_PARTY_EVALUATION_INDEX, 1);
        delegate.execute(execution);

        // THEN: We expect the new Solution to be send to the user with transition "Transition 2"
        Assertions.assertEquals(3, botMock.getCalledMethods().size());
        SendMessage sendMessage = (SendMessage) botMock.getCalledMethods().poll();
        Assertions.assertEquals("Transition 2", sendMessage.getText());
        sendMessage = (SendMessage) botMock.getCalledMethods().poll();
        Assertions.assertEquals(adminSolution.getSolutionValues().get(0), sendMessage.getText());
        sendMessage = (SendMessage) botMock.getCalledMethods().poll();
        Assertions.assertEquals(challengeMap.get(CHALLENGE_ID).getEvaluationQuestion(), sendMessage.getText());
    }


    private static class MockSolutionRepository extends SolutionRepositoryBaseMock {
        private Map<Long, SolutionEntity> solutions;

        public MockSolutionRepository(Map<Long, SolutionEntity> solutions) {
            this.solutions = solutions;
        }

        @Override
        public <S extends SolutionEntity> S save(S entity) {
            this.solutions.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public List<SolutionEntity> findAllByUserIsNotAndChallengeIsIn(UserEntity user, Collection<ChallengeEntity> challenges) {

            List<SolutionEntity> result = new LinkedList<>();

            for (Map.Entry<Long, SolutionEntity> entrySet : solutions.entrySet()) {
                SolutionEntity solution = entrySet.getValue();

                if (!solution.getUser().equals(user) && challenges.contains(solution.getChallenge())) {
                    result.add(solution);
                }
            }

            return result;
        }

        public Map<Long, SolutionEntity> getSolutions() {
            return solutions;
        }
    }

    private static class MockCmsService extends CmsServiceMockBase {
        private final Map<String, Challenge> challengeMap;
        private final List<String> admins;

        public MockCmsService(Map<String, Challenge> challengeMap, List<String> admins) {
            this.challengeMap = challengeMap;
            this.admins = admins;
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

        @Override
        public List<String> getAdminIds() {
            return admins;
        }
    }
}
