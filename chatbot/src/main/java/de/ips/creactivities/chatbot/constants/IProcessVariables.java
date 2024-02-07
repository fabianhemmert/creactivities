package de.ips.creactivities.chatbot.constants;

public interface IProcessVariables {

    /**
     * Name of String variable of the chat id.
     */
    String CHAT_ID = "chatId";

    /**
     * Name of String variable of the user id.
     */
    String USER_ID = "userId";

    /**
     * Name of String variable of the course id.
     */
    String COURSE_ID = "courseId";

    /**
     * Name of String variable of the level id.
     */
    String LEVEL_ID = "levelId";

    /**
     * Name of String variable of the challenge id.
     */
    String CHALLENGE_ID = "challengeId";

    /**
     * Name of long variable of the solution id.
     */
    String SOLUTION_ID = "solutionId";

    /**
     * Name of int variable of the solution score.
     */
    String SOLUTION_SCORE = "score";

    /**
     * Name of int variable of the admin evaluation value. <br>
     * 0: Solution approved<br>
     * 1: Solution blocked<br>
     * 2: Solution and User blocked<br>
     */
    String ADMIN_EVAL_VALUE = "eval";

    /**
     * Name of int variable of the admin block user response value. <br>
     * 0: Do not block user<br>
     * 1: Block user<br>
     */
    String ADMIN_BLOCKUSER_VALUE = "block";

    /**
     * Name of string variable of the language.
     */
    String LANGUAGE = "lang";

    /**
     * Name of boolean variable to check whether we reached the end of the interaction.
     */
    String END_OF_INTERACTION = "endOfInteraction";

    /**
     * Name of boolean variable to check whether we reached the end of the epilogue.
     */
    String END_OF_EPILOGUE = "endOfEpilogue";

    /**
     * Name of boolean variable to check whether we reached the end of challenges for this level.
     */
    String END_OF_CHALLENGES = "endOfChallenges";

    /**
     * Name of the string variable to decide on the interaction type (prologue,epilogue).
     */
    String INTERACTION_TYPE = "interactionType";
    String INTERACTION_TYPE_PROLOGUE = "prologue";
    String INTERACTION_TYPE_EPILOGUE = "epilogue";

    /**
     * Name of the string variable to decide on what depth of the world the interaction takes place (course,level,challenge).
     */
    String INTERACTION_WORLD_DEPTH = "interactionWorldDepth";
    String INTERACTION_WORLD_DEPTH_COURSE = "course";
    String INTERACTION_WORLD_DEPTH_LEVEL = "level";
    String INTERACTION_WORLD_DEPTH_CHALLENGE = "challenge";

    /**
     * Name of the int variable of the current interaction element index.
     */
    String INTERACTION_ELEMENT_INDEX = "interactionElementIndex";

    String INTERACTION_REQUIRES_LONG_DELAY = "requiresLongDelay";
    String INTERACTION_CURRENT_DELAY = "currentInteractionDelay";

    String INTERACTION_REQUIRES_USER_RESPONSE = "requiresUserResponse";

    /**
     * Name of a set of user ids that have already been asked for a solution.
     **/
    String SOLUTION_ASKED_USERS = "solutionAskedUsers";

    /**
     * Name of a set of user ids that are to be asked for evaluation of a solution.
     **/
    String SOLTUION_USERS_TO_ASK = "solutionUsersToAsk";

    /**
     * Name of int variable to check whether a solution has enough evaluations: <br>
     * 0: Not enough evaluations <br>
     * 1: Enough evaluations <br>
     * 2: Timeout -> Admin Evaluations Required <br>
     */
    String ENOUGH_EVALUATIONS = "enoughEvaluations";

    /**
     * Name of long variable of a different solution id the user has to evaluate.
     */
    String DIFFERENT_SOLUTION_ID_TO_EVALUATE = "differentSolutionIdToEvaluate";

    /**
     * Name of boolean variable to check whether we reached the end of levels for this course.
     */
    String END_OF_LEVELS = "endOfLevels";

    /**
     * Name of boolean variable to check whether we reached the end of courses for this course.
     */
    String END_OF_COURSES = "endOfCourses";

    /**
     * Name of boolean variable to check whether we reached the end of evaluations for a user in a challenge.
     */
    String END_OF_THIRD_PARTY_EVALUATIONS = "endOfThirdPartyEvaluations";

    /**
     * Name of Integer variable of the current transition index of third party evaluations.
     */
    String THIRD_PARTY_EVALUATION_INDEX = "thirdPartyEvaluationIndex";

    String REPORTED_SOLUTION_ID = "reportedSolutionId";

    /**
     * Name of int Variable of the evaluation loop counter in the challenge process.
     */
    String EVALUATION_LOOP_COUNTER = "evalLoopCounter";
}
