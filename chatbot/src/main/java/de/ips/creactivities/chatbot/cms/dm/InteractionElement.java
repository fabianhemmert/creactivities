package de.ips.creactivities.chatbot.cms.dm;

import lombok.Data;

import java.util.List;

@Data
public class InteractionElement {

    /**
     * Mandatory.
     */
    private String text;

    /**
     * Optional
      */
    private String image;

    /**
     * Optional user answers
     */
    private List<String> userAnswers;

    /**
     * Mandatory.
     */
    private Integer delay;
}
