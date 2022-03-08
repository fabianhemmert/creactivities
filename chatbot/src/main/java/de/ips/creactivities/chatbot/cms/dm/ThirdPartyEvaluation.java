package de.ips.creactivities.chatbot.cms.dm;

import lombok.Data;

import java.util.List;

@Data
public class ThirdPartyEvaluation {

    private String initialThanks;

    private String evaluationCallToAction;

    private List<String> transitions;

    private String lastThanks;

}
