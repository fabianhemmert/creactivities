package de.ips.creactivities.chatbot.spiderplot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationEntry {

    /**
     * The (localized?) Name of the aspect.
     */
    private String aspect;

    /**
     * The value for the given aspect.
     */
    private double value;
}
