package de.ips.creactivities.chatbot.cms.dto.challenge;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.ips.creactivities.chatbot.cms.dto.AbstractListDeserializer;
import lombok.Data;

import java.io.IOException;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChallengeEpilogueElement {

    /**
     * Mandatory.
     */
    @JsonProperty("challenge_epilogue_text")
    private String text;

    /**
     * Optional
     */
    @JsonProperty("challenge_epilogue_image")
    private String image;

    /**
     * Optional user answers
     */
    @JsonDeserialize(using = ChallengeEpilogueAnswer.Deserializer.class)
    @JsonProperty("challenge_epilogue_answers")
    private List<ChallengeEpilogueAnswer> userAnswers;


    @JsonProperty("challenge_epilogue_delay")
    private String delay;

    public static class Deserializer extends AbstractListDeserializer<ChallengeEpilogueElement> {

        @Override
        public List<ChallengeEpilogueElement> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            return internalDeserialize(jsonParser, deserializationContext, ChallengeEpilogueElement.class);
        }
    }

}
