package de.ips.creactivities.chatbot.cms.dto.level;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import de.ips.creactivities.chatbot.cms.dto.AbstractListDeserializer;
import lombok.Data;

import java.io.IOException;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LevelPrologueAnswer {

    /**
     * Mandatory.
     */
    @JsonProperty("level_prologue_answers_answer")
    private String answer;


    public static class Deserializer extends AbstractListDeserializer<LevelPrologueAnswer> {

        @Override
        public List<LevelPrologueAnswer> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            return internalDeserialize(jsonParser, deserializationContext, LevelPrologueAnswer.class);
        }
    }
}
