package de.ips.creactivities.chatbot.spiderplot;

import de.ips.creactivities.chatbot.basemocks.CmsServiceMockBase;
import de.ips.creactivities.chatbot.basemocks.UserRepositoryMockBase;
import de.ips.creactivities.chatbot.cms.ICmsService;
import de.ips.creactivities.chatbot.cms.dm.Challenge;
import de.ips.creactivities.chatbot.cms.dm.Course;
import de.ips.creactivities.chatbot.cms.dm.Level;
import de.ips.creactivities.chatbot.dm.*;
import de.ips.creactivities.chatbot.i18n.I18n;
import de.ips.creactivities.chatbot.i18n.I18nService;
import de.ips.creactivities.chatbot.repo.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.io.IOException;
import java.util.*;

public class PersonalStrengthDiagramTests {

    private static Random r = null;
    private static List<Challenge> challenges = new LinkedList<>();
    private static List<Level> levels = new LinkedList<>();
    private static int seed = 0;


    @BeforeEach
    void before() {
        r = new Random(seed++);
        //r = new Random(2069);
        int gid = 0;
        challenges.clear();
        levels.clear();
        for (EvaluationType et : EvaluationType.values()) {
            List<String> challengeIdentifiers = new LinkedList<>();
            for (int i = 0; i < r.nextInt(1, 10); i++) {
                Challenge c = new Challenge(gid + "", gid + "", gid + "", r.nextBoolean(), "", et.getCmsRepresentation(), null, null, null, null, null);
                challengeIdentifiers.add(gid++ + "");
                challenges.add(c);
            }
            Level l = new Level(et.getCmsRepresentation(), "level", "descr", true, null, challengeIdentifiers, null, null);
            levels.add(l);
        }
    }

    //@Test
    @RepeatedTest(100_000)
    void testGeneration() throws IOException {
        PersonalStrengthDiagramMocked instance = new PersonalStrengthDiagramMocked(new I18nMock(), new UserMock(), new PSDCMS());
        byte[] personalStrengthDiagram = instance.createPersonalStrengthDiagram("1");
        Assertions.assertTrue(PersonalStrengthDiagramMocked.entries.size() == 4);
        for (EvaluationEntry entry : PersonalStrengthDiagramMocked.entries) {
            Assertions.assertTrue(entry.getValue() <= 6.0, "Value larger than 6: " + entry.getValue() + " Seed: " + seed);
            Assertions.assertTrue(entry.getValue() >= 1.0, "Value smaller than 1: " + entry.getValue() + " Seed: " + seed);
        }
    }


    private static class UserMock extends UserRepositoryMockBase {
        @Override
        public Optional<UserEntity> findById(String s) {
            UserEntity ue = new UserEntity();
            ue.setLanguageId("en");
            ue.setBlocked(false);
            ue.setId("1");
            List<SolutionEntity> solutions = new LinkedList<>();
            for (Challenge c : challenges) {
                if (!c.getActive()) {
                    //continue;
                }
                ChallengeEntity ce = new ChallengeEntity();
                ce.setId(c.getIdentifier());
                EvaluationType aspect = EvaluationType.findByCmsString(c.getEvaluationAspect());
                SolutionEntity se = new SolutionEntity();
                se.setBlocked(r.nextInt(10) == 0);
                se.setUser(ue);
                se.setIssuedOn(1);
                se.setChallenge(ce);
                se.setId(Long.valueOf(c.getIdentifier()));
                List<EvaluationEntity> evaluations = new LinkedList<>();
                for (int j = 0; j < r.nextInt(3); j++) {
                    EvaluationEntity evaluationEntry = new EvaluationEntity();
                    evaluationEntry.setUser(null);
                    evaluationEntry.setEvaluationScore(r.nextInt(1, 6));
                    evaluationEntry.setEvaluationType(aspect);
                    evaluationEntry.setId(1L);
                    evaluationEntry.setIssuedOn(1L);
                    evaluations.add(evaluationEntry);
                }
                se.setEvaluations(evaluations);
                solutions.add(se);
            }
            ue.setSolutions(solutions);
            return Optional.of(ue);
        }
    }

    private static class I18nMock extends I18nService {
        @Override
        public String localize(String langCode, I18n key) {
            return "blah";
        }
    }

    private static class PSDCMS extends CmsServiceMockBase {
        @Override
        public List<String> getLevelsForCourse(String languageCode, String courseIdentifier) {
            List<String> lIds = new LinkedList<>();
            for (Level l : levels) {
                lIds.add(l.getIdentifier());
            }
            return lIds;
        }

        @Override
        public List<String> getCourseIdentifiers(String languageCode) {
            return Arrays.asList("1");
        }

        @Override
        public Optional<Challenge> findChallengeById(String languageCode, String challengeIdentifier) {
            for (Challenge c : challenges) {
                if (c.getIdentifier().equals(challengeIdentifier)) {
                    return Optional.of(c);
                }
            }
            return Optional.empty();
        }

        @Override
        public List<String> getChallengesForLevel(String languageCode, String levelIdentifier) {
            List<String> ids = new LinkedList<>();
            for (Level l : levels) {
                if (l.getIdentifier().equals(levelIdentifier)) {
                    return l.getChallengeIdentifiers();
                }
            }
            return null;
        }

        @Override
        public Optional<Course> findCourseById(String languageCode, String courseIdentifier) {
            return Optional.of(new Course("1", true, null, null, null));
        }

        @Override
        public Optional<Level> findLevelById(String languageCode, String levelIdentifier) {
            return Optional.of(new Level("1", "1", "1", true, null, null, null, null));
        }
    }

    private static class PersonalStrengthDiagramMocked extends PersonalStrengthDiagram {

        public static List<EvaluationEntry> entries;

        public PersonalStrengthDiagramMocked(I18nService i18n, UserRepository userRepo, ICmsService cmsService) {
            super(i18n, userRepo, cmsService);
        }

        @Override
        public byte[] generateSpiderPlot(List<EvaluationEntry> entries, String axisName) throws IOException {
            this.entries = entries;
            return null;
        }
    }
}
