package de.ips.creactivities.chatbot.i18n;

public enum I18n {
    TEST("test"),

    PROCESS_PRIVACY_POLICY_ACCEPT("process.privacypolicy.accept"),
    PROCESS_SEND_EVALUATION_RESULT_TO_USER_RESULT("process.sendevaluationresulttouser.result"),
    PROCESS_SEND_SET_USER_BLOCK_STATE_MESSAGE("process.setuserblockstate.message"),
    PROCESS_SEND_EVALUATION_RESULT_TO_USER_BLOCKED("process.sendevaluationresulttouser.blocked"),
    PROCESS_SEND_SOLUTION_TO_ADMINS("process.sendsolutiontoadmins.send"),

    MESSAGE_EVALUATION_SCORE_ONE("message.evaluationscore.one"),
    MESSAGE_EVALUATION_SCORE_TWO("message.evaluationscore.two"),
    MESSAGE_EVALUATION_SCORE_THREE("message.evaluationscore.three"),
    MESSAGE_EVALUATION_SCORE_FOUR("message.evaluationscore.four"),
    MESSAGE_EVALUATION_SCORE_FIVE("message.evaluationscore.five"),
    MESSAGE_EVALUATION_SCORE_REPORT("message.evaluationscore.report"),
    MESSAGE_EVALUATION_SCORE_INVALID("message.evaluationscore.invalid"),

    MESSAGE_EVALUATION_ADMIN_SOLUTION_FROM_USER("message.adminevaluation.solutionfromuser"),
    MESSAGE_EVALUATION_ADMIN_REPORTED_SOLUTION("message.adminevaluation.reportedsolution"),
    MESSAGE_EVALUATION_ADMIN_YES("message.adminevaluation.yes"),
    MESSAGE_EVALUATION_ADMIN_NO("message.adminevaluation.no"),
    MESSAGE_EVALUATION_ADMIN_APPROVED("message.adminevaluation.approved"),
    MESSAGE_EVALUATION_ADMIN_SOLUTION_BLOCKED("message.adminevaluation.solutionblocked"),
    MESSAGE_EVALUATION_ADMIN_USER_BLOCKED("message.adminevaluation.userblocked"),
    MESSAGE_EVALUATION_ADMIN_USER_BLOCK_CONFIRMATION("message.adminevaluation.userblockconfirmation"),

    MESSAGE_EVALUATION_QUESTION("message.evaluation.question"),
    MESSAGE_EVALUATION_CHALLENGE("message.evaluation.challenge"),
    MESSAGE_EVALUATION_SOLUTION("message.evaluation.solution"),

    //====== Beyond here: Johannes. Before here: Can!
    COMMAND_DELETEME_CONF_REQUEST("command.deleteme.confRequest"),
    COMMAND_DELETEME_CONFIRMATION("command.deleteme.confirmation"),
    COMMAND_STAERKENDIAGRAMM_ANSWER("command.staerkendiagram.answer"),
    COMMAND_USERNOTEXISTING("command.usernotexisting"),
    COMMAND_CONSENTNOTGIVEN("command.consentNotGiven"),
    COMMAND_WOBINICH_REFUSAL("command.wobinich.refusal"),
    COMMAND_WOBINICH_NOTINCHALLENGE("command.wobinich.notinchallenge"),
    COMMAND_WOBINICH_INLEVELPROLOGUE("command.wobinich.inlevelprologue"),
    COMMAND_WOBINICH_INLEVELEPILOGUE("command.wobinich.inlevelepilogue"),
    COMMAND_WOBINICH_INLEVEL("command.wobinich.inlevel"),
    COMMAND_WOBINICH_INCHALLENGE("command.wobinich.inchallenge"),
    COMMAND_WOBINICH_INCHALLENGEPROLOGUE("command.wobinich.inchallengeprologue"),
    COMMAND_WOBINICH_INCHALLENGEWAITINGFORSOLUTION("command.wobinich.inchallengewaitingforsolution"),
    COMMAND_WOBINICH_INCHALLENGEEVALUATING("command.wobinich.inchallengeevaluating"),
    COMMAND_WOBINICH_INCHALLENGEAWAITINGRESULT("command.wobinich.inchallengeawaitingresult"),
    COMMAND_WOBINICH_INCHALLENGEEPILOGUE("command.wobinich.inchallengeepilogue"),
    COMMAND_LANGUAGE_CHOOSELANGUAGE("command.language.chooselanguage"),
    COMMAND_LANGUAGE_CONFIRMLANGUAGE("command.language.confirmlanguage"),
    COMMAND_NOSOLUTIONSTOEVALUATE("command.evaluatependingsolution.nopendingsolution"),
    COMMAND_UNBLOCKUSER_NOTIFY("command.unblockuser.notify"),
    COMMAND_UNBLOCKUSER_NOTBLOCKED("command.unblockuser.notblocked"),
    COMMAND_UNBLOCKUSER_UNKNOWN("command.unblockuser.unknown"),
    DIAGRAM_AXIS_LEGEND("diagram.axis.legend"),
    DIAGRAM_CRITERIA_UNDERSTAND("diagram.criteria.understand"),
    DIAGRAM_CRITERIA_CRITICISM("diagram.criteria.criticism"),
    DIAGRAM_CRITERIA_IMAGINE("diagram.criteria.imagine"),
    DIAGRAM_CRITERIA_MAKE("diagram.criteria.make"),
    TEST2("test2");
    private final String key;

    private I18n(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}
