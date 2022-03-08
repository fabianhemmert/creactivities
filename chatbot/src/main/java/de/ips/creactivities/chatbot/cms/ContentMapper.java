package de.ips.creactivities.chatbot.cms;

import de.ips.creactivities.chatbot.cms.dm.*;
import de.ips.creactivities.chatbot.cms.dto.CmsReferenceDto;
import de.ips.creactivities.chatbot.cms.dto.challenge.*;
import de.ips.creactivities.chatbot.cms.dto.course.CourseDto;
import de.ips.creactivities.chatbot.cms.dto.course.CourseEpilogueElement;
import de.ips.creactivities.chatbot.cms.dto.course.CoursePrologueElement;
import de.ips.creactivities.chatbot.cms.dto.level.*;

import java.util.ArrayList;
import java.util.List;

public class ContentMapper {


    public List<Course> convertCourseDtosToModel(List<CourseDto> dtos) {

        List<Course> courses = new ArrayList<>();

        if (dtos != null) {
            for (CourseDto dto : dtos) {

                Course course = convertCourseDtoToModel(dto);
                courses.add(course);
            }
        }

        return courses;
    }

    private Course convertCourseDtoToModel(CourseDto dto) {

        Course course = new Course();

        course.setIdentifier(String.valueOf(dto.getId()));
        course.setActive(dto.getActive());

        course.setPrologue(convertCoursePrologueToModel(dto.getCoursePrologue()));
        course.setEpilogue(convertCourseEpilogueToModel(dto.getCourseEpilogue()));

        course.setLevelIdentifiers(convertCmsReferenceDtosToModel(dto.getLevels()));

        return course;
    }

    public List<Level> convertLevelDtosToModel(List<LevelDto> dtos) {
        List<Level> levels = new ArrayList<>();

        if (dtos != null) {

            for (LevelDto levelDto : dtos) {

                Level level = convertLevelDtoToModel(levelDto);
                levels.add(level);
            }
        }

        return levels;
    }


    private Level convertLevelDtoToModel(LevelDto dto) {

        Level level = new Level();

        level.setIdentifier(String.valueOf(dto.getId()));
        level.setName(dto.getName());
        level.setDescription(dto.getDescription());
        level.setActive(dto.getActive());
        level.setImage(dto.getImage());

        level.setPrologue(convertLevelPrologueToModel(dto.getLevelPrologue()));
        level.setEpilogue(convertLevelEpilogueToModel(dto.getLevelEpilogue()));

        level.setChallengeIdentifiers(convertCmsReferenceDtosToModel(dto.getChallenges()));

        return level;

    }

    private List<String> convertCmsReferenceDtosToModel(List<CmsReferenceDto> dtos) {
        List<String> identifiers = new ArrayList<>();

        if (dtos != null) {
            for (CmsReferenceDto dto : dtos) {
                Integer id = dto.getId();
                if (id != null) {
                    identifiers.add(String.valueOf(id));
                }
            }
        }

        return identifiers;
    }

    private List<InteractionElement> convertCoursePrologueToModel(List<CoursePrologueElement> coursePrologueElements) {

        List<InteractionElement> interactionElements = new ArrayList<>();

        if (coursePrologueElements != null) {

            for (CoursePrologueElement e : coursePrologueElements) {
                InteractionElement interactionElement = new InteractionElement();

                interactionElement.setText(e.getText());
                interactionElement.setImage(e.getImage());
                interactionElement.setUserAnswers(e.getUserAnswers());
                interactionElement.setDelay(convertDelay(e.getDelay()));

                interactionElements.add(interactionElement);
            }
        }

        return interactionElements;
    }


    private List<InteractionElement> convertCourseEpilogueToModel(List<CourseEpilogueElement> courseEpilogueElements) {

        List<InteractionElement> interactionElements = new ArrayList<>();

        if (courseEpilogueElements != null) {
            for (CourseEpilogueElement e : courseEpilogueElements) {
                InteractionElement interactionElement = new InteractionElement();

                interactionElement.setText(e.getText());
                interactionElement.setImage(e.getImage());
                interactionElement.setUserAnswers(e.getUserAnswers());
                interactionElement.setDelay(convertDelay(e.getDelay()));

                interactionElements.add(interactionElement);
            }
        }

        return interactionElements;

    }

    private List<InteractionElement> convertLevelPrologueToModel(List<LevelPrologueElement> levelPrologueElements) {

        List<InteractionElement> interactionElements = new ArrayList<>();

        if (levelPrologueElements != null) {
            for (LevelPrologueElement e : levelPrologueElements) {
                InteractionElement interactionElement = new InteractionElement();

                interactionElement.setText(e.getText());
                interactionElement.setImage(e.getImage());
                interactionElement.setUserAnswers(convertLevelPrologueAnswers(e.getUserAnswers()));
                interactionElement.setDelay(convertDelay(e.getDelay()));

                interactionElements.add(interactionElement);
            }
        }

        return interactionElements;
    }


    private List<InteractionElement> convertLevelEpilogueToModel(List<LevelEpilogueElement> levelEpilogueElements) {

        List<InteractionElement> interactionElements = new ArrayList<>();

        if (levelEpilogueElements != null) {
            for (LevelEpilogueElement e : levelEpilogueElements) {
                InteractionElement interactionElement = new InteractionElement();

                interactionElement.setText(e.getText());
                interactionElement.setImage(e.getImage());
                interactionElement.setUserAnswers(convertLevelEpilogueAnswers(e.getUserAnswers()));
                interactionElement.setDelay(convertDelay(e.getDelay()));

                interactionElements.add(interactionElement);
            }
        }

        return interactionElements;

    }

    private List<InteractionElement> convertChallengePrologueToModel(List<ChallengePrologueElement> challengePrologueElements) {

        List<InteractionElement> interactionElements = new ArrayList<>();

        if (challengePrologueElements != null) {
            for (ChallengePrologueElement e : challengePrologueElements) {
                InteractionElement interactionElement = new InteractionElement();

                interactionElement.setText(e.getText());
                interactionElement.setImage(e.getImage());
                interactionElement.setUserAnswers(convertChallengePrologueAnswers(e.getUserAnswers()));
                interactionElement.setDelay(convertDelay(e.getDelay()));

                interactionElements.add(interactionElement);

            }
        }

        return interactionElements;
    }

    private List<InteractionElement> convertChallengeEpilogueToModel(List<ChallengeEpilogueElement> challengeEpilogueElements) {

        List<InteractionElement> interactionElements = new ArrayList<>();

        if (challengeEpilogueElements != null) {
            for (ChallengeEpilogueElement e : challengeEpilogueElements) {
                InteractionElement interactionElement = new InteractionElement();

                interactionElement.setText(e.getText());
                interactionElement.setImage(e.getImage());
                interactionElement.setUserAnswers(convertChallengeEpilogueAnswers(e.getUserAnswers()));
                interactionElement.setDelay(convertDelay(e.getDelay()));

                interactionElements.add(interactionElement);
            }
        }

        return interactionElements;

    }

    private Integer convertDelay(String delay) {

        Integer result = null;
        try {
            result = Integer.valueOf(delay);
        } catch (NumberFormatException e) {
            result = 0;
        }
        return result;
    }

    private List<String> convertChallengeEpilogueAnswers(List<ChallengeEpilogueAnswer> userAnswers) {
        List<String> answers = new ArrayList<>();

        for (ChallengeEpilogueAnswer userAnswer : userAnswers) {
            answers.add(userAnswer.getAnswer());
        }

        return answers;
    }


    private List<String> convertChallengePrologueAnswers(List<ChallengePrologueAnswer> userAnswers) {
        List<String> answers = new ArrayList<>();

        for (ChallengePrologueAnswer userAnswer : userAnswers) {
            answers.add(userAnswer.getAnswer());
        }

        return answers;
    }

    private List<String> convertLevelPrologueAnswers(List<LevelPrologueAnswer> userAnswers) {
        List<String> answers = new ArrayList<>();

        for (LevelPrologueAnswer userAnswer : userAnswers) {
            answers.add(userAnswer.getAnswer());
        }

        return answers;
    }

    private List<String> convertLevelEpilogueAnswers(List<LevelEpilogueAnswer> userAnswers) {
        List<String> answers = new ArrayList<>();

        for (LevelEpilogueAnswer userAnswer : userAnswers) {
            answers.add(userAnswer.getAnswer());
        }

        return answers;
    }

    public List<Challenge> convertChallengeDtosToModel(List<ChallengeDto> dtos) {
        List<Challenge> challenges = new ArrayList<>();

        if (dtos != null) {
            for (ChallengeDto challengeDto : dtos) {

                Challenge challenge = convertChallengeDtoToModel(challengeDto);
                challenges.add(challenge);
            }
        }

        return challenges;
    }


    private Challenge convertChallengeDtoToModel(ChallengeDto dto) {
        Challenge challenge = new Challenge();

        challenge.setIdentifier(String.valueOf(dto.getId()));
        challenge.setName(dto.getTitle());
        challenge.setDescription(dto.getDescription());
        challenge.setImage(dto.getImage());
        challenge.setActive(dto.getActive());

        challenge.setEvaluationQuestion(dto.getEvaluationQuestion());
        challenge.setEvaluationAspect(dto.getEvaluationAspect().getValue());
        challenge.setThirdpartyEvaluation(convertChallengeDtoToThirdPartyEvaluation(dto));

        challenge.setTemplateSolutions(convertChallengeSolutionsToModel(dto.getTemplateSolutions()));

        challenge.setPrologue(convertChallengePrologueToModel(dto.getChallengePrologue()));
        challenge.setEpilogue(convertChallengeEpilogueToModel(dto.getChallengeEpilogue()));


        return challenge;
    }

    private ThirdPartyEvaluation convertChallengeDtoToThirdPartyEvaluation(ChallengeDto dto) {

        ThirdPartyEvaluation thirdPartyEvaluation = new ThirdPartyEvaluation();

        thirdPartyEvaluation.setInitialThanks(dto.getInitialThanks());
        thirdPartyEvaluation.setEvaluationCallToAction(dto.getEvaluationCallToAction());
        thirdPartyEvaluation.setTransitions(convertChallengeDtoEvaluationTransitions(dto.getTransitions()));
        thirdPartyEvaluation.setLastThanks(dto.getLastThanks());

        return thirdPartyEvaluation;
    }

    private List<String> convertChallengeDtoEvaluationTransitions(List<EvaluationTransitionDto> dtos) {
        List<String> transitions = new ArrayList<>();

        if (dtos != null) {
            for (EvaluationTransitionDto dto : dtos) {
                transitions.add(dto.getTransition());
            }
        }

        return transitions;
    }

    private List<TemplateSolution> convertChallengeSolutionsToModel(List<ChallengeSolutionDto> dtos) {
        List<TemplateSolution> solutions = new ArrayList<>();

        if (dtos != null) {
            for (ChallengeSolutionDto dto : dtos) {
                solutions.add(convertChallengeSolutionToModel(dto));
            }
        }

        return solutions;
    }

    private TemplateSolution convertChallengeSolutionToModel(ChallengeSolutionDto dto) {
        TemplateSolution templateSolution = new TemplateSolution();

        templateSolution.setText(dto.getText());
        templateSolution.setImage(dto.getImage());

        return templateSolution;
    }

}
