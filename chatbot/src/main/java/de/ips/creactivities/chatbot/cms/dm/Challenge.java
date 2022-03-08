package de.ips.creactivities.chatbot.cms.dm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Challenge {

    private String identifier;

    private String name;

    private String description;

    private Boolean active;

    private String evaluationQuestion;

    private String evaluationAspect;

    private List<TemplateSolution> templateSolutions;

    @URL
    private String image;

    private List<InteractionElement> prologue;

    private List<InteractionElement> epilogue;

    private ThirdPartyEvaluation thirdpartyEvaluation;

}

