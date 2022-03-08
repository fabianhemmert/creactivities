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
public class LevelEpilogueElement {

    /**
     * Mandatory.
     */
    @JsonProperty("level_epilogue_text")
    private String text;

    /**
     * Optional
     */
    @JsonProperty("level_epilogue_image")
    private String image;

    /**
     * Optional user answers
     */
    @JsonDeserialize(using =  LevelEpilogueAnswer.Deserializer.class)
    @JsonProperty("level_epilogue_answers")
    private List<LevelEpilogueAnswer> userAnswers;

    @JsonProperty("level_epilogue_delay")
    private String delay;

    public static class Deserializer extends AbstractListDeserializer<LevelEpilogueElement> {

        @Override
        public List<LevelEpilogueElement> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            return internalDeserialize(jsonParser, deserializationContext, LevelEpilogueElement.class);
        }
    }
}
