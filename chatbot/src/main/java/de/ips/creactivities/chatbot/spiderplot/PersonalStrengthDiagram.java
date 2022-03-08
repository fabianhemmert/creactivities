package de.ips.creactivities.chatbot.spiderplot;

import de.ips.creactivities.chatbot.cms.ICmsService;
import de.ips.creactivities.chatbot.cms.dm.Challenge;
import de.ips.creactivities.chatbot.dm.EvaluationEntity;
import de.ips.creactivities.chatbot.dm.EvaluationType;
import de.ips.creactivities.chatbot.dm.SolutionEntity;
import de.ips.creactivities.chatbot.dm.UserEntity;
import de.ips.creactivities.chatbot.i18n.I18n;
import de.ips.creactivities.chatbot.i18n.I18nService;
import de.ips.creactivities.chatbot.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class PersonalStrengthDiagram {

    private final I18nService i18n;

    private final UserRepository userRepo;

    private final ICmsService cmsService;

    private final static int IMAGE_SIZE = 600;

    @Transactional
    public byte[] createPersonalStrengthDiagram(String userId) throws IOException {
        Optional<UserEntity> ue = userRepo.findById(userId);
        if (ue.isEmpty()) {
            throw new IllegalArgumentException("No user found with the Id " + userId);
        }
        String lang = ue.get().getLanguageId();
        Map<EvaluationType, Double> evaluationResults = new HashMap<>();
        Map<EvaluationType, Integer> countsPerAspect = new HashMap<>();
        List<String> activeChallengeIds = new LinkedList<>();
        for (EvaluationType t : EvaluationType.values()) {
            countsPerAspect.put(t, 0);
            evaluationResults.put(t, 0.0);
        }

        // Get all challenges for everything and count them to establish the divisor.
        List<String> courseIdentifiers = cmsService.getCourseIdentifiers(lang);
        for (String ci : courseIdentifiers) {
            if (!cmsService.findCourseById(lang, ci).get().getActive()) {
                continue;
            }
            List<String> levelsForCourse = cmsService.getLevelsForCourse(lang, ci);
            for (String li : levelsForCourse) {
                if (!cmsService.findLevelById(lang, li).get().getActive()) {
                    continue;
                }
                List<String> challengesForLevel = cmsService.getChallengesForLevel(lang, li);
                for (String chi : challengesForLevel) {
                    Optional<Challenge> challenge = cmsService.findChallengeById(lang, chi);
                    if (challenge.isPresent()) {
                        if (!challenge.get().getActive()) {
                            // don't divide by challenges that are not active.
                            continue;
                        }
                        String aspect = challenge.get().getEvaluationAspect();
                        EvaluationType challengeType = EvaluationType.findByCmsString(aspect);
                        countsPerAspect.put(challengeType, countsPerAspect.get(challengeType) + 1);
                        activeChallengeIds.add(challenge.get().getIdentifier());
                    }
                }
            }
        }

        List<SolutionEntity> solutions = ue.get().getSolutions();
        for (SolutionEntity solution : solutions) {
            String challengeid = solution.getChallenge().getId();
            if (solution.isBlocked() || !activeChallengeIds.contains(challengeid)) {
                // This triggers if either the Solution was blocked by an admin OR if the challenge this solution is for is inactive.
                // A challenge is inactive if EITHER of Course, Level or Challenge are set to inactive.
                // ignore any evaluation we had.
                continue;
            }

            List<EvaluationEntity> evaluations = solution.getEvaluations();
            if (evaluations.size() > 0) {
                int scoresum = 0;
                EvaluationType dimension = null;
                for (EvaluationEntity evaluation : evaluations) {
                    dimension = evaluation.getEvaluationType();
                    scoresum += evaluation.getEvaluationScore();
                }
                double normalized = scoresum / (double) evaluations.size();
                evaluationResults.put(dimension, evaluationResults.get(dimension) + normalized);
            }
        }


        // Build the diagram
        List<EvaluationEntry> graphValues = new LinkedList<>();

        List<EvaluationType> sortedTypes = Arrays.asList(EvaluationType.VERSTEHEN, // up
                EvaluationType.VORSTELLEN, // right
                EvaluationType.MACHEN, // bottom
                EvaluationType.KRITISIEREN); // left

        for (EvaluationType type : sortedTypes) {
            // add the normalized result as a fraction divided by the number of levels that contribute to this aspect.
            double value = 1;
            if (countsPerAspect.get(type) > 0) {
                value = 1 + (evaluationResults.get(type) / countsPerAspect.get(type));
            }
            graphValues.add(new EvaluationEntry(i18n.localize(ue.get().getLanguageId(), type.getAspect()), value));
        }

        return generateSpiderPlot(graphValues, i18n.localize(ue.get().getLanguageId(), I18n.DIAGRAM_AXIS_LEGEND));

    }


    public byte[] generateSpiderPlot(List<EvaluationEntry> entries, String axisName) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (EvaluationEntry entry : entries) {
            dataset.addValue(entry.getValue(), axisName, entry.getAspect());
        }
        SpiderWebPlot plot = new SpiderWebPlot(dataset);
        plot.setSeriesPaint(new Color(248, 217, 87, 255));
        plot.setSeriesOutlineStroke(new BasicStroke(5));
        plot.setWebFillAlpha(0.7f);
        plot.setBaseSeriesPaint(new Color(248, 217, 87, 255));
        plot.setWebFilled(true);
        plot.setMaxValue(6);
        plot.setOutlineVisible(false);
        plot.setForegroundAlpha(1);
        plot.setBackgroundAlpha(0);
        plot.setLabelFont(new Font("Calibri", Font.PLAIN, 0));
        plot.setAxisLinePaint(new Color(0, 0, 0, 0));
        plot.setOutlinePaint(Color.BLUE);
        JFreeChart chart = new JFreeChart(plot);
        chart.setBackgroundPaint(Color.WHITE);
        chart.removeLegend();
        chart.setBorderVisible(false);
        chart.setBackgroundImageAlpha(1);

        try (InputStream is = new ClassPathResource("Staerkenprofil Background.png").getInputStream()) {
            Image image = ImageIO.read(is);
            chart.setBackgroundImage(image);
        }

        BufferedImage bufferedImage = chart.createBufferedImage(IMAGE_SIZE, IMAGE_SIZE);
        return ChartUtils.encodeAsPNG(bufferedImage);
    }
}
