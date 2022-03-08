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
public class ChallengePrologueElement {

    /**
     * Mandatory.
     */
    @JsonProperty("challenge_prologue_text")
    private String text;

    /**
     * Optional
      */
    @JsonProperty("challenge_prologue_image")
    private String image;

    /**
     * Optional user answers
     */
    @JsonDeserialize(using = ChallengePrologueAnswer.Deserializer.class)
    @JsonProperty("challenge_prologue_answers")
    private List<ChallengePrologueAnswer> userAnswers;

    @JsonProperty("challenge_prologue_delay")
    private String delay;

    public static class Deserializer extends AbstractListDeserializer<ChallengePrologueElement> {

        @Override
        public List<ChallengePrologueElement> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            return internalDeserialize(jsonParser, deserializationContext, ChallengePrologueElement.class);
        }
    }
}
