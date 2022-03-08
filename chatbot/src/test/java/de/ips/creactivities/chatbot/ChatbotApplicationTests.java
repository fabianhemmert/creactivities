package de.ips.creactivities.chatbot;

import de.ips.creactivities.chatbot.dm.*;
import de.ips.creactivities.chatbot.repo.ChallengeRepository;
import de.ips.creactivities.chatbot.repo.EvaluationRepository;
import de.ips.creactivities.chatbot.repo.SolutionRepository;
import de.ips.creactivities.chatbot.repo.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:mydb"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ChatbotApplicationTests {

    @Autowired
    ChallengeRepository challengeRepo;
    @Autowired
    SolutionRepository solutionRepository;
    @Autowired
    UserRepository userRepo;
    @Autowired
    EvaluationRepository evalRepo;


    //	@Test
    void contextLoads() {
        ChallengeEntity ce = new ChallengeEntity();
        ce.setId("challenge1");
        ce.setSolutions(new LinkedList<>());
        ce = challengeRepo.save(ce);

        UserEntity ue = new UserEntity("user1", true, null, null, "1", "de", null, null, false, new LinkedList<>(), new LinkedList<>());
        ue = userRepo.save(ue);

        SolutionEntity se = new SolutionEntity();
        se.setChallenge(ce);
        se.setUser(ue);
        se.setIssuedOn(System.currentTimeMillis());
        List<String> vals = Arrays.asList("Solution Value 1", "Solution Value 2");
        se.setSolutionValues(vals);
        se = solutionRepository.save(se);

        // Load the Challenge via findAllById
        ChallengeEntity fromPersistenceFindAll = challengeRepo.findAllById(Arrays.asList("challenge1")).iterator().next();
        // We only saved a single solution, so we expect one solution to come back.
        Assertions.assertEquals(1, fromPersistenceFindAll.getSolutions().size());
        // Now load the SAME challenge individually via findById
        ChallengeEntity fromPersistenceFindById = challengeRepo.findById("challenge1").get();
        // We expect the entity to have the exact same number of solutions like the other one, should be easy-peasy, right?
        Assertions.assertEquals(fromPersistenceFindById.getSolutions().size(), fromPersistenceFindAll.getSolutions().size());
    }

    @Test
    public void testDeleteUser() {

        UserEntity ue = new UserEntity("1", true, UserState.ON_BOARDING, "1", "1", "de", "1", null, false, new LinkedList<>(), null);
        ue = userRepo.save(ue);

        SolutionEntity se = new SolutionEntity();
        se.setUser(ue);
        se.setSolutionValues(Arrays.asList("s1"));
        se = solutionRepository.save(se);

        EvaluationEntity ee = new EvaluationEntity();
        ee.setUser(ue);
        ee.setEvaluationScore(1);
        ee = evalRepo.save(ee);

        deleteUser(ue);

        Assertions.assertTrue(userRepo.findById(ue.getId()).isEmpty());
        Assertions.assertTrue(solutionRepository.findById(se.getId()).isPresent());
        Assertions.assertTrue(evalRepo.findById(ee.getId()).isPresent());
        Assertions.assertNull(solutionRepository.findById(se.getId()).get().getUser());
        Assertions.assertNull(evalRepo.findById(ee.getId()).get().getUser());
    }

    @Transactional
    void deleteUser(UserEntity ue) {
        List<SolutionEntity> solutions = solutionRepository.findAllByUser(ue);

        for (SolutionEntity s : solutions) {
            s.setUser(null);
        }
        solutionRepository.saveAll(solutions);

        List<EvaluationEntity> evaluations = evalRepo.findAllByUser(ue);
        for (EvaluationEntity e : evaluations) {
            e.setUser(null);
        }
        evalRepo.saveAll(evaluations);
        userRepo.deleteById(ue.getId());
    }

    @Test
    public void deleteShouldWork() {

        ChallengeEntity ce = new ChallengeEntity();
        ce.setId("challenge1");
        ce.setSolutions(new LinkedList<>());
        ce = challengeRepo.save(ce);

        UserEntity ue = new UserEntity("user1", true, null, null, "1", "de", null, null, false, new LinkedList<>(), new LinkedList<>());
        ue = userRepo.save(ue);

        SolutionEntity s1 = createSolutionEntity(ce, ue, Arrays.asList("Solution Value 1", "Solution Value 2"));
        SolutionEntity s2 = createSolutionEntity(ce, ue, Arrays.asList("Solution 3"));
        s1 = solutionRepository.save(s1);
        s2 = solutionRepository.save(s2);

        List<SolutionEntity> result = solutionRepository.findAllByChallengeAndUser(ce, ue);
        Assertions.assertEquals(2, result.size());

        solutionRepository.deleteAll(result);

        List<SolutionEntity> afterDelete = solutionRepository.findAllByChallengeAndUser(ce, ue);
        Assertions.assertEquals(0, afterDelete.size(), "Solutions should be deleted but they are not.");


    }

    private SolutionEntity createSolutionEntity(ChallengeEntity ce, UserEntity ue, List<String> asList) {
        SolutionEntity se = new SolutionEntity();
        se.setChallenge(ce);
        se.setUser(ue);
        se.setSolutionValues(asList);
        se.setIssuedOn(System.currentTimeMillis());
        return se;
    }

}
