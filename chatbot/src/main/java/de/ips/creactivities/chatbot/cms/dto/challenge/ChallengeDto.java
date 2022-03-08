package de.ips.creactivities.chatbot.cms.dto.challenge;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.ips.creactivities.chatbot.cms.dto.AbstractListDeserializer;
import de.ips.creactivities.chatbot.cms.dto.AbstractObjectDeserializer;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.io.IOException;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChallengeDto {

    private Integer id;

    @JsonProperty("challenge_title")
    private String title;

    @JsonProperty("challenge_description")
    private String description;

    @JsonProperty("challange_evaluation_question")
    private String evaluationQuestion;

    @JsonProperty("challenge_evaluation_aspect")
    private ChallengeEvaluationAspectDto evaluationAspect;

    @URL
    @JsonProperty("challenge_image")
    private String image;

    @JsonProperty("challenge_active")
    private Boolean active;

    @JsonDeserialize(using = ChallengeSolutionDto.Deserializer.class)
    @JsonProperty("challenge_solutions")
    private List<ChallengeSolutionDto> templateSolutions;

    @JsonDeserialize(using = ChallengePrologueElement.Deserializer.class)
    @JsonProperty("challenge_prologue")
    private List<ChallengePrologueElement> challengePrologue;

    @JsonDeserialize(using = ChallengeEpilogueElement.Deserializer.class)
    @JsonProperty("challenge_epilogue")
    private List<ChallengeEpilogueElement> challengeEpilogue;

    @JsonProperty("challenge_foreign_evalutions_thx")
    private String initialThanks;

    @JsonProperty("challenge_foreign_evalutions_cta")
    private String evaluationCallToAction;

    @JsonProperty("challenge_foreign_evalutions_last_thx")
    private String lastThanks;

    @JsonDeserialize(using = EvaluationTransitionDto.Deserializer.class)
    @JsonProperty("challenge_foreign_evalutions_transitions")
    private List<EvaluationTransitionDto> transitions;

    public static class Deserializer extends AbstractListDeserializer<ChallengeDto> {

        @Override
        public List<ChallengeDto> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            return internalDeserialize(jsonParser, deserializationContext, ChallengeDto.class);
        }
    }

    public static class ObjectDeserializer extends AbstractObjectDeserializer<ChallengeDto> {
        @Override
        public ChallengeDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            return internalDeserialize(jsonParser, deserializationContext, ChallengeDto.class);
        }
    }
}
