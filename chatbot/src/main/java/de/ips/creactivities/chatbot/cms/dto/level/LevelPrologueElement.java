package de.ips.creactivities.chatbot.cms.dto.level;

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
public class LevelPrologueElement {

    /**
     * Mandatory.
     */
    @JsonProperty("level_prologue_text")
    private String text;

    /**
     * Optional
     */
    @JsonProperty("level_prologue_image")
    private String image;

    /**
     * Optional user answers
     */
    @JsonDeserialize(using = LevelPrologueAnswer.Deserializer.class)
    @JsonProperty("level_prologue_answers")
    private List<LevelPrologueAnswer> userAnswers;

    @JsonProperty("level_prologue_delay")
    private String delay;

    public static class Deserializer extends AbstractListDeserializer<LevelPrologueElement> {

        @Override
        public List<LevelPrologueElement> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            return internalDeserialize(jsonParser, deserializationContext, LevelPrologueElement.class);
        }
    }
}

