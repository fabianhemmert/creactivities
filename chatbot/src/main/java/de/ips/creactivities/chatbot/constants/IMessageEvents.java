package de.ips.creactivities.chatbot.constants;

public interface IMessageEvents {

    /**
     * Event triggered by the user when he accepts the privacy policy.
     */
    String PRIVACY_POLICY_ACCEPTED = "privacyPolicyAccepted";

    /**
     * Event triggered by the user when he accepts the terms of use.
     */
    String TERMS_OF_USE_ACCEPTED = "termsOfUseAccepted";

    /**
     * Event triggered by the user when he accepts the copy right.
     */
    String COPY_RIGHT_ACCEPTED = "copyRightAccepted";

    /**
     * Event triggered by the user when he selects a response to an interaction text.
     */
    String INTERACTION_RESPONSE = "interactionResponse";

    /**
     * Event triggered by the user when he selects a language.
     */
    String LANGUAGE_SELECTED = "langSelected";

    /**
     * Event triggered by the user when he selects a response to an epilogue text.
     */
    String EPILOGUE_RESPONSE = "epilogueResponse";

    /**
     * Event triggered by a user when he evaluates a solution.
     */
    String SOLUTION_EVALUATED = "solutionEvaluated";

    /**
     * Event triggered by the bot when he receives a solution from a user.
     */
    String SOLUTION_RECEIVED = "solutionReceived";

    /**
     * Error event triggered when we receive empty interactions or no response where we expect one.
     */
    String NO_INTERACTION_OR_RESPONSE = "noInteractionOrResponseAvailable";

    /**
     * Event triggered by an admin evaluation of a reported solution.
     */
    String ADMIN_REPORTED_EVALUATION = "ARE";

    /**
     * Event triggered by an admin evaluation of a solution.
     */
    String ADMIN_EVALUATION = "adminEvaluation";

    /**
     * Event triggered by the confirmation of a user block request in the admin group chat.
     */
    String USER_BLOCK_RESPONSE = "userBlockResponse";

}
